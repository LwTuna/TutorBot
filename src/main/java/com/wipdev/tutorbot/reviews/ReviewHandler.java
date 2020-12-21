package com.wipdev.tutorbot.reviews;

import com.wipdev.tutorbot.RequestHandler;
import com.wipdev.tutorbot.Settings;
import com.wipdev.tutorbot.database.DatabaseHandler;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;
import java.util.List;

public class ReviewHandler implements RequestHandler {

    DatabaseHandler databaseHandler;

    public ReviewHandler(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
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
}
