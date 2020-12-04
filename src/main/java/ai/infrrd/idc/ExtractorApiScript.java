package ai.infrrd.idc;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;

public class ExtractorApiScript {
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        try {
            JSONArray array = (JSONArray) parser
                    .parse(new InputStreamReader(InsertScript.class.getResourceAsStream("/extractor.json")));
            for (Object object : array) {
                RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
                LoggingRequestInterceptor loggingInterceptor = new LoggingRequestInterceptor();
                template.getInterceptors().add(loggingInterceptor);

                String json = object.toString();
                HttpHeaders reqHeaders = new HttpHeaders();
                reqHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity = new HttpEntity<String>(json, reqHeaders);
                String sb = template
                        .exchange("http://localhost:8701/de/extractor-api-config", HttpMethod.POST, requestEntity,
                                String.class).getBody();

                System.out.println(sb.toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}