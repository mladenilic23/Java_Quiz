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
public class Contestant implements Serializable, Comparable<Contestant> {
    
    private String userName;
    private ArrayList<ScoreAndHelp> sets;
    private int totalAnswers;
    private int correctAnswers;
    private boolean active;
    private boolean available;
    
    public Contestant(String userName)
    {
        this.userName = userName;
        this.sets = new ArrayList<>(); 
        this.totalAnswers = 0;
        this.correctAnswers = 0;
        this.active = false;
        this.available = false;
    }
    
    public ArrayList<ScoreAndHelp> getSets()
    {
        return sets;
    }
    
    public void setSets(ArrayList<ScoreAndHelp> sets)
    {
        this.sets = sets;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public void addSet(SetOfQuestions set)
    {
        sets.add(new ScoreAndHelp(set));
    }
    
    public void scoreUpdate()
    {
        int totalNum = 0;
        int correctNum = 0;
        
        for (ScoreAndHelp set: sets)
        {
            totalNum += set.getTotalAnswers();
            correctNum += set.getCorrectAnswers();
        }
        
        totalAnswers = totalNum;
        correctAnswers = correctNum;
    }
    
    public int getTotalAnswers()
    {
        return totalAnswers;
    }
    
    public int getCorrectAnswers()
    {
        return correctAnswers;
    }
    
    public boolean isAvailable()
    {
        return available;
    }
    
    public void setAvailable(boolean available)
    {
        this.available = available;
    }
    
    @Override
    public int compareTo(Contestant cnt) 
    {
        return Integer.compare(cnt.correctAnswers, this.correctAnswers);
    }
}
