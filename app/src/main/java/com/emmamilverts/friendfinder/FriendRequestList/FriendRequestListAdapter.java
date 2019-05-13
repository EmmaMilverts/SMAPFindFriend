package com.emmamilverts.friendfinder.FriendRequestList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.R;

import java.util.List;

public class FriendRequestListAdapter extends RecyclerView.Adapter {
    private List<FriendDTO> friendDTOList;
    private Context context;
    public FriendRequestListAdapter(List<FriendDTO> friendDTOList, Context context){
        this.friendDTOList = friendDTOList;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friendrequestlist_item,parent,false);
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
        public ImageView profile_ImageView;
        public TextView userName_TextView;
        public FloatingActionButton accept_FAB;
        public FloatingActionButton deny_FAB;


        public ListViewholder(@NonNull View itemView) {
            super(itemView);
            profile_ImageView = itemView.findViewById(R.id.user_imageView);
            userName_TextView = itemView.findViewById(R.id.userName_textView);
            accept_FAB = itemView.findViewById(R.id.accept_button);
            deny_FAB = itemView.findViewById(R.id.deny_button);

            accept_FAB.setOnClickListener(v -> {

            });

            deny_FAB.setOnClickListener(v -> {

            });
        }

        public void bindView(int position){
            // TODO: 09-05-2019 Add image
            userName_TextView.setText(friendDTOList.get(position).visibleName == null ? friendDTOList.get(position).userName : friendDTOList.get(position).visibleName );
        }
    }
}
