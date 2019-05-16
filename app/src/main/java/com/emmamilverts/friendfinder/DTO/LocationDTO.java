package com.emmamilverts.friendfinder.DTO;

public class LocationDTO {
    private String coordinates;
    private Long timeStamp;

    public LocationDTO() {
    }

    public LocationDTO(String coordinates, Long timeStamp) {
        this.coordinates = coordinates;
        this.timeStamp = timeStamp;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
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
