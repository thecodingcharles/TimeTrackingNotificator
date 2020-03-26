package com.base22.harvestmonthlyupdate.demo.Model;



public class DateInterval {

    private String from;
    private String to;

    public String getFrom() {
        return from;
    }

    public DateInterval(String from,String to) {
        this.from = from;
        this.to = to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "DateInterval{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }

}


