package de.wip.tutorbot.persistent;

import de.wip.tutorbot.sessions.LoggedInUser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseHandler {


    private static Connection connection;

    private static final String dbName="tutorbot",username="root",password="";

    public static void connect() throws ClassNotFoundException, SQLException {
        connection = DriverManager.getConnection(String.format("jdbc:mysql://localhost:3306/%S",dbName),username,password);
    }


    private static JSONArray select(String table,String afterSelect){
        JSONArray fetch= new JSONArray();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(String.format("SELECT * from %S "+afterSelect,table));
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            while(rs.next()){
                JSONObject row = new JSONObject();
                for(int i=1;i<=resultSetMetaData.getColumnCount();i++){
                    String columnName = resultSetMetaData.getColumnName(i);
                    row.put(columnName,rs.getObject(i).toString());
                }
                fetch.put(row);
            }
            return fetch;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            return fetch;
        }
    }

    private static int insert(String table,JSONObject obj) throws SQLException {
        Statement statement = connection.createStatement();
        String queryString=String.format("INSERT INTO %S (", table);
        for(String key : obj.keySet()){
            queryString += key+",";
        }
        queryString = queryString.substring(0,queryString.length()-1) +") VALUES(";
        for(String key : obj.keySet()){
            queryString += "\""+obj.get(key).toString().replaceAll("\"","\\\\\"")+"\""+",";
        }
        queryString = queryString.substring(0,queryString.length()-1) +")";
        return statement.executeUpdate(queryString);

    }

    private static int delete(String table,String condition) throws SQLException {
        Statement statement = connection.createStatement();
        String queryString = String.format("DELETE FROM %S %S",table,condition );
        return statement.executeUpdate(queryString);
    }

    public static LoggedInUser logIn(String username,String password){
        JSONArray result = select("account","WHERE username='"+username+"' AND password='"+password+"';");
        if(result.length() >=1){
            JSONObject object = result.getJSONObject(0);
            return new LoggedInUser(object.getString("id"),object.getString("username"),object.getInt("role"),object.getString("realname"));
        }
        return null;
    }

    public static JSONArray getExercisesByAssignedUserId(String userid,String completed){
        JSONArray jsonArray =  select("exercise","JOIN assigned_exercises ON assigned_exercises.exercise_id = exercise.id " +
                "WHERE assigned_exercises.account_id = '"+userid+"' AND assigned_exercises.completed = "+completed+";");
        return jsonArray;
    }

    public static boolean submitAnswer(String accountId,String exerciseId,String answer){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("account_id",accountId);
        jsonObject.put("exercise_id",exerciseId);
        jsonObject.put("answer",answer);
        jsonObject.put("lastChanged", getCurrentDate());
        try {
            insert("answer",jsonObject);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getCurrentDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static void removeAssignment(String userId, String exId) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(String.format(
                "UPDATE assigned_exercises SET completed = true WHERE account_id = %S and exercise_id = %S",
                userId,exId));
    }

    public static void assignExercise(String userId,String exId,String due_date) throws SQLException {
        JSONObject object = new JSONObject().put("account_id",userId).put("exercise_id",exId)
                .put("completed","false").put("due_date",due_date);
        insert("assigned_exercises",object);
    }

    public static void createExercise(String head,String answers) throws SQLException {
        JSONObject object = new JSONObject().put("head",head).put("answers",answers);
        insert("exercise",object);
    }

    public static JSONArray getAllExercises() {
        return select("exercise",";");
    }

    public static JSONArray getAnswers(int id) {
        JSONArray fetch = new JSONArray();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(
                    "SELECT ans.id, ex.head, ans.answer,ans.lastChanged,acc.realname from answer ans " +
                            "JOIN exercise ex ON ans.exercise_id = ex.id JOIN account acc ON acc.id = ans.account_id " +
                            "WHERE ans.exercise_id = "+id+"; ");
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            while(rs.next()){
                JSONObject row = new JSONObject();
                for(int i=1;i<=resultSetMetaData.getColumnCount();i++){
                    String columnName = resultSetMetaData.getColumnName(i);
                    row.put(columnName,rs.getObject(i).toString());
                }
                fetch.put(row);
            }


            for(int i=0;i<fetch.length();i++){
                fetch.getJSONObject(i).put("score",getScore(fetch.getJSONObject(i).getInt("id")));
            }
            return fetch;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static JSONObject getAnswer(int answId) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(
                "SELECT ans.id, ex.head,ans.exercise_id, ans.answer,ans.lastChanged,acc.realname from answer ans" +
                        " JOIN exercise ex ON ans.exercise_id = ex.id JOIN account acc ON acc.id = ans.account_id " +
                        "WHERE ans.id = "+answId+"; ");
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        JSONArray fetch = new JSONArray();
        while(rs.next()){
            JSONObject row = new JSONObject();
            for(int i=1;i<=resultSetMetaData.getColumnCount();i++){
                String columnName = resultSetMetaData.getColumnName(i);
                row.put(columnName,rs.getObject(i).toString());
            }
            fetch.put(row);
        }
        return fetch.getJSONObject(0);
    }

    public static boolean hasVoted(int answId, String idInDB) {
        return select("vote",String.format("WHERE answer_id = %S AND account_id = %S",answId,idInDB) ).length() > 0;
    }


    public static void overrideVote(int answId, String idInDB, int value) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(String.format("UPDATE vote SET score = %S WHERE account_id = %S AND answer_id = %S",
                value,idInDB,answId));
    }

    public static void putVote(int answId, String idInDB, int value) throws SQLException {
        JSONObject object = new JSONObject().put("answer_id",answId).put("account_id",idInDB).put("score",value);
        insert("vote",object);
    }

    public static int getScore(int answId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(String.format("SELECT sum(score) from vote WHERE answer_id = %s;",answId));
        ResultSet rs = preparedStatement.executeQuery();
        if(rs.next()){
            String sum = rs.getString(1);
            try{
                return Integer.parseInt(sum);
            }catch (Exception e){
                return 0;
            }
        }else {
            return 0;
        }
    }

    public static void updateExercise(String head, String answers, int exid) throws SQLException {
        Statement statement = connection.createStatement();
        head = head.replaceAll("\"","\\\\\"");
        answers = answers.replaceAll("\"","\\\\\"");
        statement.executeUpdate(String.format("UPDATE exercise SET head = '%s' , answers = '%s' WHERE id = %S ;",
                head,answers,exid));
    }

    public static JSONObject getExercise(String exId) {
        return select("exercise",String.format("WHERE id = %S ;", exId)).getJSONObject(0);
    }


    public static JSONArray getUsersWhichHaventDoneEx(int permissionLevel,int exId) {
        JSONArray allUsers = select("account",String.format("WHERE role = %S;",permissionLevel));
        JSONArray usersWhichHaventDoneExercise = new JSONArray();
        for(int i=0;i<allUsers.length();i++){
            JSONArray selRes = select("assigned_exercises",
                    String.format("WHERE account_id = %S AND exercise_id = %S",
                    allUsers.getJSONObject(i).getString("id"),String.valueOf(exId)));
            if(selRes.isEmpty()){
                usersWhichHaventDoneExercise.put(allUsers.getJSONObject(i));
            }
        }
        return usersWhichHaventDoneExercise;
        //return select("account",String.format("JOIN assigned_exercises ON account.id = assigned_exercises.account_id WHERE assigned_exercises = %S AND account.role = %S ;",exId,permissionLevel));
    }

    public static boolean hasQVoted(int ansId, String userid) {
        return select("question_vote",String.format("WHERE answer_id = %S AND account_id = %S",ansId,userid) ).length() > 0;
    }

    public static void overrideQVote(int ansId, String userid, int score) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(String.format("UPDATE question_vote SET score = %S WHERE account_id = %S AND answer_id = %S",
                score,userid,ansId));
    }

    public static void putQVote(int ansId, String userid, int score) throws SQLException {
        JSONObject object = new JSONObject().put("answer_id",ansId).put("account_id",userid).put("score",score);
        insert("question_vote",object);
    }

    public static void putQAnswer(int qid,String accountId, String answer) throws SQLException {
        insert("question_answers",new JSONObject().put("question_id",qid).put("account_id",accountId).put("body",answer).put("date",getCurrentDate()));
    }

    public static void createQuestion(String idInDB, String body, String title) throws SQLException {
        insert("question",new JSONObject().put("account_id",idInDB).put("body",body).put("title",title).put("date",getCurrentDate()));
    }

    public static JSONArray getQuestions() throws SQLException {
        JSONArray ret = select("question",";");
        for(int i=0;i<ret.length();i++){
            JSONObject curr = ret.getJSONObject(i);
            curr.put("author",geRealname(curr.getInt("account_id")));
            curr.put("answers",getQAnswers(curr.getInt("id")));
        }
        return ret;
    }

    private static String geRealname(int account_id) {
        try{
            return select("account",String.format("WHERE id = %s",account_id)).getJSONObject(0).getString("realname");
        }catch (Exception e){
            return "Anonym";
        }
    }

    private static JSONArray getQAnswers(int id) throws SQLException {
        JSONArray ret = select("question_answers",String.format("WHERE question_id = %s", id));
        for(int i=0;i<ret.length();i++){
            JSONObject curr = ret.getJSONObject(i);
            curr.put("score",getQAScore(curr.getInt("id")));
            curr.put("author",geRealname(curr.getInt("account_id")));
        }
        return ret;
    }

    public static int getQAScore(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(String.format("SELECT sum(score) from question_vote WHERE answer_id = %s;",id));
        ResultSet rs = preparedStatement.executeQuery();
        if(rs.next()){
            String sum = rs.getString(1);
            try{
                return Integer.parseInt(sum);
            }catch (Exception e){
                return 0;
            }
        }else {
            return 0;
        }
    }

    public static JSONObject getQuestion(int qid) {
        return select("question",String.format("WHERE id = %s ;",qid)).getJSONObject(0);
    }

    public static JSONObject getAccountFromQuestionId(int qid) {
        String accountId = getQuestion(qid).getString("account_id");
        return select("account",String.format("WHERE id = %s ;",accountId)).getJSONObject(0);
    }

    public static JSONObject getQuestion(String body, String title, String idInDB) {
        return select("question",String.format("WHERE account_id = %s AND title = &s AND body = %s;",idInDB,title,body)).getJSONObject(0);
    }
}
