package crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CrawlerManager {
    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;
    private final Set<String> visited;
    private final AtomicInteger taskCount;
    private final String seed;
    private final String rootDomain;
    private final AtomicBoolean shutdownTriggered;
    private final int timeOutInSeconds;
    private final RobotsTxtManager robotsTxtManager;
    private final int depth;
    private final String keyword;

    public CrawlerManager(int threadCount, String seed, int timeOutInSeconds, int depth, String keyword) {
        executor = Executors.newFixedThreadPool(threadCount);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        robotsTxtManager = new RobotsTxtManager();
        visited = ConcurrentHashMap.newKeySet();
        taskCount = new AtomicInteger();
        shutdownTriggered = new AtomicBoolean(false);
        this.seed = seed;
        this.rootDomain = extractDomain(seed);
        this.timeOutInSeconds = timeOutInSeconds;
        this.depth = depth;
        this.keyword = keyword;
    }

    public void submitUrl(String url, int depth) {
        if (alreadyVisited(url)) return;
        if (!robotsTxtManager.isAllowed(url)) {
            System.out.println("Blocked by robots.txt: " + url);
            return;
        }

        taskCount.incrementAndGet();
        executor.submit(new WebCrawler(url, depth, this, depth));
    }

    public boolean alreadyVisited(String url) {
        return !visited.add(url);
    }

    public boolean validateDomain(String url) {
        return !extractDomain(url).endsWith(rootDomain);
    }

    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            return host != null ? host : "";
        } catch (URISyntaxException e) {
            return "";
        }
    }

    public Document request(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            if (!document.title().toLowerCase().contains(keyword))
                return null;

            if (connection.response().statusCode() == 200) {
                System.out.println("Visiting: " + url);
                System.out.println("Title: " + document.title());
                return document;
            }
            return null;

        } catch (IOException e) {
            return null;
        }
    }

    public void taskFinished() {
        if (taskCount.decrementAndGet() == 0 && shutdownTriggered.compareAndSet(false, true)) {
            executor.shutdown();
            System.out.println("Crawling complete. Visited " + visited.size() + " sites.");
        }

        if (shutdownTriggered.get()) scheduler.shutdownNow();
    }

    public void start() {
        taskCount.incrementAndGet();
        executor.submit(new WebCrawler(seed, 0, this, depth));

        scheduler.schedule(() -> {
            if (shutdownTriggered.compareAndSet(false, true)) {
                executor.shutdownNow();
                System.out.println("Crawling complete. Visited " + visited.size() + " sites.");
            }
        }, timeOutInSeconds, TimeUnit.SECONDS);
    }
}
