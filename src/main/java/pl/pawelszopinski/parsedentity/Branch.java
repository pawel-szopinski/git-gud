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

    public boolean isProtect() {
        return protect;
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
