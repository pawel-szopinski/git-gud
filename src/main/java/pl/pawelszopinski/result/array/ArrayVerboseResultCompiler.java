package pl.pawelszopinski.result.array;

import com.cedarsoftware.util.io.JsonWriter;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;

import java.net.http.HttpResponse;

public class ArrayVerboseResultCompiler {

    private final ResultCompilerBasicInfo basicInfo;
    private final String[] items;
    private final String uriItemReplacement;

    public ArrayVerboseResultCompiler(ResultCompilerBasicInfo basicInfo, String[] items,
                                      String uriItemReplacement) {
        this.basicInfo = basicInfo;
        this.items = items;
        this.uriItemReplacement = uriItemReplacement;
    }

    public String compileJsonResult() throws Exception {
        StringBuilder result = new StringBuilder();

        for (String item : items) {
            result.append(System.getProperty("line.separator"))
                    .append(System.getProperty("line.separator"))
                    .append("item: ").append(item)
                    .append(System.getProperty("line.separator"))
                    .append(getSingleItemResult(item));
        }

        return result.toString().trim();
    }

    private String getSingleItemResult(String item) throws Exception {
        String finalUri = basicInfo.getUri().replace(uriItemReplacement, item);

        HttpResponse<String> response =
                basicInfo.getHttpRequest().sendGet(finalUri, basicInfo.isAuthenticate());

        return JsonWriter.formatJson(response.body());
    }
}
