package pl.pawelszopinski.parsedentity;

import javax.annotation.Nonnull;

public class User extends ParsedResult {

    private int id;
    private String login;
    private String type;

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "User [login=" + login + ", id=" + id + ", type=" + type + "]";
    }

    @Override
    public int compareTo(@Nonnull ParsedResult o) {
        if (this == o) {
            return 0;
        }

        User u = (User) o;

        return this.login.compareToIgnoreCase(u.login);
    }
}
