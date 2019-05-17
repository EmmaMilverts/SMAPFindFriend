package com.emmamilverts.friendfinder;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
//SOURCE: https://blog.usejournal.com/send-device-to-device-push-notifications-without-server-side-code-238611c143?fbclid=IwAR2Qz-1oUwqfbg9P_NAWzJk2oRaSfkrH2QfAgl_l8WEYX_s2DkwXg5uYLzA
public class RequestQueueSingleton {
    private static RequestQueueSingleton instance;
    private RequestQueue requestQueue;
    private Context context;

    public RequestQueueSingleton(Context context)
    {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if (requestQueue == null)
        {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized RequestQueueSingleton getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> request)
    {
        getRequestQueue().add(request);
    }
}
