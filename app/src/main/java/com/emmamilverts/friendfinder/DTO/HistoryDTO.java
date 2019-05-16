package com.emmamilverts.friendfinder.DTO;

import java.util.concurrent.TimeUnit;

public class HistoryDTO {
    public String username;
    public String coordinates;
    public long timeStamp;


    public String getTime()
    {
        long currentTime = System.currentTimeMillis();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime-timeStamp);
        if (minutes > 60)
        {
            String hours = String.valueOf(TimeUnit.MILLISECONDS.toHours(currentTime-timeStamp));
            return hours + " hour(s) ago";
        }
        return String.valueOf(minutes) + " minute(s) ago";
    }

}
