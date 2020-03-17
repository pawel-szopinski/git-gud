package pl.pawelszopinski.parsedentity;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;

public class Branch extends ParsedResult {

    private String name;

    @SerializedName("protected")
    private boolean protect;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isProtect() {
        return protect;
    }

    public void setProtect(boolean protect) {
        this.protect = protect;
    }

    @Override
    public String toString() {
        return "Branch [name=" + name + ", protected=" + protect + "]";
    }

    @Override
    public int compareTo(@Nonnull ParsedResult o) {
        if (this == o) {
            return 0;
        }

        Branch b = (Branch) o;

        return this.name.compareToIgnoreCase(b.name);
    }
}
