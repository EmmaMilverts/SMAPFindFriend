package com.emmamilverts.friendfinder;

//SOURCE: https://developers.google.com/maps/documentation/urls/url-encoding?fbclid=IwAR3-ySqEikj1az_iJKM_721FUePKp1MunOQli6btlU9z73FZxeemZPwX188
public class GoogleMapsEncode {

    public static String encodeString(String location){
        location = location.replace(" ", "%20");
        location = location.replace("<", "%3C");
        location = location.replace(">","%3E");
        location = location.replace("#","%23");
        return location;
    }
}
