package com.emmamilverts.friendfinder.FriendRequestList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestListFragment extends Fragment {
    List<FriendDTO> friends;
    DatabaseReference databaseFriendRequests;
    FirebaseAuth mAuth;

    public FriendRequestListFragment() {
        friends = new ArrayList<FriendDTO>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friendrequest_list,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.friendRequestRecycleView);
        FriendRequestListAdapter listAdapter = new FriendRequestListAdapter(friends, getContext());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAuth = FirebaseAuth.getInstance();
        databaseFriendRequests = FirebaseDatabase.getInstance().getReference().child("FriendsRequests").child(mAuth.getUid());

        databaseFriendRequests.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String friendUsername = dataSnapshot.getValue().toString();
                String friendUId = dataSnapshot.getKey();
                FriendDTO friendDTO = new FriendDTO();
                friendDTO.userId = friendUId;
                friendDTO.userName = friendUsername;
                friends.add(friendDTO);
                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                for (FriendDTO friend: friends) {
                    if(friend.userId.equals(dataSnapshot.getKey())) {
                        friends.remove(friend);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }
}
