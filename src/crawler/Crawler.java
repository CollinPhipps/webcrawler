package crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Crawler {

    public static void main(String[] args) {
        CrawlConfig config = new CrawlConfig();
        Map<String, String> parsedArguments = config.parseArguments(args);
        String output = parsedArguments.getOrDefault("output", "results.json");
        int duration = Integer.parseInt(parsedArguments.getOrDefault("duration", "10"));
        int threads = Integer.parseInt(parsedArguments.getOrDefault("threads", "5"));
        String startUrl = parsedArguments.getOrDefault("start-url", "https://nytimes.com");
        String keyword = parsedArguments.getOrDefault("keyword", "").toLowerCase();
        int depth = Integer.parseInt(parsedArguments.getOrDefault("depth", "2"));

        CrawlerManager manager = new CrawlerManager(threads, startUrl, duration, depth, keyword);
        manager.start();
        manager.awaitTermination();
        List<PageInfo> pages = manager.getPages();
        PageInfo.writeToFile(new File(output), pages);
    }
}
