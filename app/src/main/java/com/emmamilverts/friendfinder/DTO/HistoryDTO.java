package com.emmamilverts.friendfinder.DTO;

public class HistoryDTO {
    public String username;
    public String coordinates;
    public long timeStamp;


    public Long getTime()
    {
        long currentTime = System.currentTimeMillis();
        return currentTime-timeStamp;
    }

}
