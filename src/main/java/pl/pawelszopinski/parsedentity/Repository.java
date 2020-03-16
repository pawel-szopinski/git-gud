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

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(@Nonnull ParsedResult o) {
        if (this == o) {
            return 0;
        }

        Repository r = (Repository) o;

        if (this.name.equals(r.name)) {
            return this.owner.getLogin().compareToIgnoreCase(r.owner.getLogin());
        }
        return this.name.compareToIgnoreCase(r.name);
    }

    @Override
    public String toString() {
        return "Repository [name=" + name + ", " +
                "id=" + id + ", " +
                "owner=" + owner.getLogin() + ", " +
                "description=" + description + "]";
    }
}
