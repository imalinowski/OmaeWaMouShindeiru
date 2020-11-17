package com.example.appchat;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    private String from="";
    private final String time;
    private final String message;

    Message(JSONObject json) throws JSONException {
        if(json.has("nick")) from = String.valueOf(json.get("nick"));
        time = ((json.get("hour")+":"+json.get("minute")));
        message = String.valueOf(json.get("message"));
    }

    String getTime(){return time;}
    String getName(){return from;}
    String getMessage(){return message;}
}
