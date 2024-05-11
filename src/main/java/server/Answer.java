/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.Serializable;

/**
 *
 * @author ilicm
 */
public class Answer implements Serializable {
    
    private String answerText;
    private boolean correct;

    public String getAnswerText() 
    {
        return answerText;
    }

    public void setAnswerText(String answerText) 
    {
        this.answerText = answerText;
    }

    public boolean isCorrect() 
    {
        return correct;
    }

    public void setCorrect(boolean correct) 
    {
        this.correct = correct;
    }
    
    
}
