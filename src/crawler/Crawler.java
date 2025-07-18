package crawler;

import java.util.Map;

public class Crawler {
    public static void main(String[] args) {
//        int coreCount = Runtime.getRuntime().availableProcessors();
//        String seed = "https://nytimes.com";
//        int timeOutSeconds = 30;
//        CrawlerManager manager = new CrawlerManager(coreCount, seed, timeOutSeconds);
//        manager.start();

        CrawlConfig config = new CrawlConfig();
        Map<String, String> parsedArguments = config.parseArguments(args);
        String output = parsedArguments.getOrDefault("output", "results.json");
        int duration = Integer.parseInt(parsedArguments.getOrDefault("duration", "10"));
        int threads = Integer.parseInt(parsedArguments.getOrDefault("threads", "5"));
        String startUrl = parsedArguments.getOrDefault("start-url", "https://example.com");
        String keyword = parsedArguments.getOrDefault("keyword", "");
        int depth = Integer.parseInt(parsedArguments.getOrDefault("depth", "2"));

        System.out.println(output);
        System.out.println(duration);
        System.out.println(threads);
        System.out.println(startUrl);
        System.out.println(keyword);
        System.out.println(depth);

        CrawlerManager manager = new CrawlerManager(threads, startUrl, duration, depth, keyword);
        manager.start();
    }
}
