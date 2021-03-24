package de.wip.tutorbot;

import de.wip.tutorbot.persistent.DatabaseHandler;
import de.wip.tutorbot.sessions.LoggedInUser;
import de.wip.tutorbot.sessions.Role;
import de.wip.tutorbot.sessions.SessionDataHandler;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class Server {

    private Javalin javalin;
    private final int port;

    private SessionDataHandler sessionDataHandler = new SessionDataHandler();
    private TutorBot tutorBot;
    public Server(int port) {
        this.port = port;
        javalin =  Javalin.create(config -> {
            config.addStaticFiles("/public");
        });
        javalin.start(port);

        javalin.post("isLoggedIn", this::isLoggedInHandler);
        javalin.post("login", this::login);
        javalin.post("getExercises", this::getExercises);
        javalin.post("submitExercise",this::submitExercise);
        javalin.post("getAnswers",this::getAnswers);
        javalin.post("getAnswer",this::getAnswer);
        javalin.post("vote",this::vote);
        javalin.post("createExercise",this::createExercise);
        javalin.post("getRole",this::getRole);
        javalin.post("getExercise", this::getExercise);
        javalin.post("setActiveExercises",this::setActiveExercises);
        javalin.post("activeExercises",this::activeExercises);
        javalin.post("questions",this::getQuestions);
        javalin.post("submitQAnswer",this::submitQuestionAnswer);
        javalin.post("upvoteQAnswer",this::upvoteQAnswer);
        javalin.post("downvoteQAnswer",this::downvoteQAnswer);
        javalin.post("createQuestion",this::createQuestion);

        tutorBot = new TutorBot();
    }

    public void getQuestions(Context ctx) throws UnsupportedEncodingException, SQLException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());

            ctx.result(new JSONObject().put("questions",DatabaseHandler.getQuestions()).toString());

            ctx.res.setStatus(200);
        }else{
            ctx.res.setStatus(401);
        }

    }

    public void createQuestion(Context ctx) throws UnsupportedEncodingException, SQLException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
            JSONObject request = new JSONObject(decode);
            DatabaseHandler.createQuestion(loggedInUser.getIdInDB(),request.getString("body"),request.getString("title"));
            //TODO send best Answer based on google search query
            //tutorBot.postAnAnswerToQuestion(loggedInUser.getIdInDB(),request.getString("body"),request.getString("title"));
            ctx.res.setStatus(200);
        }else{
            ctx.res.setStatus(401);
        }
    }

    public void submitQuestionAnswer(Context ctx) throws UnsupportedEncodingException, SQLException, MessagingException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
            JSONObject request = new JSONObject(decode);
            DatabaseHandler.putQAnswer(request.getInt("qid"),loggedInUser.getIdInDB(),request.getString("answer"));
            tutorBot.sendNotificationAboutNewAnswer(loggedInUser,request.getInt("qid"),request.getString("answer"));
            ctx.res.setStatus(200);
        }else{
            ctx.res.setStatus(401);
        }
    }
    public void upvoteQAnswer(Context ctx) throws UnsupportedEncodingException, SQLException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
            JSONObject request = new JSONObject(decode);
            voteQAnswer(request.getInt("ansId"),1,loggedInUser.getIdInDB());
            ctx.result(new JSONObject().put("score",DatabaseHandler.getQAScore(request.getInt("ansId"))).toString());
            ctx.res.setStatus(200);
        }else{
            ctx.res.setStatus(401);
        }
    }
    public void downvoteQAnswer(Context ctx) throws UnsupportedEncodingException, SQLException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
            JSONObject request = new JSONObject(decode);
            voteQAnswer(request.getInt("ansId"),-1,loggedInUser.getIdInDB());
            ctx.result(new JSONObject().put("score",DatabaseHandler.getQAScore(request.getInt("ansId"))).toString());
            ctx.res.setStatus(200);
        }else{
            ctx.res.setStatus(401);
        }
    }


    public void voteQAnswer(int ansId,int score,String userid) throws SQLException {
        if(DatabaseHandler.hasQVoted(ansId,userid)){
            DatabaseHandler.overrideQVote(ansId,userid,score);
        }else{
            DatabaseHandler.putQVote(ansId,userid,score);
        }
    }

    public void activeExercises(Context ctx){
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            if (loggedInUser.getRole() == Role.TUTOR || loggedInUser.getRole() == Role.PROFESSOR) {
                JSONArray all = DatabaseHandler.getAllExercises();
                JSONArray active = new JSONArray();
                JSONArray inactive = new JSONArray();
                List<Integer> acIds = tutorBot.getCurrentExerciseIds();

                for(int i=0;i<all.length();i++){
                    JSONObject current = all.getJSONObject(i);
                    if(acIds.contains(current.getInt("id"))){
                        active.put(current);
                    }else{
                        inactive.put(current);
                    }
                }
                JSONObject response = new JSONObject().put("active",active).put("inactive",inactive);
                ctx.res.setStatus(200);
                ctx.result(response.toString());

            } else {
                ctx.res.setStatus(401);
            }
        }else{
            ctx.res.setStatus(401);
        }
    }

    public void setActiveExercises(Context ctx) throws UnsupportedEncodingException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            if (loggedInUser.getRole() == Role.TUTOR || loggedInUser.getRole() == Role.PROFESSOR) {
                String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
                JSONObject request = new JSONObject(decode);
                tutorBot.setCurrentExerciseId(request.getJSONArray("exIds"));
                ctx.res.setStatus(200);
            }else{
                ctx.res.setStatus(401);
            }
        }else{
            ctx.res.setStatus(401);
        }
    }

    public void getExercise(Context ctx) throws UnsupportedEncodingException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
            JSONObject request = new JSONObject(decode);
            int exId = request.getInt("id");

            JSONObject object = DatabaseHandler.getExercise(String.valueOf(exId));
            ctx.result(object.toString());
            ctx.res.setStatus(200);
        }else{
            ctx.res.setStatus(401);
        }
    }

    public void getRole(Context ctx){
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            JSONObject response = new JSONObject();
            response.put("level",loggedInUser.getRole().getPermissionLevel());
            ctx.result(response.toString());
            ctx.res.setStatus(200);
        }else{
            ctx.res.setStatus(401);
        }
    }

    public void createExercise(Context ctx) throws UnsupportedEncodingException, SQLException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            if(loggedInUser.getRole() == Role.TUTOR || loggedInUser.getRole() == Role.PROFESSOR){
                String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
                JSONObject request = new JSONObject(decode);
                String head = request.getString("head");
                String answers = request.getString("answers");
                if(request.getInt("exid") != -1){
                    DatabaseHandler.updateExercise(head,answers,request.getInt("exid"));
                }else{
                    DatabaseHandler.createExercise(head,answers);
                }

                ctx.res.setStatus(200);
            }else{
                ctx.res.setStatus(401);
            }
        }else{
            ctx.res.setStatus(401);
        }
    }

    public void vote(Context ctx) throws UnsupportedEncodingException, SQLException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {
            String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
            JSONObject request = new JSONObject(decode);
            int answId = request.getInt("answId");
            int value = request.getBoolean("up") ? 1 : -1;

            JSONObject voteResult = new JSONObject();
            if(DatabaseHandler.hasVoted(answId,loggedInUser.getIdInDB())){
                DatabaseHandler.overrideVote(answId,loggedInUser.getIdInDB(),value);
                voteResult.put("msg","overrittenVote");
            }else{
                DatabaseHandler.putVote(answId,loggedInUser.getIdInDB(),value);
                voteResult.put("msg","putVote");
            }
            voteResult.put("score",DatabaseHandler.getScore(answId));
            ctx.result(voteResult.toString());
            ctx.res.setStatus(200);

        }else{
            ctx.res.setStatus(401);
        }
    }

    public void getAnswer(Context ctx) throws UnsupportedEncodingException, SQLException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {

            String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
            JSONObject request = new JSONObject(decode);
            int answId = request.getInt("id");

            JSONObject response = DatabaseHandler.getAnswer(answId);
            ctx.result(response.toString());
            ctx.status(200);
        }else{
            ctx.res.setStatus(401);
        }
    }
    public void getAnswers(Context ctx) throws UnsupportedEncodingException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {

            String decode = URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
            JSONObject request = new JSONObject(decode);
            int exId = Integer.parseInt(request.getString("id"));

            JSONArray answers = DatabaseHandler.getAnswers(exId);
            JSONObject response = new JSONObject().put("answers",answers);

            ctx.result(response.toString());
            ctx.status(200);
        }else{
            ctx.res.setStatus(401);
        }
    }

    public void submitExercise(Context ctx) throws UnsupportedEncodingException, SQLException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());
        if(loggedInUser != null) {

            String decode =URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
            JSONObject request = new JSONObject(decode);
            String answer=request.getString("answer");;
            String exid = request.getString("exId");
            if(DatabaseHandler.submitAnswer(loggedInUser.getIdInDB(),exid,answer)){
                removeAssignment(loggedInUser.getIdInDB(),exid);
                ctx.result(new JSONObject().put("success","true").toString());
                ctx.res.setStatus(200);
            }else{
                ctx.res.setStatus(500);
            }
        }else{
            ctx.res.setStatus(401);
        }
    }



    public void getExercises(Context ctx) throws UnsupportedEncodingException {
        LoggedInUser loggedInUser = sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId());

        String decode =URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
        JSONObject request = new JSONObject(decode);


        if(loggedInUser != null){
            ctx.res.setStatus(200);
            JSONObject response = new JSONObject();
            JSONArray exercises = request.getBoolean("all") ?DatabaseHandler.getAllExercises() :DatabaseHandler.getExercisesByAssignedUserId(loggedInUser.getIdInDB(),"false");
            response.put("exercises", exercises);

            ctx.result(response.toString());
        }else{
            ctx.res.setStatus(401);
        }

    }

    private void removeAssignment(String userId,String exId) throws SQLException {
        DatabaseHandler.removeAssignment(userId,exId);
    }

    public void isLoggedInHandler(Context ctx){
        JSONObject response = new JSONObject().put("loggedIn",String.valueOf(sessionDataHandler.isLoggedIn(ctx.req.getSession().getId())));
        ctx.result(response.toString());
    }

    public void login(Context ctx) throws UnsupportedEncodingException {
        String decode =URLDecoder.decode(ctx.queryString(), StandardCharsets.UTF_8.toString());
        JSONObject request = new JSONObject(decode);
        String username = request.getString("signInName");
        String password= request.getString("signInPass");

        LoggedInUser loggedInUser = DatabaseHandler.logIn(username,password);
        JSONObject response = new JSONObject();
        if(loggedInUser !=null){
            if(sessionDataHandler.getLoggedInUser(ctx.req.getSession().getId()) == null){
                sessionDataHandler.put(ctx.req.getSession().getId(),loggedInUser);
                response.put("success","true");
            }else{
                response.put("success","false");
                response.put("message","Already Logged In!");
            }
        }else{
            response.put("success","false");
            response.put("message","Invalid Username or Password!");
        }

        ctx.result(response.toString());
    }
}
