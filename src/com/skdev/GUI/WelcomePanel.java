
package com.skdev.GUI;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WelcomePanel extends javax.swing.JPanel {

    /**
     * Creates new form WelcomePanel
     */
    public WelcomePanel() {
        initComponents();
        setSize(2000,2000);
    }
    
    public WelcomePanel(MainWindow father) {
        initComponents();
        this.father = father;
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelButtonPC = new javax.swing.JLabel();
        jLabelButtonPVP = new javax.swing.JLabel();
        jLabelBackground = new javax.swing.JLabel();

        setLayout(null);

        jLabelButtonPC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/buttonPC.png"))); // NOI18N
        jLabelButtonPC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelButtonPCMouseClicked(evt);
            }
        });
        add(jLabelButtonPC);
        jLabelButtonPC.setBounds(10, 330, 200, 200);

        jLabelButtonPVP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/buttonPVP.png"))); // NOI18N
        jLabelButtonPVP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelButtonPVPMouseClicked(evt);
            }
        });
        add(jLabelButtonPVP);
        jLabelButtonPVP.setBounds(230, 330, 200, 201);

        jLabelBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/Amiralbatti.png"))); // NOI18N
        add(jLabelBackground);
        jLabelBackground.setBounds(10, -20, 950, 760);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabelButtonPCMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelButtonPCMouseClicked
        // TODO add your handling code here:
        this.setVisible(false);
        playShotSound();
        father.playSongs("game");
        father.getjPanelBackground().setBounds(0, 20, this.getWidth(), this.getHeight());
        father.setContentPane(father.getjPanelBackground());
        father.getjPanelBackground().setVisible(true);
        father.startGameVsPC();
    }//GEN-LAST:event_jLabelButtonPCMouseClicked

    private void jLabelButtonPVPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelButtonPVPMouseClicked
        this.setVisible(false);
        playShotSound();
        EnterRoom er = new EnterRoom(father);
        er.setBounds(0, 20, this.getWidth(), this.getHeight());
        father.setContentPane(er);
        er.setVisible(true);
    }//GEN-LAST:event_jLabelButtonPVPMouseClicked

    private void playShotSound() {
        try {
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResource("/com/skdev/resources/sounds/bomb2.wav")); 
        Clip clip = AudioSystem.getClip();
        clip.open(inputStream);
        clip.start();
        } catch(UnsupportedAudioFileException e) {
            
        } catch(IOException e) {
            
        } catch (LineUnavailableException e) {
            
        } 
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelBackground;
    private javax.swing.JLabel jLabelButtonPC;
    private javax.swing.JLabel jLabelButtonPVP;
    // End of variables declaration//GEN-END:variables
    MainWindow father;
}
