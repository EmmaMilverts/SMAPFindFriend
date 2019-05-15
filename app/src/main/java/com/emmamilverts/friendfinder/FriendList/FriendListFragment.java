package com.emmamilverts.friendfinder.FriendList;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends Fragment {
    List<FriendDTO> friends;
    FirebaseAuth mAuth;
    private String mCurrentId;
    DatabaseReference databaseUsernames;
    DatabaseReference databaseCurrentUser;
    DatabaseReference databaseFriendRequests;
    FriendListAdapter listAdapter;

    public FriendListFragment() {
        friends = new ArrayList<FriendDTO>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_list,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.friendRecycleView);
        listAdapter = new FriendListAdapter(friends, getContext());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FloatingActionButton add_Button = view.findViewById(R.id.add_FAB);
        add_Button.setOnClickListener(v -> openAddFriendDialog());
        mAuth = FirebaseAuth.getInstance();
        mCurrentId = FirebaseAuth.getInstance().getUid();
        databaseUsernames = FirebaseDatabase.getInstance().getReference().child("Usernames");
        databaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        databaseFriendRequests = FirebaseDatabase.getInstance().getReference().child("FriendsRequests");
        setUpListeners();
        return view;
    }

    // SOURCE: https://stackoverflow.com/questions/10903754/input-text-dialog-android
    private void openAddFriendDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Type user name");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {

            databaseUsernames.child(input.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String friendUId = dataSnapshot.getValue().toString();
                    if (friendUId != null && friendUId != mAuth.getUid())
                    {
                        databaseCurrentUser.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                databaseFriendRequests.child(friendUId).child(mAuth.getUid()).setValue(dataSnapshot.getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if (friendUId.equals(mAuth.getUid()))
                    {
                        Toast.makeText(getContext(), "You cannot add yourself as a friend", Toast.LENGTH_SHORT).show();
                    }
                    if (friendUId == null)
                    {
                        Toast.makeText(getContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void setUpListeners()
    {
        databaseCurrentUser.child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot currentdata : dataSnapshot.getChildren())
                {
                    FriendDTO friendDTO = new FriendDTO();
                    friendDTO.userId = currentdata.getKey();
                    databaseCurrentUser.child("Friends").child(friendDTO.userId).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            friendDTO.userName = dataSnapshot.getValue().toString();
                            friends.add(friendDTO);
                            listAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
