package ai.infrrd.idc;


import ai.infrrd.idc.entity.DeConfigResponse;
import ai.infrrd.idc.entity.FinalizedDeconfigResponse;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class PostCode {
    private static final Logger LOGGER = LoggerFactory.getLogger( InsertScript.class );


    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        Gson gson = new Gson();
        try {
            JSONArray array = (JSONArray) parser
                    .parse(new InputStreamReader(InsertScript.class.getResourceAsStream("/deconfig.json")));
            for (Object object : array) {
                RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
                LoggingRequestInterceptor loggingInterceptor = new LoggingRequestInterceptor();
                template.getInterceptors().add(loggingInterceptor);

                String json = object.toString();
                HttpHeaders reqHeaders = new HttpHeaders();
                reqHeaders.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity = new HttpEntity<String>(json, reqHeaders);
                String sb = template
                        .exchange("http://localhost:8701/de/de-config/", HttpMethod.POST, requestEntity,
                                String.class).getBody();

                System.out.println(sb.toString());
                DeConfigResponse deconfigresponse = gson.fromJson( String.valueOf( sb ),  DeConfigResponse.class );
                Thread.sleep(3000);
                if ( deconfigresponse.getMessage().contains("Successfully DEConfig created") ){
                    callFinalizedDeConfig( deconfigresponse.getData() );
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //finalized deconfig
    private static void callFinalizedDeConfig( String id )
    {
        Gson gson = new Gson();
        RestTemplate template = new RestTemplate( new HttpComponentsClientHttpRequestFactory() );

        //set interceptors/requestFactory
        LoggingRequestInterceptor loggingInterceptor = new LoggingRequestInterceptor();
        template.getInterceptors().add(loggingInterceptor);
        HashMap map = new HashMap();
        map.put( "id", id );

        Object sb =  template
                .postForEntity( "http://localhost:8701/de/finalize-deconfig?id={id}", null, String.class, map ).getBody();

        FinalizedDeconfigResponse deconfigResponse = gson.fromJson( String.valueOf( sb ), FinalizedDeconfigResponse.class );
        if ( deconfigResponse.getMessage() != null && deconfigResponse.getMessage()
                .contains( "Successfully  finalize deconfig" ) ) {
            LOGGER.info( "finalizedDeConfig reponse which were success are : " + sb );
        } else {
            LOGGER.info( "Deconfig has not been finalised" );
        }

    }
}