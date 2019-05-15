package com.emmamilverts.friendfinder.DTO;

import java.util.Date;

public class LocationDTO {
    private String coordinates;
    private Date timeStamp;

    public LocationDTO() {
    }

    public LocationDTO(String coordinates, Date timeStamp, String place) {
        this.coordinates = coordinates;
        this.timeStamp = timeStamp;
        this.place = place;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    private String place;
}
