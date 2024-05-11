/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ilicm
 */
public class CloseApp implements Runnable{
    
    private ArrayList<Contestant> contestants;
    private ArrayList<String> admins;
    
    ObjectOutputStream outContestants;
    ObjectOutputStream outAdmins;

    //putanje do .txt fajlova
    private String contestantsTxt = "D:\\JavaProjects\\contestants.txt";
    private String adminsTxt = "D:\\JavaProjects\\admins.txt";
    
    public CloseApp(ArrayList<Contestant> contestants, ArrayList<String> admins) 
    {
        this.contestants = contestants;
        this.admins = admins;
    }
    
    @Override
    public void run() 
    {
        Scanner sc = new Scanner(System.in);
        while(true) 
        {
            String userMess = sc.nextLine();
            if (userMess.equalsIgnoreCase("Exit")) {
                System.out.println("Server terminated.");
                saveContestants();
                saveAdmins();
                System.exit(0);
            }
            else 
            {
                System.out.println("Command for closing app: Exit");
            }
        }
    }
    
    void saveContestants() 
    {
        try 
        {
            outContestants = new ObjectOutputStream(new FileOutputStream(contestantsTxt));
            outContestants.writeObject(this.contestants);
            outContestants.close();
        } catch (FileNotFoundException ex1) {
            Logger.getLogger(ServerConnectedClient.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (IOException ex1) {
            Logger.getLogger(ServerConnectedClient.class.getName()).log(Level.SEVERE, null, ex1);
        }
        System.out.println("All contestants saved!");
    }

    void saveAdmins() 
    {
        try 
        {
            outAdmins = new ObjectOutputStream(new FileOutputStream(adminsTxt));
            outAdmins.writeObject(this.admins);
            outAdmins.close();
        } catch (FileNotFoundException ex1) {
            Logger.getLogger(ServerConnectedClient.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (IOException ex1) {
            Logger.getLogger(ServerConnectedClient.class.getName()).log(Level.SEVERE, null, ex1);
        }
        System.out.println("All admins saved!");
    } 
}
