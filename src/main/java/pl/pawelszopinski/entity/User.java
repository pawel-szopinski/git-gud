package pl.pawelszopinski.entity;

public class User extends ParsedResult implements Comparable<User> {

    private int id;
    private String login;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "User [login=" + login + ", id=" + id + ", type=" + type + "]";
    }

    @Override
    public int compareTo(User o) {
        if (this == o) {
            return 0;
        }

        return this.login.compareToIgnoreCase(o.login);
    }
}
