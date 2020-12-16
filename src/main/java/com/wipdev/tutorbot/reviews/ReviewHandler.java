package com.wipdev.tutorbot.reviews;

import com.wipdev.tutorbot.RequestHandler;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;

public class ReviewHandler implements RequestHandler {
    @Override
    public JSONObject handleRequest(JSONObject request, HttpSession session) {
        JSONObject response = new JSONObject();

        boolean hasAnswers = true;
        response.put("hasAnswers",hasAnswers);
        return response;
    }
}
