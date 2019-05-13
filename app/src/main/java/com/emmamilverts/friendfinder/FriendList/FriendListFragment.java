package com.emmamilverts.friendfinder.FriendList;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.R;

import java.util.List;

public class FriendListFragment extends Fragment {
    List<FriendDTO> friends;

    public FriendListFragment() {
        friends = new FriendDTO().preFillFriendList();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_list,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.friendRecycleView);
        FriendListAdapter listAdapter = new FriendListAdapter(friends, getContext());
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FloatingActionButton add_Button = view.findViewById(R.id.add_FAB);
        add_Button.setOnClickListener(v -> openAddFriendDialog());
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
            String userId = input.getText().toString();
            // TODO: 09-05-2019 Search for user in Firebase and send request.
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
