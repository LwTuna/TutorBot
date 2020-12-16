package com.wipdev.tutorbot.questions;

import com.wipdev.tutorbot.RequestHandler;
import com.wipdev.tutorbot.database.DatabaseHandler;
import com.wipdev.tutorbot.sessions.SessionData;
import com.wipdev.tutorbot.sessions.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;

public class QuestionManager implements RequestHandler {

    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;

    public QuestionManager(DatabaseHandler databaseHandler, SessionManager sessionManager) {
        this.databaseHandler = databaseHandler;
        this.sessionManager = sessionManager;
    }

    private JSONObject getQuestions(SessionData sessionData) {
        return getQuestionsForUser(sessionData);
    }

    public JSONObject createQuestion(QuestionType type, String questionString, String... answers) {
        JSONObject question = new JSONObject();
        question.put("type", type.key);
        question.put("question", questionString);
        question.put("answers", answers);
        return question;
    }

    private JSONObject getQuestionsForUser(SessionData data){
        JSONObject res = new JSONObject();
        res.put("key", "questions");

        JSONArray questionArray = new JSONArray();

        //TODO Change to select for User id
        for (JSONObject object:databaseHandler.getAllQuestions()) {
            questionArray.put(object);
        }
        res.put("questionsArray", questionArray);
        return res;
    }

    @Override
    public JSONObject handleRequest(JSONObject request, HttpSession session) {
        return getQuestions(sessionManager.getSessionData(session));
    }

    public JSONObject handleAnswers(JSONObject request, HttpSession session) {
        JSONObject response = new JSONObject();
        response.put("success",true);
        JSONArray array = request.getJSONArray("answers");
        for(int i=0;i<array.length();i++){
            int id = array.getJSONObject(i).getInt("index");
            String answer = array.getJSONObject(i).getString("answer");
            databaseHandler.putAnswer(id,answer,sessionManager.getSessionData(session).getUserObjectID());
        }


        return response;
    }
}
