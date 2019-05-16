package com.emmamilverts.friendfinder.FriendList;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.emmamilverts.friendfinder.Activities.MainActivity;
import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.DTO.LocationDTO;
import com.emmamilverts.friendfinder.R;
import com.emmamilverts.friendfinder.RequestQueueSingleton;
import com.emmamilverts.friendfinder.Services.LocationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FriendListFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1000;
    List<FriendDTO> friends;
    FirebaseAuth mAuth;
    DatabaseReference databaseUsernames;
    DatabaseReference databaseCurrentUser;
    DatabaseReference databaseFriendRequests;
    DatabaseReference databaseUsers;
    FriendListAdapter listAdapter;
    private Context mContext;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String SERVER_KEY = "key=AAAA4mxHB-Y:APA91bHYQsp4uUj_6zHGj6fvqKP1OMSxkwco9tXs4gwx2aCp90ifJ7P6SEUqXIjC1XizR3JqNlluynATkYaS03ximFtn3Jg0h5VzKADb0i68pNoW3dXVh9FGm6xRpP5igjLUXDqoi-4H";
    final private String CONTENT_TYPE = "application/json";

    LocationService mService;
    public static final String NOTIFICATION_TYPE_SEND_LOCATION = "NOTIFICATION_TYPE_SEND_LOCATION";
    public static final String NOTIFICATION_TYPE_REQUEST_LOCATION = "NOTIFICATION_TYPE_REQUEST_LOCATION";

    public FriendListFragment() {
        friends = new ArrayList<FriendDTO>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_list,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.friendRecycleView);
        listAdapter = new FriendListAdapter(friends, mContext,this);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FloatingActionButton add_Button = view.findViewById(R.id.add_FAB);
        add_Button.setOnClickListener(v -> openAddFriendDialog());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid() != null) {
            databaseUsernames = FirebaseDatabase.getInstance().getReference().child("Usernames");
            databaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
            databaseFriendRequests = FirebaseDatabase.getInstance().getReference().child("FriendsRequests");
            databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
            setUpListeners();
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        if(main.showDialogState()) {
            openAddFriendDialog();
        }
    }

    // SOURCE: https://stackoverflow.com/questions/10903754/input-text-dialog-android
    private void openAddFriendDialog(){
        MainActivity main = (MainActivity) getActivity();
        Objects.requireNonNull(main).setDialogState(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.Type_user_name));

        // Set up the input
        final EditText input = new EditText(mContext);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.Add_Friend_Button), (dialog, which) -> {
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
                                main.setDialogState(false);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if (friendUId.equals(mAuth.getUid()))
                    {
                        Toast.makeText(mContext, getString(R.string.You_cannot_add_yourself_as_a_friend), Toast.LENGTH_SHORT).show();
                    }
                    if (friendUId == null)
                    {
                        Toast.makeText(mContext, getString(R.string.User_doesnt_exist), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(mContext, getString(R.string.Database_error), Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton(getString(R.string.cancel_button), (dialog, which) -> {
            dialog.cancel();
            main.setDialogState(false);
        } );
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).registerReceiver(new LocationReceiver(), new IntentFilter(LocationService.ACTION_GET_LOCATION));
        Objects.requireNonNull(getActivity()).registerReceiver(new PermissionReceiver(),new IntentFilter(LocationService.ACTION_REQUEST_LOCATION_PERMISSION));
    }
    public LocationService getLocationService(){
        MainActivity main = (MainActivity) getActivity();
        return mService = Objects.requireNonNull(main).getLocationService();
    }
    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
                Location locationObject = arg1.getParcelableExtra(LocationService.RESULT_LOCATION_OBJECT);
                String userId = arg1.getStringExtra(LocationService.RESULT_USER_ID);

            sendLocationNotification(userId, locationObject);
        }
    }

    private class PermissionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Requests permission if not granted already
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_LOCATION);
            Toast.makeText(context, "Requesting permissions", Toast.LENGTH_SHORT).show();
        }
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
                            List<String>namesInList = new ArrayList<>();

                            for(FriendDTO friend: friends)
                            {
                                namesInList.add(friend.userName);
                            }

                            if(!namesInList.contains(dataSnapshot.getValue().toString()))
                            {
                                friendDTO.userName = dataSnapshot.getValue().toString();
                                friends.add(friendDTO);
                                listAdapter.notifyDataSetChanged();
                            }
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

    public void sendRequestNotification(String userId)
    {
        databaseCurrentUser.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String TOPIC = "/topics/"+ userId;
                String NOTIFICATION_MESSAGE = dataSnapshot.getValue().toString() + getString(R.string.wants_to_see_your_location);
                String NOTIFICATION_TITLE = getString(R.string.Location_request);
                String username = dataSnapshot.getValue().toString();
                JSONObject notification = new JSONObject();
                JSONObject notificationBody = new JSONObject();
                try {
                    notificationBody.put("title", NOTIFICATION_TITLE);
                    notificationBody.put("message", NOTIFICATION_MESSAGE);
                    notificationBody.put("notificationType", NOTIFICATION_TYPE_REQUEST_LOCATION);
                    notificationBody.put("senderid", mAuth.getUid());
                    notificationBody.put("username", username);

                    notification.put("to", TOPIC);
                    notification.put("data", notificationBody);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                            response -> Toast.makeText(mContext, getString(R.string.notification_sent), Toast.LENGTH_SHORT).show(),
                            error -> Toast.makeText(mContext, getString(R.string.Error_sending_notification), Toast.LENGTH_SHORT).show())
                    {
                        public Map<String, String> getHeaders() {
                            Map<String, String> parameters = new HashMap<>();
                            parameters.put("Authorization", SERVER_KEY);
                            parameters.put("Content-Type", CONTENT_TYPE);
                            return parameters;
                        }
                    };

                    RequestQueueSingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendLocationNotification(String userId, Location locationObject)
    {
        databaseCurrentUser.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String TOPIC = "/topics/"+ userId;
                String NOTIFICATION_MESSAGE = dataSnapshot.getValue().toString() + getString(R.string.has_sent_you_a_location);
                String NOTIFICATION_TITLE = getString(R.string.new_location_has_arrived);
                String username = dataSnapshot.getValue().toString();
                JSONObject notification = new JSONObject();
                JSONObject notificationBody = new JSONObject();
                try {
                    notificationBody.put("title", NOTIFICATION_TITLE);
                    notificationBody.put("message", NOTIFICATION_MESSAGE);
                    String coordinates = String.valueOf(locationObject.getLatitude()+ "," + locationObject.getLongitude());
                    notificationBody.put("Coordinates",coordinates);
                    notificationBody.put("notificationType", NOTIFICATION_TYPE_SEND_LOCATION);
                    notificationBody.put("senderid", mAuth.getUid());
                    notificationBody.put("username", username);


                    notification.put("to", TOPIC);
                    notification.put("data", notificationBody);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                            response -> {
                        Toast.makeText(mContext, getString(R.string.notification_sent), Toast.LENGTH_SHORT).show();
                        LocationDTO locationDTO = new LocationDTO(coordinates, System.currentTimeMillis());
                        databaseUsers.child(userId).child("Friends").child(mAuth.getUid()).child("Locations").setValue(locationDTO);
                        },
                            error -> Toast.makeText(mContext, getString(R.string.Error_sending_notification), Toast.LENGTH_SHORT).show()){
                        public Map<String, String> getHeaders() {
                            Map<String, String> parameters = new HashMap<>();
                            parameters.put("Authorization", SERVER_KEY);
                            parameters.put("Content-Type", CONTENT_TYPE);
                            return parameters;
                        }
                    };

                    RequestQueueSingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
