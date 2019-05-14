package com.emmamilverts.friendfinder.FriendList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendListAdapter extends RecyclerView.Adapter {

    FirebaseAuth mAuth;
    private String mCurrentId;
    DatabaseReference databaseUsers;


    private List<FriendDTO> friendDTOList;
    private Context context;
    public FriendListAdapter(List<FriendDTO> friendDTOList, Context context){
        this.friendDTOList = friendDTOList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendlist_item,parent,false);
        mAuth = FirebaseAuth.getInstance();
        mCurrentId = FirebaseAuth.getInstance().getUid();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
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
        ImageView profile_ImageView;
        TextView userName_TextView;
        Button send_Button;
        Button request_Button;
        public ListViewholder(@NonNull View itemView) {
            super(itemView);
            profile_ImageView = itemView.findViewById(R.id.user_imageView);
            userName_TextView = itemView.findViewById(R.id.userName_textView);
            send_Button  = itemView.findViewById(R.id.sendLocationButton);
            request_Button = itemView.findViewById(R.id.requestLocationButton);


            send_Button.setOnClickListener(v -> {

                /*final String userId = mAuth.getUid(); //User id for Mads
                databaseUsers.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String myUserId = user.getUserId();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                */



                String message = "NotificationTestMessage";
                String userId = "-LepttPmmqeQqdnzK_dP";
                if (message != null) {
                    Map<String, Object> notificationMessage = new HashMap<>();
                    notificationMessage.put("message", message);
                    notificationMessage.put("from", mCurrentId);

                    databaseUsers.child(userId + "/Notification").setValue(notificationMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("SS", "Notification sent");
                        }
                    });
                }

/*
                mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                    @Override
                    public void onSuccess(GetTokenResult getTokenResult) {
                        String message = "NotificationTestMessage";
                        String userId = "-LemUHWRkloWWtzuSYqt";
                        if (message != null)
                        {
                            Map<String, Object> notificationMessage = new HashMap<>();
                            notificationMessage.put("message", message);
                            notificationMessage.put("from", mCurrentId);

                            databaseUsers.child("Users/" + userId + "/Notification").setValue(notificationMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("SS","Notification sent");
                                }
                            });
                            /*
                            mfirestore.collection("Users/" + userId + "/Notification").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("SS","Notification sent");
                                }
                            });
                            //Get username from recyclerview.
                            //Hvem der skal modtage notifikationen sættes der hvor der står 11.
                            //Get data
                            */
                /*
                        }


                        String token_id = getTokenResult.getToken();
                        String current_id = mAuth.getCurrentUser().getUid();
                        Map<String, Object> tokenMap = new HashMap<>();
                        tokenMap.put("token_id", token_id);
                        mfirestore.collection("Users").document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("ff", "Reached onSuccessListener");
                            }
                        });
                    }
                });
*/
            });

            request_Button.setOnClickListener(v -> {
                // TODO: 09-05-2019 Should be able to request location from selected user
            });
        }

        public void bindView(int position){
            // TODO: 09-05-2019 Add image
            userName_TextView.setText(friendDTOList.get(position).visibleName == null ? friendDTOList.get(position).userName : friendDTOList.get(position).visibleName );
        }
    }
}
