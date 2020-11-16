package com.wipdev.tutorbot.questions;

import com.wipdev.tutorbot.RequestHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;

public class QuestionManager implements RequestHandler {

    private JSONObject getQuestions(){
        JSONObject object = new JSONObject();
        object.put("key","questions");


        JSONArray questionArray = new JSONArray();
        //Put questions

        JSONObject q1 = createQuestion(QuestionType.INPUT,"TestFrageInput1","Placeholder");
        JSONObject q2 = createQuestion(QuestionType.TEXT_AREA,"TestFrageTextArea2","Placeholder");
        JSONObject q3 = createQuestion(QuestionType.MULTIPLE_CHOICE,"TestMultipleChoice3","Antwort 1","Antwort 2","Antwort 3","Antwort 4");
        questionArray.put(q1);
        questionArray.put(q2);
        questionArray.put(q3);
        questionArray.put(createQuestion(QuestionType.MULTIPLE_CHOICE,"Was ist a?","a","b","c","d"));
        object.put("questionsArray", questionArray);

        return object;
    }

    private JSONObject createQuestion(QuestionType type,String questionString,String... answers){
        JSONObject question = new JSONObject();
        question.put("type",type.key);
        question.put("question",questionString);
        question.put("answers",answers);
        return question;
    }

    @Override
    public JSONObject handleRequest(JSONObject request, HttpSession session) {
        return getQuestions();
    }
}
