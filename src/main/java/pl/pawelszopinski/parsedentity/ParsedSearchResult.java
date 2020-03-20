package pl.pawelszopinski.parsedentity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParsedSearchResult<T extends ParsedResult> {

    @SerializedName("total_count")
    private int totalCount;
    private List<T> items;

    public int getTotalCount() {
        return totalCount;
    }

    public List<T> getItems() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String prefix = "";
        for (T item : items) {
            sb.append(prefix);
            prefix = "\n";
            sb.append(item);
        }

        return sb.toString();
    }
}
