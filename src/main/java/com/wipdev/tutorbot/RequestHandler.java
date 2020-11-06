package com.wipdev.tutorbot;

import org.json.JSONObject;

public interface RequestHandler {

    public JSONObject handleRequest(JSONObject request);

}
