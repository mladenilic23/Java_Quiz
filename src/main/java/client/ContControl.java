/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package client;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author ilicm
 */
public class ContControl extends javax.swing.JFrame {

    Client client;
    /**
     * Creates new form ContForm
     */
    public ContControl(Client client) {
        initComponents();
        this.client = client;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        buttonLogout.setVisible(true);
        buttonStart.setVisible(true);
        buttonScore.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonScore = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();
        buttonLogout = new javax.swing.JButton();
        labelTitle = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        buttonScore.setBackground(new java.awt.Color(204, 255, 255));
        buttonScore.setText("Score");
        buttonScore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonScoreActionPerformed(evt);
            }
        });

        buttonStart.setBackground(new java.awt.Color(153, 255, 153));
        buttonStart.setText("Startuj kviz");
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        buttonLogout.setText("Izloguj se");
        buttonLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLogoutActionPerformed(evt);
            }
        });

        labelTitle.setText("Contestant");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(buttonLogout)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(buttonScore, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(180, 180, 180)
                        .addComponent(labelTitle)))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTitle)
                .addGap(101, 101, 101)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonStart, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonScore, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                .addComponent(buttonLogout)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        // TODO add your handling code here:
        //parent.getNewSet();
    }//GEN-LAST:event_buttonStartActionPerformed

    private void buttonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLogoutActionPerformed
        // TODO add your handling code here:
        client.clientLogOut();
    }//GEN-LAST:event_buttonLogoutActionPerformed

    private void buttonScoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonScoreActionPerformed
        // TODO add your handling code here:
       // parent.sendResults();
    }//GEN-LAST:event_buttonScoreActionPerformed

    public void updateCntUsername(String username)
    {
        this.labelTitle.setText(username);
    }
    
    public void tryAgain(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage);
    }
    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonLogout;
    private javax.swing.JButton buttonScore;
    private javax.swing.JButton buttonStart;
    private javax.swing.JLabel labelTitle;
    // End of variables declaration//GEN-END:variables
}