package pl.pawelszopinski.entity;

public class ErrorResult extends ParsedResult {

    private String itemId;
    private int number;
    private String message;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Error [itemId=" + itemId + ", number=" + number + ", message=" + message + "]";
    }
}
