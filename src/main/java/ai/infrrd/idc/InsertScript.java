package ai.infrrd.idc;

import ai.infrrd.idc.entity.FinalizedDeconfigResponse;
import ai.infrrd.idc.entity.SuccessfulUpdation;
import ai.infrrd.idc.entity.UpdateRequestBody;
import ai.infrrd.idc.entity.UpdatedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InsertScript
{
    static List<FinalizedDeconfigResponse> deconfigResponseList = new ArrayList<>();

    private static final Logger LOGGER = LoggerFactory.getLogger( InsertScript.class );


    public static void main( String[] args ) throws IOException
    {
        JSONParser parser = new JSONParser();
        try {
            JSONArray array = (JSONArray) parser
                .parse( new InputStreamReader( InsertScript.class.getResourceAsStream( "/dummy.json" ) ) );
            for ( Object object : array ) {
                RestTemplate template = new RestTemplate( new HttpComponentsClientHttpRequestFactory() );
                LoggingRequestInterceptor loggingInterceptor = new LoggingRequestInterceptor();
                template.getInterceptors().add(loggingInterceptor);

                String json = object.toString();
                HttpHeaders reqHeaders = new HttpHeaders();
                reqHeaders.setContentType( MediaType.APPLICATION_JSON );
                HttpEntity<String> requestEntity = new HttpEntity<String>( json, reqHeaders );
                String sb = template
                    .exchange( "https://internal-dev-idc.infrrdapis.com/de/de-config-apis", HttpMethod.PATCH, requestEntity,
                        String.class ).getBody();

                System.out.println( sb.toString() );
                Gson g = new Gson();
                SuccessfulUpdation s = g.fromJson( String.valueOf( sb ), SuccessfulUpdation.class );

                //PUT call to update the DEconfigs
                if ( sb.contains( "Successfully DEConfig updated" ) ) {
                    UpdateRequestBody updateBody = mapper( s );
                    String updateString = new Gson().toJson( updateBody );
                    reqHeaders = new HttpHeaders();
                    reqHeaders.setContentType( MediaType.APPLICATION_JSON );
                    requestEntity = new HttpEntity<String>( updateString, reqHeaders );
                    String sb1 = template
                        .exchange( "https://internal-dev-idc.infrrdapis.com/de/de-config", HttpMethod.PUT, requestEntity,
                            String.class ).getBody();
                    //sending the id to finalized de config as param
                    UpdatedResponse deconfigResponse = g.fromJson( String.valueOf( sb1 ), UpdatedResponse.class );
                    if ( deconfigResponse.getMessage().contains( "Successfully DEConfig updated" ) ) {
                        callFinalizedDeConfig( updateBody.getId() );
                    } else {
                        LOGGER.info( "deconfig was finalized for the id : " + deconfigResponse.getData().getId() );
                    }
                }
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( ParseException e ) {
            e.printStackTrace();
        }

    }


    private static UpdateRequestBody mapper( SuccessfulUpdation successfulUpdation )
    {
        UpdateRequestBody updateRequestBody = new UpdateRequestBody();
        //usermodelID and customerId has to be hardcoded
        updateRequestBody.setUserModelId( "0aa4314f-8bda-4c3c-9dcc-c77533e1b841" );
        updateRequestBody.setCustomerId( "54b63893-fd6d-4f8b-ae9e-5161a6b08943" );
        updateRequestBody.setExtractionProcessType( successfulUpdation.getData().getExtractionProcessType() );
        updateRequestBody.setExtractorFieldName( successfulUpdation.getData().getExtractorFieldName() );
        updateRequestBody.setFieldName( successfulUpdation.getData().getFieldName() );
        updateRequestBody.setMicroserviceFieldConfig( successfulUpdation.getData().getMicroserviceFieldConfig() );
        updateRequestBody.setId( successfulUpdation.getData().getId() );
        System.out.println( updateRequestBody.getId() );
        return updateRequestBody;
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
            .postForEntity( "https://internal-dev-idc.infrrdapis.com/de/finalize-deconfig?id={id}", null, String.class, map ).getBody();

        FinalizedDeconfigResponse deconfigResponse = gson.fromJson( String.valueOf( sb ), FinalizedDeconfigResponse.class );
        if ( deconfigResponse.getMessage() != null && deconfigResponse.getMessage()
            .contains( "Successfully  finalize deconfig" ) ) {
            LOGGER.info( "finalizedDeConfig reponse which were success are : " + sb );
            deconfigResponseList.add( deconfigResponse );
        } else {
            LOGGER.info( "Deconfig has not been finalised" );
        }

    }
}
