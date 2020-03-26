package com.base22.harvestmonthlyupdate.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HarvestUser {
    private String id;

    @JsonProperty("first_name")
    private String firstName;



    @JsonProperty("last_name")
    private String lastName;

    public HarvestUser() {
    }

    public HarvestUser(int id, String firstName, String lastName) {
        this.id = Integer.toString(id);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = Integer.toString(id);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @Override
    public String toString() {
        return "HarvestUser{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
