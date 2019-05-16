package com.emmamilverts.friendfinder.FriendList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.R;
import com.emmamilverts.friendfinder.Services.LocationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter {

    FirebaseAuth mAuth;
    DatabaseReference databaseUsers;
    private List<FriendDTO> friendDTOList;
    private Context context;
    private FriendListFragment fragment;

    public FriendListAdapter(List<FriendDTO> friendDTOList, Context context, FriendListFragment fragment){
        this.friendDTOList = friendDTOList;
        this.context = context;
        this.fragment = fragment;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendlist_item,parent,false);
        mAuth = FirebaseAuth.getInstance();
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
        TextView userName_TextView;
        Button send_Button;
        Button request_Button;
        public ListViewholder(@NonNull View itemView) {
            super(itemView);
            userName_TextView = itemView.findViewById(R.id.userName_textView);
            send_Button  = itemView.findViewById(R.id.sendLocationButton);
            request_Button = itemView.findViewById(R.id.requestLocationButton);

            send_Button.setOnClickListener(v -> {
                Log.d("TEST","TEST");
                LocationService mService = fragment.getLocationService();
                mService.getLocation(friendDTOList.get(getAdapterPosition()).userId);
            });

            request_Button.setOnClickListener(v -> fragment.sendRequestNotification(friendDTOList.get(getAdapterPosition()).userId));
        }

        public void bindView(int position){
            userName_TextView.setText(friendDTOList.get(position).userName);
        }
    }
}