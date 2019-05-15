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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.emmamilverts.friendfinder.DTO.FriendDTO;
import com.emmamilverts.friendfinder.MySingleton;
import com.emmamilverts.friendfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendListAdapter extends RecyclerView.Adapter {

    FirebaseAuth mAuth;
    private String mCurrentId;
    DatabaseReference databaseUsers;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String SERVER_KEY = "key=AAAA4mxHB-Y:APA91bHYQsp4uUj_6zHGj6fvqKP1OMSxkwco9tXs4gwx2aCp90ifJ7P6SEUqXIjC1XizR3JqNlluynATkYaS03ximFtn3Jg0h5VzKADb0i68pNoW3dXVh9FGm6xRpP5igjLUXDqoi-4H";
    final private String CONTENT_TYPE = "application/json";
    final String TAG = "NOTIFICATION_TAG";


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
                databaseUsers.child(mAuth.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String TOPIC = "/topics/"+ friendDTOList.get(getAdapterPosition()).userId;
                        String NOTIFICATION_MESSAGE = dataSnapshot.getValue().toString() + "has sent you a location";
                        String NOTIFICATION_TITLE = "New location has arrived!";
                        JSONObject notification = new JSONObject();
                        JSONObject notificationBody = new JSONObject();
                        try {
                            notificationBody.put("title", NOTIFICATION_TITLE);
                            notificationBody.put("message", NOTIFICATION_MESSAGE);
                            notificationBody.put("Coordinates","56.170785, 10.189453");

                            notification.put("to", TOPIC);
                            notification.put("data", notificationBody);

                            sendNotification(notification);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            });
        }

        public void bindView(int position){
            // TODO: 09-05-2019 Add image
            userName_TextView.setText(friendDTOList.get(position).visibleName == null ? friendDTOList.get(position).userName : friendDTOList.get(position).visibleName );
        }

        private void sendNotification(JSONObject notification)
        {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, "Notification sent!", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Error sending notification", Toast.LENGTH_SHORT).show();
                }
            }){
                public Map<String, String> getHeaders()throws AuthFailureError{
                    Map<String, String> parameters = new HashMap<>();
                    parameters.put("Authorization", SERVER_KEY);
                    parameters.put("Content-Type", CONTENT_TYPE);
                    return parameters;
                }
            };

            MySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
}
}