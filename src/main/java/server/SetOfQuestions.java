/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author ilicm
 */
public class SetOfQuestions implements Serializable {
    
    private String setName;
    private ArrayList<QuizQuestions> questions = new ArrayList();

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public ArrayList<QuizQuestions> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<QuizQuestions> questions) {
        this.questions = questions;
    }
    
    public void addQuestion(QuizQuestions question) 
    {
        this.questions.add(question);
    }
    
}
