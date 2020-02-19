package pl.pawelszopinski;

import com.cedarsoftware.util.io.JsonWriter;
import org.apache.commons.cli.*;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Java11HttpClientExample {

    // one instance, reuse
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    public static void main(String[] args) throws Exception {

        Java11HttpClientExample obj = new Java11HttpClientExample();

        Options options = CmdLineOptions.getInstance().getOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("commit-info")) {
            List<String> repo = cmd.getArgList();

            obj.sendGet(repo.get(1));
        }

        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("git", options);

//        System.out.println("Testing 2 - Send Http POST request");
//        obj.sendPost();

    }

    private void sendGet(String repository) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
//                .uri(URI.create("https://httpbin.org/get"))
//                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .uri(URI.create(
                        "https://api.github.com/repos/cschool-cinema/" + repository + "/git/commits" +
                                "/9fa8b2ad225452fc8ccb18ae76472803f3bf807c"))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String formattedBody = JsonWriter.formatJson(response.body());

        // print status code
//        System.out.println(response.statusCode());

        // print response body
        System.out.println(formattedBody);
    }

    private void sendPost() throws Exception {

        // form parameters
        Map<Object, Object> data = new HashMap<>();
        data.put("username", "abc");
        data.put("password", "123");
        data.put("custom", "secret");
        data.put("ts", System.currentTimeMillis());

        HttpRequest request = HttpRequest.newBuilder()
                .POST(buildFormDataFromMap(data))
                .uri(URI.create("https://httpbin.org/post"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // print status code
        System.out.println(response.statusCode());

        // print response body
        System.out.println(response.body());

    }

    private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        System.out.println(builder.toString());
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

}