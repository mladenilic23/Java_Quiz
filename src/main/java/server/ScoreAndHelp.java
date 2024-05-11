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
public class ScoreAndHelp implements Serializable {
    
    private SetOfQuestions set;
    private int totalAnswers;
    private int correctAnswers;
    private boolean rejected;
    //help
    private boolean fiftyFifty;
    private boolean questionReplacement;
    private boolean friendHelp;
    
    private int changedQuestion;
    private final ArrayList<Integer> ejectedQuestions;
    
    public ScoreAndHelp(SetOfQuestions set)
    {
        this.set = set;
        this.totalAnswers = 0;
        this.correctAnswers = 0;
        //pomoci
        this.rejected = false;
        this.fiftyFifty = true;
        this.questionReplacement = true;
        this.friendHelp = true;
        
        this.changedQuestion = 0;
        this.ejectedQuestions = new ArrayList<>();
    }
    
    public SetOfQuestions getSet()
    {
        return set;
    }
    
    public void setSet(SetOfQuestions set)
    {
        this.set = set;
    }
    
    public int getTotalAnswers()
    {
        return totalAnswers;
    }
    
    public void setTotalAnswers(int correctAnswers)
    {
        this.totalAnswers = correctAnswers;
    }
    
    public void addTotalAnswers()
    {
        totalAnswers++;
    }
    
    public int getCorrectAnswers()
    {
        return correctAnswers;
    }
    
    public void setCorrectAnswers(int correctAnswers)
    {
        this.correctAnswers = correctAnswers;
    }
    
    public void addCorrectAnswers()
    {
        correctAnswers++;
    }
//=========================POMOCI==================================================    
    public boolean getFiftyFifty()
    {
        return fiftyFifty;
    }
    
    public void setFiftyFifty(boolean fiftyFifty)
    {
        this.fiftyFifty = fiftyFifty;
    }
    
    public boolean getQuestionReplacement()
    {
        return questionReplacement;
    }
    
    public void setQuestionReplacement(boolean questionReplacement)
    {
        this.questionReplacement = questionReplacement;
    }
    
    public boolean getFriendHelp()
    {
        return friendHelp;
    }
    
    public void setFriendHelp(boolean friendHelp)
    {
        this.friendHelp = friendHelp;
    }
    
    public boolean getRejected()
    {
        return rejected;
    }
    
    public void setRejected(boolean rejected)
    {
        this.rejected = rejected;
    }
    
    public void setChangedQuestion(int changedQuestion)
    {
        this.changedQuestion = changedQuestion;
    }
    
    public int getChangedQuestion()
    {
        return changedQuestion;
    }
    
    public void addEjectedQuestions(int number)
    {
        this.ejectedQuestions.add(number);
    }
    
    public ArrayList<Integer> getEjectedQuestions()
    {
        return ejectedQuestions;
    }
    
}
