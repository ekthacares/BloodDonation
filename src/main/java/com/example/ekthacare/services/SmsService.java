package com.example.ekthacare.services;

import java.util.Base64;
import org.springframework.stereotype.Service;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

@Service
public class SmsService {

    public void sendJsonSms(String mobile, String message) {
        Client client = ClientBuilder.newClient();
        String payload_string = "{"
                + "\"Text\": \"" + message + "\","
                + "\"Number\": \"91" + mobile + "\","
                + "\"SenderId\": \"EKTHAE\","
                + "\"DRNotifyUrl\": \"https://www.domainname.com/notifyurl\","
                + "\"DRNotifyHttpMethod\": \"POST\","
                + "\"Tool\": \"API\""
                + "}";
        
        String apiKey = "OSNeHNEzfPJpshh6SwPc";
        String authToken = "OUdUnRZzVgODovH2WnLJMjslU2ypjnQ06bcUvyUZ";
        String authorizationKey = apiKey + ":" + authToken;
        
        String encodedString = Base64.getEncoder().encodeToString(authorizationKey.getBytes());
        
        Entity<String> payload = Entity.json(payload_string);
        Response response = client.target("https://restapi.smscountry.com/v0.1/Accounts/OSNeHNEzfPJpshh6SwPc/SMSes/")
                .request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", "Basic " + encodedString)
                .post(payload);

        System.out.println("status: " + response.getStatus());
        System.out.println("headers: " + response.getHeaders());
        System.out.println("body:" + response.readEntity(String.class));
    }
}
