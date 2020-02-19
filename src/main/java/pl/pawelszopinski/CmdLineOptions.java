package pl.pawelszopinski;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public final class CmdLineOptions {

    private static CmdLineOptions instance;
    private static Options options;

    private CmdLineOptions() {
        options = new Options();
        buildAndAddOptions();
    }

    public static CmdLineOptions getInstance() {
        if (instance == null) {
            instance = new CmdLineOptions();
        }
        return instance;
    }

    public Options getOptions() {
        return options;
    }

    private void buildAndAddOptions() {
        Option action = Option.builder()
                .longOpt("action")
                .desc("action to be performed")
                .required()
                .build();

        Option repo = Option.builder()
                .longOpt("commit-info")
                .desc("print commit info")
                .hasArgs()
                .numberOfArgs(3)
                .argName("account-name")
                .argName("repo-name")
                .argName("commit-sha")
                .build();

        options.addOption(repo);
    }
}
