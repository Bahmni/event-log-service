package org.bahmni.module.eventlogservice.web.helper;

import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class OpenMRSWebClient {

    protected HttpClient httpClient;
    protected ConnectionDetails connectionDetails;

    public String get(URI uri) {
        String response;
        try{
           response = httpClient.get(uri);
        }
        catch(Exception e){
            throw new RuntimeException("Http request failed: "+ e);
        }
        return response;
    }

    @Autowired
    public OpenMRSWebClient(OpenMRSProperties properties) {
        connectionDetails = connectionDetails(properties);
        httpClient = new HttpClient(connectionDetails, new OpenMRSLoginAuthenticator(connectionDetails));
    }

    private ConnectionDetails connectionDetails(OpenMRSProperties properties) {
        return new ConnectionDetails(properties.getOpenMRSAuthURL(),
                properties.getOpenMRSUser(),
                properties.getOpenMRSPassword(),
                properties.getConnectionTimeoutInMilliseconds(),
                properties.getReplyTimeoutInMilliseconds());
    }
}
