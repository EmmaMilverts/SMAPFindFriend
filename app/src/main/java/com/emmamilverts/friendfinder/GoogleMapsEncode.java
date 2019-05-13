package com.emmamilverts.friendfinder;

public class GoogleMapsEncode {

    public static String encodeString(String location){
        location = location.replace(" ", "%20");
        location = location.replace("<", "%3C");
        location = location.replace(">","%3E");
        location = location.replace("#","%23");
        return location;
    }
}
