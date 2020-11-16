package com.wipdev.tutorbot.questions;

public enum QuestionType {

    INPUT("input"),TEXT_AREA("textArea"),MULTIPLE_CHOICE("multipleChoice");

    String key;

    QuestionType(String key) {
        this.key = key;
    }
}
