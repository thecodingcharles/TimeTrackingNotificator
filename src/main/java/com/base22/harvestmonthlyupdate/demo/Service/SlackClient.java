package com.base22.harvestmonthlyupdate.demo.Service;
import com.base22.harvestmonthlyupdate.demo.ErrorHandler.RestTemplateResponseErrorHandler;
import com.base22.harvestmonthlyupdate.demo.Model.SlackUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class SlackClient {

    private final ObjectMapper OBJECT_MAPPER =  new ObjectMapper();
    private final RestTemplate restTemplate;
    private static String token;
    private static final Logger  logger = LogManager.getLogger();
    private static final String  postMessageURL = "https://slack.com/api/chat.postMessage";
    private static final String  getAllUsersURL = "https://slack.com/api/users.list";


    public SlackClient(RestTemplateBuilder restTemplateBuilder,@Value("${slack.token}") String slackToken) {
         restTemplate = restTemplateBuilder.errorHandler(new RestTemplateResponseErrorHandler()).build();
         token = slackToken;
    }

    public void sendNotificationToAllUsers(String notificationMessage) throws JsonProcessingException{



        SlackUser[] users = getAllUsers();
        if(users == null)
            return;

        int contActiveUsers = 0;

        UriComponentsBuilder builder = fromHttpUrl(postMessageURL);

        for (SlackUser user : users) {
            if(user.isActive()) {
                contActiveUsers++;
                JSONObject body = new JSONObject();
                body.put("channel", user.getId());
                body.put("text", notificationMessage);
                HttpEntity<String> entity = buildHttpEntity(body);
                restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
            }
        }
        logger.info("Sending notification to {} active slack users",contActiveUsers);
    }

    private SlackUser[] getAllUsers() throws JsonProcessingException {


        UriComponentsBuilder builder = fromHttpUrl(getAllUsersURL);
        HttpEntity<String> entity =  buildHttpEntity(null);
        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(),HttpMethod.GET,entity,String.class);
        JSONArray jsonObject;

        try {
             jsonObject = new JSONObject(response.getBody()).getJSONArray("members");

        }catch (JSONException e){
             logger.error("There was a problem parsing response from Slack. Message: {} ",e.toString());
             return null;
        }
        return OBJECT_MAPPER.readValue(jsonObject.toString(), SlackUser[].class);
    }


    private static HttpEntity<String> buildHttpEntity(Object messageObject){

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization",token);
            headers.set("Content-Type","application/json;charset=UTF-8");
            headers.set("User-Agent","Base22 Slack API Example");

            if(messageObject == null)
                return new HttpEntity<>(headers);
            return new HttpEntity<>(messageObject.toString(),headers);
    }
}
