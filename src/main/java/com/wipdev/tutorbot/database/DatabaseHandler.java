package com.wipdev.tutorbot.database;

import com.mongodb.*;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {


    private MongoClientURI uri;

    private MongoClient mongoClient;

    public final String userKey = "user", passwordKey = "password";
    public final String idKey = "_id";

    public void connect() {
        uri = new MongoClientURI("mongodb+srv://Admin:2212@cluster0.jquix.mongodb.net/user_data?retryWrites=true&w=majority");

        mongoClient = new MongoClient(uri);
    }

    private void insertOne(Database database, JSONObject object) {
        mongoClient.getDatabase(database.databaseName).getCollection(database.collectionName).insertOne(Document.parse(object.toString()));
    }

    private JSONObject find(Database database, String key, Object value) {
        MongoCursor<Document> it = mongoClient.getDatabase(database.databaseName).getCollection(database.collectionName).find().iterator();
        while (it.hasNext()) {
            var val = it.next();
            if (!val.containsKey(key)) continue;
            if (val.getString(key).equals(value)) {
                var ret = new JSONObject(val.toJson());
                it.close();
                return ret;
            }
        }
        it.close();
        return null;
    }

    private List<JSONObject> getAll(Database database){
        List<JSONObject> objects = new ArrayList<>();
        MongoCursor<Document> it = mongoClient.getDatabase(database.databaseName).getCollection(database.collectionName).find().iterator();
        while (it.hasNext()) {
            var val = it.next();
            var ret = new JSONObject(val.toJson());
            objects.add(ret);
        }
        it.close();
        return objects;
    }

    public JSONObject logIn(String username, String password) {
        var object = find(Database.User_Data, userKey, username);
        if (object == null) return null;
        if (object.getString(passwordKey).equals(password)) {
            return object;
        } else {
            return null;
        }
    }

    public boolean contains(Database database,String key,String value){
        return find(database,key,value) !=null;
    }

    public String createNewUser(String username,String password){
        JSONObject object = new JSONObject();
        object.put(userKey,username);
        object.put(passwordKey,password);
        insertOne(Database.User_Data, object);
        JSONObject entry = find(Database.User_Data,userKey,username);
        return entry.get(idKey).toString();
    }

    public void createQuestion(JSONObject question){
        question.put("questionID", getNextQuestionId());
        insertOne(Database.Questions,question);
    }

    public int getNextQuestionId(){
        List<JSONObject> objects = getAllQuestions();

        int maxId = 0;
        for(JSONObject obj:objects){
            if(obj.getInt("questionID") > maxId){
                maxId = obj.getInt("questionID");
            }
        }
        return maxId +1;

    }

    public List<JSONObject> getAllQuestions(){
        return getAll(Database.Questions);
    }

    public void putAnswer(int id, String answer, String userObjectID) {
        JSONObject packet = new JSONObject();
        packet.put("questionID",id);
        packet.put("answer",answer);
        packet.put("userId",userObjectID);
        insertOne(Database.Answers,packet);
    }
}
