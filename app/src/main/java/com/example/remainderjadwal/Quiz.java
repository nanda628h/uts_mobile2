package com.example.remainderjadwal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Quiz implements Serializable {
    private String id;
    private String title;
    private ArrayList<Question> questions;

    // Constructor kosong baru (default) - INI YANG DITAMBAHIN
    public Quiz() {
        this.id = UUID.randomUUID().toString();
        this.questions = new ArrayList<>();
        // title bisa kosong atau di-set nanti via setter
    }

    // Constructor yang sudah ada (dengan title)
    public Quiz(String title) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.questions = new ArrayList<>();
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question q) {
        questions.add(q);
    }
}