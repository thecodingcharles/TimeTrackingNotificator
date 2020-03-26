package com.base22.harvestmonthlyupdate.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackUser {

    public SlackUser() {
    }

    @JsonProperty("id")
    private String id;


    @JsonProperty("name")
    private String name;


    @JsonProperty("deleted")
    private boolean isActive;

    public SlackUser(String id, String name,boolean isActive) {
        this.id = id;
        this.name = name;
        this.isActive=isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "SlackUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isActive='" + isActive + '\'' +
                '}';
    }


}
