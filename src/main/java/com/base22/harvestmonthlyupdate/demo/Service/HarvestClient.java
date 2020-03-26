package com.base22.harvestmonthlyupdate.demo.Service;

import com.base22.harvestmonthlyupdate.demo.ErrorHandler.RestTemplateResponseErrorHandler;
import com.base22.harvestmonthlyupdate.demo.Model.DateInterval;
import com.base22.harvestmonthlyupdate.demo.Model.HarvestUser;
import com.base22.harvestmonthlyupdate.demo.Utils.Utils;
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

import java.util.*;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;


@Service
public class HarvestClient{

    private final RestTemplate restTemplate;
    private static final Logger logger = LogManager.getLogger();
    private static String token;
    private static String accountId;
    private  static String taskId;
    private  static String projectId;
    private final ObjectMapper OBJECT_MAPPER =  new ObjectMapper();
    private static final String getTimeEntriesByUserIdAndWeekURL = "https://api.harvestapp.com/v2/time_entries";
    private static final String getAllUsersURL = "https://api.harvestapp.com/api/v2/users/";
    private  static  final  String  updateTimeEntryURL = "https://api.harvestapp.com/v2/time_entries";


    public HarvestClient(RestTemplateBuilder restTemplateBuilder,@Value("${harvest.token}")
            String harvestToken,@Value("${harvest.accountId}") String harvestAccountId, @Value("${harvest.taskId}") String harvestTaskId, @Value("${harvest.projectId}") String harvestProjectId) {
            restTemplate = restTemplateBuilder.errorHandler(new RestTemplateResponseErrorHandler()).build();
            token = harvestToken;
            accountId = harvestAccountId;
            taskId = harvestTaskId;
            projectId = harvestProjectId;
    }

    public void updateAllUsersTimeEntries() throws  JsonProcessingException{


        HarvestUser[] users = getAllUsers();

        if(users == null)
            return;
        List<DateInterval> dateIntervals = Utils.getDateIntervals();


        for(HarvestUser user : users) {
            for (DateInterval date : dateIntervals) {

                logger.info("PROCESSING USER: {}",user.getFirstName()+user.getLastName());
                logger.info("PROCESSING WEEK: {}",date.toString());

              //  JSONArray timeEntries = getTimeEntriesByUserIdAndWeek(user.getId(), date.getFrom(), date.getTo());
              //  updateTimeEntriesByuser(timeEntries,user.getId(),date.getFrom(),date.getTo());

            }
        }
    }
    private JSONArray getTimeEntriesByUserIdAndWeek(String userId,String from, String to) {


        UriComponentsBuilder builder = fromHttpUrl(getTimeEntriesByUserIdAndWeekURL)
                .queryParam("user_id", userId)
                .queryParam("from", from)
                .queryParam("to", to);

        HttpEntity<String> entity = buildHttpEntity(null);

        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);


        JSONArray jsonObject;

        try {
            jsonObject = new JSONObject(response.getBody()).getJSONArray("time_entries");

        } catch (JSONException e) {
            logger.error("There was a problem parsing response from Harvest. Message: {} ", e.toString());
            return null;
        }
        return jsonObject;

    }

    private void updateTimeEntriesByuser(JSONArray timeEntries,String userId,String from, String to){


        Set<String> datesOfWeek = Utils.getDayDatesOfWeek(from, to);

        Map<String, Float> hoursByDay = new HashMap<>();
        float totalHours = 0;


        /*
         * Initialization of the map {"DATE":"HOURS"}
         * We set all dates to 0.0f hours
         * then, we update all time entries with our json object
         */
        for(String dateOfWeek: datesOfWeek){
            hoursByDay.putIfAbsent(dateOfWeek,0.0f);
        }
        for (int i = 0; i < timeEntries.length(); i++) {
            String spent_date = timeEntries.getJSONObject(i).getString("spent_date");
            float entryHours =  timeEntries.getJSONObject(i).getFloat("hours");
            hoursByDay.put(spent_date,hoursByDay.get(spent_date)+entryHours);
            totalHours+=entryHours;
        }


        for (Map.Entry<String,Float> entry : hoursByDay.entrySet()) {
            logger.info("Key = {} Value = {}", entry.getKey(), entry.getValue());
        }

        /*
         * The logic is  iterate our 7 week days in our Set
         * compare each date to the one in our Map
         * If hours are less than 8 AND user's missing
         * hours for a total of 40 hours
         * THEN update time entry with the minimum of
         *
         */
        Iterator<String> iterator = datesOfWeek.iterator();

        while (iterator.hasNext()) {

            String date = iterator.next();

            if (hoursByDay.containsKey(date) && hoursByDay.get(date) < 8.0 && totalHours < 40) {

                float hoursToAdd = Math.min(40 - totalHours, 8.0f - hoursByDay.get(date));
                totalHours += hoursToAdd;
                logger.info("ADDING:\t {} to  {} ", hoursToAdd, date);
                float updatedHours = hoursToAdd + hoursByDay.get(date);
                updateTimeEntry(userId, date, hoursToAdd);
                logger.info("UPDATED TO:\t {}", updatedHours);
                hoursByDay.put(date, updatedHours);
                iterator.remove();
            }
        }
        logger.info("DATES NON UPDATED:\t {}",datesOfWeek.toArray());
        logger.info("TOTAL WEEK HOURS:\t {}", totalHours);
    }

    private void updateTimeEntry(String userId,String spentDate,float hours){

        logger.info("USER ID\t {} ",userId);
        logger.info("SPENT DATE:\t {}", spentDate);
        logger.info("HOURS:\t {}", hours);
        logger.info(token);
        logger.info(projectId);
        logger.info(taskId);

        UriComponentsBuilder builder = fromHttpUrl(updateTimeEntryURL);

        JSONObject body = new JSONObject();
        body.put("user_id",userId);
        body.put("project_id", projectId);
        body.put("task_id",taskId);
        body.put("spent_date", spentDate);
        body.put("hours",hours);

        try {
            logger.info(OBJECT_MAPPER.writeValueAsString(body.toString()));

        } catch (Exception e){
            logger.error("hola");
        }

        HttpEntity<String> entity =  buildHttpEntity(body.toString());
        restTemplate.exchange( builder.toUriString(),HttpMethod.POST, entity,String.class);

    }


    private HarvestUser[] getAllUsers() throws JsonProcessingException {

        UriComponentsBuilder builder = fromHttpUrl(getAllUsersURL).queryParam("is_active", "true");

        HttpEntity<String> entity = buildHttpEntity(null);
        ResponseEntity<String>  response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);

        JSONArray jsonObject;
        try {
            jsonObject = new JSONObject(response.getBody()).getJSONArray("users");

        }catch (JSONException e){
            logger.error("There was a problem parsing response from Harvest. Message: {} ",e.toString());
            return null;
        }
        return OBJECT_MAPPER.readValue(jsonObject.toString(), HarvestUser[].class);
    }


    private static HttpEntity<String> buildHttpEntity(Object body){
    {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
        headers.set("Harvest-Account-ID",accountId);
        headers.set("User-Agent","Base22 Harvest API Example");
        headers.set("Content-Type","application/json;charset=UTF-8");

        if(body == null)
            return new HttpEntity<>(headers);

        return new HttpEntity<>(body.toString(),headers);
    }
}
}