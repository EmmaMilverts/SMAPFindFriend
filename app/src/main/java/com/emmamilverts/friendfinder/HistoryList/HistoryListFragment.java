package com.emmamilverts.friendfinder.HistoryList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmamilverts.friendfinder.DTO.HistoryDTO;
import com.emmamilverts.friendfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryListFragment extends Fragment {
    DatabaseReference databaseFriends;
    FirebaseAuth mAuth;
    HistoryListAdapter listAdapter;
    List<HistoryDTO> historyList;

    public HistoryListFragment() {
        historyList = new ArrayList<HistoryDTO>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.historyRecycleView);
        listAdapter = new HistoryListAdapter(historyList, getContext());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAuth = FirebaseAuth.getInstance();
        databaseFriends = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("Friends");
        getLocationHistory();
        return view;
    }

    private void getLocationHistory() {
        databaseFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot friend : dataSnapshot.getChildren()) {
                    String friendUsername = friend.child("username").getValue().toString();
                    String friendCoordinates = friend.child("Locations").child("coordinates").getValue().toString();
                    Long friendTimeStamp = (long) friend.child("Locations").child("timeStamp").getValue();
                    HistoryDTO historyDTO = new HistoryDTO();
                    historyDTO.username = friendUsername;
                    historyDTO.coordinates = friendCoordinates;
                    historyDTO.timeStamp = friendTimeStamp;
                    historyList.add(historyDTO);
                    listAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
