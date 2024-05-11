/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author ilicm
 */
public class Client extends javax.swing.JFrame {

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private ServerMessage reciever;
    
    private String username;
    private String role;
    
    SignIn signIn;
    ContControl contRegistration;
    AdminControl adminRegistration;
    NewContestant newContestant;
    NewAdmin newAdmin;
    QuestionFrame questionFrame;
            
    public Client() {
        initComponents();
        labelNotAvailable.setVisible(false);
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
    
    public BufferedReader getBr() {
        return br;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    public void loginSuccessful(boolean ok) 
    {
        if (ok) 
        {
            if (this.role.equals("admin"))
            {
                adminRegistration = new AdminControl(this);
                adminRegistration.setVisible(true);
                adminRegistration.updateAdmUsername(this.username);
                signIn.setVisible(false);
            }
            else 
            {
                contRegistration = new ContControl(this);
                contRegistration.setVisible(true);
                contRegistration.updateCntUsername(this.username);
                signIn.setVisible(false);
            }
        }
        else
        {
            signIn.notCorrect();
        }
    }
  
  
    public void printMess(String mess) {
        JOptionPane.showMessageDialog(null, mess);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTitle = new javax.swing.JLabel();
        buttonConnect = new javax.swing.JButton();
        labelNotAvailable = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        labelTitle.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        labelTitle.setText("Molimo Vas da se konektujete kako biste imali pristup.");

        buttonConnect.setText("Konektuj se!");
        buttonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonConnectActionPerformed(evt);
            }
        });

        labelNotAvailable.setText("Server nije dostupan trenutno.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(labelTitle)
                .addContainerGap(80, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelNotAvailable)
                    .addComponent(buttonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(225, 225, 225))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(labelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                .addComponent(buttonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(labelNotAvailable)
                .addGap(16, 16, 16))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonConnectActionPerformed
        // TODO add your handling code here:
            try {
            //First connect to server
            this.socket = new Socket("127.0.0.1", 6001);
            this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()), true);
            
            //Create new thread to recieve messagers from server
            this.reciever = new ServerMessage(this);
            Thread thr = new Thread(reciever);
            thr.start();
            
            //enable next step
            buttonConnect.setVisible(false);
            labelNotAvailable.setVisible(false);
            
            this.setVisible(false);
            signIn = new SignIn(this);
            signIn.setVisible(true);
        } 
        catch (IOException ex) 
        {
            labelNotAvailable.setVisible(true);
        } 
    }//GEN-LAST:event_buttonConnectActionPerformed
    
    public void login(String loginMessage, String role, String username)
    {
        this.pw.println(loginMessage);
        this.role = role;
        this.username = username;
    }    
    
    public void logout() 
    {
        try {
            this.socket.close();
        } 
        catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (this.role.equals("contestant")) {
            contRegistration.setVisible(false);
        } else {
            adminRegistration.setVisible(false);
        }
        
        this.username = "";
        this.role = "";

        this.setVisible(true);
        buttonConnect.setVisible(true);
        labelTitle.setVisible(true);
        labelNotAvailable.setVisible(false);
        
        labelNotAvailable.setVisible(false);
    }
    
    public void clientLogOut()
    {
        pw.println("Logout");
            
        logout();
    };
       
    public void contestantCreation() 
    {
        String errorMessage;
        
        String newContestantUsername = newContestant.textUsername.getText().trim();
        String newContestantPassword = newContestant.textPassword.getText().trim();
       
        if (newContestantUsername.equals("") || newContestantPassword.equals(""))
        {
            errorMessage = "All fields must be filled!";
            newContestant.tryAgain(errorMessage);
            return;
        }
        else 
        {
            Pattern userNamePattern = Pattern.compile("^[^0-9][a-zA-Z0-9]*$");
            Matcher userNameMatcher = userNamePattern.matcher(newContestantUsername);
            boolean matchFound = userNameMatcher.find();
            if (!matchFound)
            {
                errorMessage = "Username incorrect!";
                newContestant.tryAgain(errorMessage);
                return;
            }
            
            Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{7,}$");
            Matcher passwordMatcher = passwordPattern.matcher(newContestantPassword);
            matchFound = passwordMatcher.find();
            if (!matchFound)
            {
                errorMessage = "Password incorrect!";
                newContestant.tryAgain(errorMessage);
                return;
            }
        }
        
        newContestant.setVisible(false);
        String newContestantInfo = "New contestant" + ":" + newContestantUsername + ":" + newContestantPassword;
        this.pw.println(newContestantInfo);
    }
    
    public void adminCreation() 
    {
        String errorMessage;
        
        String newAdminUsername = newAdmin.textUsername.getText().trim();
        String newAdminPassword = newAdmin.textPassword.getText().trim();
       
        if (newAdminUsername.equals("") || newAdminPassword.equals(""))
        {
            errorMessage = "All fields must be filled!";
            newAdmin.tryAgain(errorMessage);
            return;
        }
        else 
        {
            Pattern userNamePattern = Pattern.compile("^[^0-9][a-zA-Z0-9]*$");
            Matcher userNameMatcher = userNamePattern.matcher(newAdminUsername);
            boolean matchFound = userNameMatcher.find();
            if (!matchFound)
            {
                errorMessage = "Username incorrect!";
                newAdmin.tryAgain(errorMessage);
                return;
            }
            
            Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{7,}$");
            Matcher passwordMatcher = passwordPattern.matcher(newAdminPassword);
            matchFound = passwordMatcher.find();
            if (!matchFound)
            {
                errorMessage = "Password incorrect!";
                newAdmin.tryAgain(errorMessage);
                return;
            }
        }
        
        newAdmin.setVisible(false);
        String newAdminInfo = "New admin" + ":" + newAdminUsername + ":" + newAdminPassword;
        this.pw.println(newAdminInfo);
    }
    
    public void setMessage(String message)
    {
        this.pw.println(message);
    }
    
    public void addContestant()
    {
        newContestant = new NewContestant(this);
        newContestant.setVisible(true);
    }
    
    public void addAdmin()
    {
        newAdmin = new NewAdmin(this);
        newAdmin.setVisible(true);
    }
    
    public void updateContestants(String [] names) 
    {
        adminRegistration.updateContestants(names);
    }
    
    public void updateAdmins(String [] names) 
    {
        adminRegistration.updateAdmins(names);
    }
   
    public void updateSets(String [] names) 
    {
    }

    public void chooseAnswer(String answer)
    {
        this.pw.println(answer);
    }
    
    public void startSet()
    {
        adminRegistration.startSet();
    }
    
    public void closeQuestionWindow()
    {
        questionFrame.setVisible(false);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonConnect;
    private javax.swing.JLabel labelNotAvailable;
    private javax.swing.JLabel labelTitle;
    // End of variables declaration//GEN-END:variables
}