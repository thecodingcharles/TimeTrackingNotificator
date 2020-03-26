package com.base22.harvestmonthlyupdate.demo;


import com.base22.harvestmonthlyupdate.demo.Service.HarvestClient;
import com.base22.harvestmonthlyupdate.demo.Service.SlackClient;
import com.base22.harvestmonthlyupdate.demo.Utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger();
	private HarvestClient harvestClient;
	private  SlackClient slackClient;


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	public DemoApplication(HarvestClient harvestClient, SlackClient slackClient){
		this.harvestClient = harvestClient;
		this.slackClient = slackClient;
	}

	@Override public void run(String... args) throws  JsonProcessingException{

		logger.info("STARTED");
		harvestClient.updateAllUsersTimeEntries();
		slackClient.sendNotificationToAllUsers(Utils.getSubmitYourTimesheetMessage());
		logger.info("FINISHED");

		/*
		if(args.length != 1)
			throw  new RuntimeException();

		if(args[0].equals("1")){
				slackClient.sendNotificationToAllUsers(Utils.getLogYourTimesheetMessage());

		}else{
				harvestClient.updateAllUsersTimeEntries();
				slackClient.sendNotificationToAllUsers(Utils.getSubmitYourTimesheetMessage());
		}
		logger.info("FINISHED");
		*/

	}
}
