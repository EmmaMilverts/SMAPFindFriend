package com.emmamilverts.friendfinder.DTO;

import java.util.List;

public class UserDTO {
    String username;
    List<FriendDTO> friends;
    String email;

    public UserDTO()
    {

    }

    public UserDTO(String userName, List<FriendDTO>friends, String email)
    {
        this.username = userName;
        this.friends = friends;
        this.email = email;
    }

    public UserDTO(String userName, String email)
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

    public List<UserDTO> setFriends(List<UserDTO> friends)
    {
        return friends;
    }

    public String getEmail() {return email;}
}

