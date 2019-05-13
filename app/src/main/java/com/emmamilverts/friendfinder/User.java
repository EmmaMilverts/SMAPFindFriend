package com.emmamilverts.friendfinder;

import java.util.List;

public class User {
    String userId;
    String userName;
    List<User> friends;

    public User()
    {

    }

    public User(String userId, String userName, List<User>friends)
    {
        this.userId = userId;
        this.userName = userName;
        this.friends = friends;
    }

    public User(String userId, String userName)
    {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public List<User> getFriends() {return friends;}

    public String setUserName(String userName)
    {
        return userName;
    }

    public List<User> serFriends(List<User> friends)
    {
        return friends;
    }
}

