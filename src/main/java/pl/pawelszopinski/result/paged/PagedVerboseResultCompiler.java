package pl.pawelszopinski.result.paged;

import com.cedarsoftware.util.io.JsonWriter;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.util.LastPageExtractor;

import java.net.http.HttpResponse;

public class PagedVerboseResultCompiler {

    private final ResultCompilerBasicInfo basicInfo;

    public PagedVerboseResultCompiler(ResultCompilerBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public String compileJsonResult() throws Exception {
        StringBuilder result = new StringBuilder();
        HttpResponse<String> response;
        String linkHeader;

        int pageNo = 1;

        do {
            response = basicInfo.getHttpRequest().sendGet(basicInfo.getUri() + pageNo,
                    basicInfo.isAuthenticate());

            String body = response.body();
            int statusCode = response.statusCode();

            if (statusCode != HttpStatus.SC_OK) {
                throw new HttpException("Error while fetching data from GitHub. " +
                        "Page " + pageNo + " has thrown an error: " + body);
            }

            linkHeader = response.headers().firstValue("link").orElse(null);

            result.append(JsonWriter.formatJson(body));

            pageNo++;
        } while (linkHeader != null && LastPageExtractor.getLastPage(linkHeader) >= pageNo);

        return result.toString();
    }
}
