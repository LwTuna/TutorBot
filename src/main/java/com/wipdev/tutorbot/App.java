/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.wipdev.tutorbot;

import com.wipdev.tutorbot.database.Database;
import com.wipdev.tutorbot.database.DatabaseHandler;
import com.wipdev.tutorbot.questions.QuestionManager;
import com.wipdev.tutorbot.questions.QuestionType;
import com.wipdev.tutorbot.reviews.ReviewHandler;
import com.wipdev.tutorbot.sessions.SessionManager;
import io.javalin.Javalin;
import org.json.JSONObject;

import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class App {


    private Map<String, RequestHandler> handlers = new HashMap<>();

    private Javalin javalin;

    private SessionManager sessionManager = new SessionManager();

    private DatabaseHandler databaseHandler = new DatabaseHandler();

    private QuestionManager questionManager ;

    private ReviewHandler reviewHandler = new ReviewHandler();

    public App() {
        javalin = Javalin.create(config -> {
            config.addStaticFiles("/public");
        });

        javalin.start(8080);

        javalin.post("request", ctx -> {
            ctx.result(handleRequest(URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString()), ctx.req.getSession()));
        });
        databaseHandler.connect();
        questionManager = new QuestionManager(databaseHandler,sessionManager);

        initializeHandlers();

     }


    private void initializeHandlers() {
        handlers.put("test", (request, session) -> {
            JSONObject object = new JSONObject();
            object.put("key", "testResponse");
            object.put("message", "Hello World back !");
            return object;
        });

        handlers.put("getQuestions", questionManager::handleRequest);

        handlers.put("isLoggedIn", (request, session) -> {
            JSONObject object = new JSONObject();
            boolean isLoggedIn = sessionManager.getSessionData(session).isLoggedIn();
            object.put("loggedIn", isLoggedIn);
            return object;
        });

        handlers.put("login", ((request, session) -> {


            String username = request.getString("user");
            String password = request.getString("password");

            boolean b = sessionManager.getSessionData(session).logIn(username, password, databaseHandler);

            JSONObject response = new JSONObject();
            response.put("success", b);

            response.put("status", b ? "success" : "Log in Failed. Wrong User/Password");


            return response;
        }));

        handlers.put("getAnswersToReview",reviewHandler);

        handlers.put("sumbitAnswers", questionManager::handleAnswers);


        handlers.put("register", ((request, session) -> {


            String username = request.getString("user");
            String password = request.getString("password");

            JSONObject response = new JSONObject();

            if(databaseHandler.contains(Database.User_Data,databaseHandler.userKey,username)){
                response.put("success",false);
                response.put("message","Es gibt schon einen Nutzer mit dem Benutzernamen");
            }else{
                databaseHandler.createNewUser(username,password);
                response.put("success",true);
                response.put("message","Erfolgreich Registriert");
            }
            return response;
        }));
    }


    private String handleRequest(String decode, HttpSession session) {
        JSONObject request = new JSONObject(decode);
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("key", "error");
        if (request != null) {
            String key = request.getString("key");
            if (key != null) {
                if (handlers.containsKey(key)) {
                    return handlers.get(key).handleRequest(request, session).toString();
                } else {
                    errorResponse.put("message", "Request Handlers does not contain key=" + key);
                }
            } else {
                errorResponse.put("message", "Key can not be null");
            }
        } else {
            errorResponse.put("message", "Request is not an valid JSON Object. ->" + decode);
        }

        return errorResponse.toString();

    }


    public static void main(String[] args) {
        new App();
    }
}
