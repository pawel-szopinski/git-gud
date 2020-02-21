package pl.pawelszopinski.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import pl.pawelszopinski.options.ReusableHelp;
import pl.pawelszopinski.options.ReusableOwner;
import pl.pawelszopinski.options.ReusableRepository;
import pl.pawelszopinski.util.HttpRequestUtil;

import java.util.concurrent.Callable;

@Command(name = "commit-info", description = "Print info about one or more commits.")
public class GetCommitInfo implements Callable<Integer> {

    @Mixin
    private ReusableOwner owner;

    @Mixin
    private ReusableRepository repository;

    @Parameters(arity = "1..*", description = "One or more commit SHAs.")
    private String[] shaArray;

    @Mixin
    private ReusableHelp help;

    @Override
    public Integer call() throws Exception {
        System.out.println();
        System.out.println("====================================================================");
        System.out.println();

        for (String sha : shaArray) {
            HttpRequestUtil.printGet("https://api.github.com/repos/" + owner.getOwner() + "/" +
                    repository.getRepository() + "/git/commits/" + sha);

            System.out.println();
            System.out.println("====================================================================");
            System.out.println();
        }

        return 0;
    }
}