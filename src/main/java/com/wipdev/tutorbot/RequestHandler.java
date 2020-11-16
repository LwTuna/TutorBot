package com.wipdev.tutorbot;

import org.json.JSONObject;

import javax.servlet.http.HttpSession;

public interface RequestHandler {

    JSONObject handleRequest(JSONObject request, HttpSession session);

}
