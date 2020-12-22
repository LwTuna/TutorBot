package com.wipdev.tutorbot.reviews;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.wipdev.tutorbot.RequestHandler;
import com.wipdev.tutorbot.Settings;
import com.wipdev.tutorbot.database.DatabaseHandler;
import com.wipdev.tutorbot.sessions.SessionManager;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;
import java.util.List;

public class ReviewHandler implements RequestHandler {

    DatabaseHandler databaseHandler;
    SessionManager sessionManager;

    public ReviewHandler(DatabaseHandler databaseHandler, SessionManager sessionManager) {
        this.databaseHandler = databaseHandler;
        this.sessionManager = sessionManager;
    }

    @Override
    public JSONObject handleRequest(JSONObject request, HttpSession session) {
        JSONObject response = new JSONObject();

        List<JSONObject> answers = databaseHandler.getAnswersToQuestion(request.getInt("questionID"));
        boolean hasAnswers = answers.size() >= Settings.minAnwersToReview;
        response.put("hasAnswers",hasAnswers);
        response.put("answers",answers);
        return response;
    }

    public JSONObject handleSubmitBestAnswer(JSONObject request,HttpSession session){
        JSONObject entry = new JSONObject();
        entry.put("reviewer", sessionManager.getSessionData(session).getUserObjectID());
        entry.put("answerId",request.getString("val"));
        databaseHandler.submitReviewedAnswer(entry);
        return entry;
    }
}
