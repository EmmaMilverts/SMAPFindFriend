package com.emmamilverts.friendfinder.FriendRequestList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FriendRequestListAdapter extends RecyclerView.Adapter {
    private List<FriendDTO> friendDTOList;
    private Context context;
    DatabaseReference databaseFriendRequests;
    DatabaseReference databaseUsers;
    FirebaseAuth mAuth;

    public FriendRequestListAdapter(List<FriendDTO> friendDTOList, Context context) {
        this.friendDTOList = friendDTOList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendrequestlist_item, parent, false);
        mAuth = FirebaseAuth.getInstance();
        databaseFriendRequests = FirebaseDatabase.getInstance().getReference().child("FriendsRequests").child(mAuth.getUid());
        return new ListViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewholder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return friendDTOList.size();
    }

    private class ListViewholder extends RecyclerView.ViewHolder {
        public TextView userName_TextView;
        public FloatingActionButton accept_FAB;
        public FloatingActionButton deny_FAB;


        public ListViewholder(@NonNull View itemView) {
            super(itemView);
            userName_TextView = itemView.findViewById(R.id.userName_textView);
            accept_FAB = itemView.findViewById(R.id.accept_button);
            deny_FAB = itemView.findViewById(R.id.deny_button);

            accept_FAB.setOnClickListener(v -> {
                databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                FriendDTO mySelf = new FriendDTO();
                mySelf.userId = mAuth.getUid();
                DatabaseReference myUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mySelf.userId);
                myUser.child("username").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mySelf.userName = dataSnapshot.getValue().toString();
                        databaseUsers.child(friendDTOList.get(getAdapterPosition()).userId).child("Friends").child(mySelf.userId).child("username").setValue(mySelf.userName);
                        databaseUsers.child(mySelf.userId).child("Friends").child(friendDTOList.get(getAdapterPosition()).userId).child("username").setValue(friendDTOList.get(getAdapterPosition()).userName);

                        databaseFriendRequests.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String friendIdInApp = friendDTOList.get(getAdapterPosition()).userId;

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    String uid = snapshot.getKey();
                                    if (uid.equals(friendIdInApp)) {
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            });

            deny_FAB.setOnClickListener(v -> {
                databaseFriendRequests.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String friendIdInApp = friendDTOList.get(getAdapterPosition()).userId;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String uid = snapshot.getKey();
                            if (uid.equals(friendIdInApp)) {
                                dataSnapshot.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            });
        }

        public void bindView(int position) {
            userName_TextView.setText(friendDTOList.get(position).visibleName == null ? friendDTOList.get(position).userName : friendDTOList.get(position).visibleName);
        }
    }
}
