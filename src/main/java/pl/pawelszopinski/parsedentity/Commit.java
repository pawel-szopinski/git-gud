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

    public void setSha(String sha) {
        this.sha = sha;
    }

    public Developer getAuthor() {
        return author;
    }

    public void setAuthor(Developer author) {
        this.author = author;
    }

    public Developer getCommitter() {
        return committer;
    }

    public void setCommitter(Developer committer) {
        this.committer = committer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
