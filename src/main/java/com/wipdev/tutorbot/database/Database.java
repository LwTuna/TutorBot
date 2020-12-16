package com.wipdev.tutorbot.database;

public enum Database {

    User_Data("User_Data"),Questions("Questions"),Answers("Answers"),Assigned_Questions("AssignedQuestions");

    String databaseName;

    Database(String databaseName) {
        this.databaseName = databaseName;
    }
}
