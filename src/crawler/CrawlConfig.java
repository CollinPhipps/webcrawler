package crawler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CrawlConfig {
    private final Set<String> flags;

    public CrawlConfig() {
        flags = new HashSet<>();
        flags.add("output");
        flags.add("duration");
        flags.add("threads");
        flags.add("keyword");
        flags.add("depth");
        flags.add("start-url");
    }

    public Map<String, String> parseArguments(String[] args) {
        Map<String, String> configs = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String argument = args[i];

            if (!argument.startsWith("--")) continue;

            String key = argument.substring(2);
            if (!flags.contains(key)) {
                throw new IllegalArgumentException("Unknown argument " + key);
            }

            String value = args[i + 1];
            i++;
            configs.put(key, value);

        }
        return configs;
    }
}
