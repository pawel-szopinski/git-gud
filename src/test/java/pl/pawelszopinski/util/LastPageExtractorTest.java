package pl.pawelszopinski.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LastPageExtractorTest {

    @Test
    void testReturnCorrectLastPage() {
        //given
        String linkHeader = "<https://api.github.com/search/repositories?q=snake+created%3A%3E2020-03-20+language" +
                "%3Ajava+language%3Apython&per_page=100&page=2>; rel=\"next\", <https://api.github" +
                ".com/search/repositories?q=snake+created%3A%3E2020-03-20+language%3Ajava+language%3Apython&per_page" +
                "=100&page=2137>; rel=\"last\"";

        //when
        int lastPage = LastPageExtractor.getLastPage(linkHeader);

        //then
        assertEquals(2137, lastPage);
    }

    @Test
    void testNoMatchReturnZero() {
        //given
        String linkHeader = "test";

        //when
        int lastPage = LastPageExtractor.getLastPage(linkHeader);

        //then
        assertEquals(0, lastPage);
    }

    @Test
    void testInputNullReturnZero() {
        //when
        int lastPage = LastPageExtractor.getLastPage(null);

        //then
        assertEquals(0, lastPage);
    }
}
