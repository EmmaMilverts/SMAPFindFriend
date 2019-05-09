package com.emmamilverts.friendfinder.DTO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FriendDTO {
    public FriendDTO(){}

    public FriendDTO(String un, String vn, String imgUrl){
        this.userName = un;
        this.visibleName = vn;
        this.image = imgUrl;
    }

    public FriendDTO(String un, String vn, String imgUrl, String location, Date timeOfLocation){
        this.userName = un;
        this.visibleName = vn;
        this.image = imgUrl;
        this.location = location;
        this.timeOfLocation = timeOfLocation;
    }
    public String userId; //Used for Firebase
    public String userName;
    public String visibleName;
    public String image;
    public String location;
    public Date timeOfLocation;

    public List<FriendDTO> preFillFriendList(){
        List<FriendDTO> friends = new ArrayList<FriendDTO>();
        friends.add(new FriendDTO("Sejereje",null,"test"));
        friends.add(new FriendDTO("CoolKid69","Kurt",null));
        friends.add(new FriendDTO("69696969","Testing",null));
        friends.add(new FriendDTO("Motherfucker",null,null));
        friends.add(new FriendDTO("Whothis","Fede finn",null));
        friends.add(new FriendDTO("Batman82","Bruce Wayne",null));
        friends.add(new FriendDTO("FlashTheGorden","The Flash",null));

        return friends;
    }

    public List<FriendDTO> preFillHistoryList(){
        List<FriendDTO> friends = new ArrayList<FriendDTO>();
        Calendar test = Calendar.getInstance();
        test.set(2019,4,9,10,0);
        friends.add(new FriendDTO("Sejereje",null,"test","Afghanistan", test.getTime()));

        test.set(2019,4,9,9,0);
        friends.add(new FriendDTO("CoolKid69","Kurt",null,"Salling Rooftop",test.getTime()));

        test.set(2019,4,8,10,0);
        friends.add(new FriendDTO("69696969","Testing",null,"ASE",test.getTime()));

        test.set(2019,4,7,10,0);
        friends.add(new FriendDTO("Motherfucker",null,null,"Middle of nowhere",test.getTime()));
        return friends;
    }

    public List<FriendDTO> PopulateFriendRequests(){
        List<FriendDTO> friends = new ArrayList<FriendDTO>();
        friends.add(new FriendDTO("GustenFyr",null,null));
        friends.add(new FriendDTO("ILikeChildren",null,null));
        friends.add(new FriendDTO("LarsLøøøø",null,null));
        return friends;
    }
}
