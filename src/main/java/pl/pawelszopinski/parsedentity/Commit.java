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
    public int compareTo(@Nonnull ParsedResult o) {
        if (this == o) {
            return 0;
        }

        Commit c = (Commit) o;

        return this.sha.compareToIgnoreCase(c.sha);
    }
}
