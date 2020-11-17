package com.wipdev.tutorbot.database;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;

public class DatabaseHandler {


    private MongoClientURI uri;

    private MongoClient mongoClient;

    private final String userKey = "user", passwordKey = "password";
    public final String idKey = "_id";

    public void connect() {
        uri = new MongoClientURI("mongodb+srv://Admin:2212@cluster0.jquix.mongodb.net/user_data?retryWrites=true&w=majority");
        mongoClient = new MongoClient(uri);


    }

    private void insertOne(Database database, JSONObject object) {
        mongoClient.getDatabase(database.databaseName).getCollection(database.collectionName).insertOne(new Document(object.toMap()));
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



    public JSONObject logIn(String username, String password) {
        var object = find(Database.User_Data, userKey, username);
        if (object == null) return null;
        if (object.getString(passwordKey).equals(password)) {
            return object;
        } else {
            return null;
        }
    }


    public String createNewUser(String username,String password){
        JSONObject object = new JSONObject();
        object.put(userKey,username);
        object.put(passwordKey,password);
        insertOne(Database.User_Data, object);
        JSONObject entry = find(Database.User_Data,userKey,username);
        return entry.get(idKey).toString();
    }

}
