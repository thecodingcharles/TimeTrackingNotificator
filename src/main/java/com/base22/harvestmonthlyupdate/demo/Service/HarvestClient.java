package com.base22.harvestmonthlyupdate.demo.Service;
import com.base22.harvestmonthlyupdate.demo.ErrorHandler.RestTemplateResponseErrorHandler;
import com.base22.harvestmonthlyupdate.demo.Model.Pair;
import com.base22.harvestmonthlyupdate.demo.Utils.HarvestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.ArrayList;
import java.util.List;



@Service
public class HarvestClient{

    private final RestTemplate restTemplate;
    private final HttpEntity<String> entity;
    private static final Logger logger = LogManager.getLogger();
    @Value("${harvest.token}")
    private static String token;
    @Autowired
    public HarvestClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.errorHandler(new RestTemplateResponseErrorHandler()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",token);
                        headers.set("Harvest-Account-ID","138568");
        headers.set("User-Agent","Base22 Harvest API Example");
        entity = new HttpEntity<>(headers);
    }

    public void patchUsersTimeEntries(){

        List<Pair<Integer,String>> users = fetchUsers();
        List<Pair<String,String>>  weeklyDateRanges = HarvestUtils.fetchWeeklyDateRanges();

        for(Pair<Integer,String> user : users) {
            for (Pair<String, String> weeklyDateRange : weeklyDateRanges) {
                float submittedHours = fetchWeeklyHoursLoggedByUser(user.getItem_1().toString(), weeklyDateRange.getItem_1(), weeklyDateRange.getItem_2());
                float missingHours = 40-submittedHours;
                if(submittedHours < 40){
                    logger.log(Level.INFO, "PROCESSING USER:\t" + user.getItem_2());
                    logger.log(Level.INFO, "WEEK:\t" + weeklyDateRange.getItem_1() + "\t" + weeklyDateRange.getItem_2());
                    logger.log(Level.INFO, "LOGGED HOURS:\t" + submittedHours);
                    logger.log(Level.INFO, "MISSING HOURS:\t" + missingHours);
                }
            }
        }
    }

    public List fetchUsers(){

        List<Pair<Integer,String>> users = new ArrayList<>();

        final String url = "https://api.harvestapp.com/api/v2/users/";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("is_active", "true");

        ResponseEntity<String>  response = restTemplate.exchange(
                                           builder.toUriString(),
                                           HttpMethod.GET,
                                           entity,
                                           String.class);

        JSONObject jsonObject = new JSONObject(response.getBody().trim());
        JSONArray rawUsers = jsonObject.getJSONArray("users");

        for(int i = 0; i < rawUsers.length(); i++){
            String username = rawUsers.getJSONObject(i).get("first_name").toString() + " " + rawUsers.getJSONObject(i).get("last_name").toString();
            users.add(new Pair(rawUsers.getJSONObject(i).getInt("id"),username));
        }
        return users;
    }


    public int fetchWeeklyHoursLoggedByUser(String userId,String from, String to) {

        final String url = "https://api.harvestapp.com/api/v2/time_entries/";
        int submittedHours = 0;

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("user_id", userId)
                .queryParam("from", from)
                .queryParam("to", to);


        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);

        JSONObject jsonObject = new JSONObject(response.getBody().trim());
        JSONArray timeEntries = jsonObject.getJSONArray("time_entries");

        for (int i = 0; i < timeEntries.length(); i++)
            submittedHours += timeEntries.getJSONObject(i).getInt("hours");

        return submittedHours;
    }

    public void patchTimeEntry(String timeEntryId,String projectId, String spentDate,String hours){

        List<Pair<Integer,String>> usersList = new ArrayList<>();
        final String url = "https://api.harvestapp.com/api/v2/time_entries/" + timeEntryId;

        UriComponentsBuilder builder = UriComponentsBuilder
                                        .fromHttpUrl(url)
                                        .queryParam("hours", hours)
                                        .queryParam("spend_date", spentDate)
                                        .queryParam("project_id", projectId);

        ResponseEntity<String>  response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);

        logger.info(response);
    }

}
