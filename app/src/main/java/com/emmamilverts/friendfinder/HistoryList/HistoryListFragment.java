package com.emmamilverts.friendfinder.HistoryList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.R;

import java.util.List;

public class HistoryListFragment extends Fragment {
    List<FriendDTO> friends;
    public HistoryListFragment() {
        friends = new FriendDTO().preFillHistoryList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_list,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.historyRecycleView);
        HistoryListAdapter listAdapter = new HistoryListAdapter(friends, getContext());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
}
