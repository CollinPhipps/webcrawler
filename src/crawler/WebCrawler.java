package crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class WebCrawler implements Runnable {
    private final int maxDepth;
    private final String url;
    private final int depth;
    private final CrawlerManager manager;

    public WebCrawler(String url, int depth, CrawlerManager manager, int maxDepth) {
        this.url = url;
        this.depth = depth;
        this.manager = manager;
        this.maxDepth = maxDepth;
    }

    @Override
    public void run() {
        try {
            if (depth > maxDepth) return;

            Document document = manager.request(url, depth);
            if (document == null) return;

            for (Element link : document.select("a[href]")) {
                String absLink = link.absUrl("href");

                if (absLink.isEmpty() || !manager.validateDomain(absLink)) continue;

                manager.submitUrl(absLink, depth + 1);
            }
        } finally {
            manager.taskFinished();
        }
    }
}
