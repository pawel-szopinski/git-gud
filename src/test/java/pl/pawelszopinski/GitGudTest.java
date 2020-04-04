package pl.pawelszopinski;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GitGudTest {

    @Test
    void testAppVersionReturnedCorrectly() {
        //when
        CommandLine cmd = new CommandLine(new GitGud());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        int exitCode = cmd.execute("-V");

        //then
        assertEquals(0, exitCode);
        assertEquals("Version: 1.0", StringUtils.chomp(sw.toString()));
    }
}