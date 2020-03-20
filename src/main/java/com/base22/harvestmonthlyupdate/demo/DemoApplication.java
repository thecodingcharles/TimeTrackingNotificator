package com.base22.harvestmonthlyupdate.demo;

import com.base22.harvestmonthlyupdate.demo.Service.HarvestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.CharBuffer;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	@Autowired
	HarvestClient harvestClient;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override public void run(String... arg0){
		System.out.println("S T A R T E D");
		harvestClient.patchUsersTimeEntries();
	}



}
