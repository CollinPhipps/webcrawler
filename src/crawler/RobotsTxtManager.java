package crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RobotsTxtManager {
    private final Map<String, List<String>> disallowMap = new ConcurrentHashMap<>();

    public boolean isAllowed(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost();
            String path = u.getPath();

            List<String> disallowList = disallowMap.computeIfAbsent(host, h -> fetchDisallowedPaths(h, u.getProtocol()));
            for (String disallowed : disallowList)
                if (path.startsWith(disallowed)) return false;
        } catch (Exception _) {
            return true;
        }
        return true;
    }

    private List<String> fetchDisallowedPaths(String host, String protocol) {
        List<String> disallowList = new ArrayList<>();
        String robotsURL = protocol + "://" + host + "/robots.xt";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(robotsURL).openStream()))) {
            String line;
            boolean userAgentSectionApplies = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("User-agent:")) {
                    String agent = line.substring("User-agent:".length()).trim();
                    userAgentSectionApplies = agent.equals("*");
                } else if (userAgentSectionApplies && line.startsWith("Disallow:")) {
                    String path = line.substring("Disallow:".length()).trim();
                    if (!path.isEmpty()) {
                        disallowList.add(path);
                    }
                }
            }
        } catch (Exception _) {}
        return disallowList;
    }
}
