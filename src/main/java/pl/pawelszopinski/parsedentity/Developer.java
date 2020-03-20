package pl.pawelszopinski.parsedentity;

public class Developer {

    private String name;
    private String email;
    private String date;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "[" +
                "name=" + name +
                ", email=" + email +
                ", date=" + date + "]";
    }
}
