package com.emmamilverts.friendfinder.FriendList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.LocationService;
import com.emmamilverts.friendfinder.R;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter {
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
                // TODO: 09-05-2019 Should be able to send location to selected user
               LocationService mService = fragment.getLocationService();
               mService.getLocation();
            });

            request_Button.setOnClickListener(v -> {
                // TODO: 09-05-2019 Should be able to request location from selected user
                LocationService mService = fragment.getLocationService();
            });
        }

        public void bindView(int position){
            // TODO: 09-05-2019 Add image
            userName_TextView.setText(friendDTOList.get(position).visibleName == null ? friendDTOList.get(position).userName : friendDTOList.get(position).visibleName );
        }
    }
}
