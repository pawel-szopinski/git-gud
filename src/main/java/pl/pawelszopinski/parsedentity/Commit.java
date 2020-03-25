package pl.pawelszopinski.parsedentity;

import javax.annotation.Nonnull;

public class Commit extends ParsedResult {

    private String sha;
    private Developer author;
    private Developer committer;
    private String message;

    public String getSha() {
        return sha;
    }

    public Developer getAuthor() {
        return author;
    }

    public Developer getCommitter() {
        return committer;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Commit [" +
                "sha=" + sha +
                ", author=" + author +
                ", committer=" + committer +
                ", message=" + message + "]";
    }

    @Override
    public int compareTo(@Nonnull ParsedResult parsedResult) {
        if (this == parsedResult) {
            return 0;
        }

        Commit commit = (Commit) parsedResult;

        return this.sha.compareToIgnoreCase(commit.sha);
    }
}
