package pl.pawelszopinski.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LastPageExtractor {

    private static final Pattern LAST_PAGE_PATTERN =
            Pattern.compile("page=\\d+>; rel=\"last\"");

    public static int getLastPage(String linkHeader) {
        if (linkHeader == null) {
            return 0;
        }

        Matcher m = LAST_PAGE_PATTERN.matcher(linkHeader);

        if (!m.find()) {
            return 0;
        }

        return Integer.parseInt(m.group().replaceAll("[^\\d]", ""));
    }
}
