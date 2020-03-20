package pl.pawelszopinski.parsedentity;

import javax.annotation.Nonnull;

public class ErrorResult extends ParsedResult {

    private String item;
    private int number;
    private String message;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Error [item=" + item + ", number=" + number + ", message=" + message + "]";
    }

    @Override
    public int compareTo(@Nonnull ParsedResult o) {
        if (this == o) {
            return 0;
        }

        ErrorResult e = (ErrorResult) o;

        return this.item.compareToIgnoreCase(e.item);
    }
}
