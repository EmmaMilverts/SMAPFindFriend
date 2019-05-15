package com.emmamilverts.friendfinder;

import com.emmamilverts.friendfinder.DTO.FriendDTO;

import java.util.List;

public class User {
    String username;
    List<FriendDTO> friends;
    String email;

    public User()
    {

    }

    public User(String userName, List<FriendDTO>friends, String email)
    {
        this.username = userName;
        this.friends = friends;
        this.email = email;
    }

    public User(String userName, String email)
    {
        this.username = userName;
        this.email = email;
    }

    public String getUsername()
    {
        return username;
    }

    public List<FriendDTO> getFriends() {return friends;}

    public String setUserName(String userName)
    {
        return userName;
    }

    public List<User> setFriends(List<User> friends)
    {
        return friends;
    }

    public String getEmail() {return email;}
}

