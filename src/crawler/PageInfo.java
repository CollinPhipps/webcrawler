package crawler;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class PageInfo {
    private final String url;
    private final String title;

    public PageInfo(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public static void writeToFile(File file, List<PageInfo> sites) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("[\n");

            for (int i = 0; i < sites.size(); i++) {
                writer.write(sites.get(i).formatPageInfo(i == sites.size() - 1));
            }

            writer.write("]\n");

        } catch (Exception _) {}
    }

    private String formatPageInfo(boolean lastItem) {
        String result = "\t{\n"
                + "\t\t\"url\": \"" + url + "\",\n"
                + "\t\t\"title\": \"" + title + "\"\n"
                + "\t}";
        if (!lastItem) result += ",\n";
        else result += "\n";
        return result;
    }
}