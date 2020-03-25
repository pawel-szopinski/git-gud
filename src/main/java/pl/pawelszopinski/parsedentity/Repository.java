package pl.pawelszopinski.parsedentity;

import javax.annotation.Nonnull;

public class Repository extends ParsedResult {

    private String id;
    private String name;
    private User owner;
    private String description;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(@Nonnull ParsedResult parsedResult) {
        if (this == parsedResult) {
            return 0;
        }

        Repository repository = (Repository) parsedResult;

        if (this.name.equals(repository.name)) {
            return this.owner.getLogin().compareToIgnoreCase(repository.owner.getLogin());
        }
        return this.name.compareToIgnoreCase(repository.name);
    }

    @Override
    public String toString() {
        return "Repository [name=" + name + ", " +
                "id=" + id + ", " +
                "owner=" + owner.getLogin() + ", " +
                "description=" + description + "]";
    }
}
