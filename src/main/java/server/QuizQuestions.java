/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author ilicm
 */
public class QuizQuestions implements Serializable {
    
    private String questionText;
    private ArrayList<Answer> answers = new ArrayList();

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
    
    public void addAnswer(Answer answer) 
    {
        this.answers.add(answer);
    }
    
    public void shuffleAnswers()
    {
        Collections.shuffle(this.answers);
    }
    
}
