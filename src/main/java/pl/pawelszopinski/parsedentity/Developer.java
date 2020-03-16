package pl.pawelszopinski.parsedentity;

public class Developer {

    private String name;
    private String email;
    private String date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "[" +
                "name=" + name +
                ", email=" + email +
                ", date=" + date + "]";
    }
}
