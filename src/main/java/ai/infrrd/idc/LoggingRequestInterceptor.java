package ai.infrrd.idc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;


public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor
{

    final static Logger log = LoggerFactory.getLogger( LoggingRequestInterceptor.class );


    @Override public ClientHttpResponse intercept( HttpRequest request, byte[] body, ClientHttpRequestExecution execution )
        throws IOException
    {
        traceRequest( request, body );
        return execution.execute( request, body );
    }


    private void traceRequest( HttpRequest request, byte[] body ) throws IOException
    {
        System.out.println( "\n\n===========================request begin================================================" );
        System.out.println( "URI         : " + request.getURI() );
        System.out.println( "Method      : " + request.getMethod() );
        System.out.println( "Headers     : " + request.getHeaders() );
        System.out.println( "Request body: " + new String( body, "UTF-8" ) );
        System.out.println( "\n\n==========================request end================================================" );
    }


}