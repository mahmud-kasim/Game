
package com.skdev.GUI;

import com.skdev.classes.ClientThread;
import com.skdev.classes.Process;
import com.skdev.classes.ServerThread;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;


public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    private String playerName;
    private ServerThread serverSock;
    private com.skdev.classes.Process proc;

    private ClientThread clientSock;    
    int rows = 8;
    int columns = 8;
    GridGUI grid1;
    public GridGUI pOneGrid, pTwoGrid;
    AIGridGUI grid2;
    boolean autoPlacement = false;
    AudioInputStream inputStream;
    Clip clip;
    
    public MainWindow() {
        initComponents();
        playSongs("");
        this.setContentPane(new WelcomePanel(this));
    }

     public void startNetworkGame() {
        pOneGrid = new GridGUI(rows, columns);
        pOneGrid.build(true);
        pOneGrid.setBounds(0, 0, 500, 270);
        jPanelBackground.add(pOneGrid);
        
        pTwoGrid = new GridGUI(rows, columns);
        pTwoGrid.build(true);
        pTwoGrid.setBounds(0, 310, 500, 270);        
        jPanelBackground.add(pTwoGrid);
        
        jPanelBackground.updateUI();
        
        
        Runnable reader = new MainWindow.IncomingReaderNetwork();
        Thread readerThread = new Thread(reader);
        readerThread.start();

        Runnable ecReader = new MainWindow.EnableCellsReaderNetwork();
        Thread ecReaderThread = new Thread(ecReader);
        ecReaderThread.start();
     }     
     
     public void startGameVsPC() {
        grid1 = new GridGUI(rows, columns);
        grid1.build(false);
        grid1.setBounds(0, 310, 500, 270);
        jPanelBackground.add(grid1);
        
        grid2 = new AIGridGUI(rows, columns);
        grid2.setBounds(0, 0, 500, 270);
        
        jPanelBackground.add(grid2);
        jPanelBackground.updateUI();
        //buildGUI();

        if(autoPlacement) {
         grid2.autoPlaceShips();
        } else {
         grid2.placeShips();
        }
        
        jTextAreaHist.setText("Sadece oyuncu modunda kullanılabilir sohbet \nvs Oyuncu...");
        jTextFieldMsg.setEnabled(false);
        
        Runnable reader = new MainWindow.IncomingReader();
        Thread readerThread = new Thread(reader);
        readerThread.start();

        Runnable ecReader = new MainWindow.EnableCellsReader();
        Thread ecReaderThread = new Thread(ecReader);
        ecReaderThread.start();
     }
     
     public void playSongs(String song) {
            try {
                if (song.equals(""))
                    inputStream = AudioSystem.getAudioInputStream(getClass().getResource("/com/skdev/resources/sounds/song1.mid"));
                else if(song.equals("game")) {
                    clip.stop();
                    inputStream = AudioSystem.getAudioInputStream(getClass().getResource("/com/skdev/resources/sounds/game.mid"));
                }
                clip = AudioSystem.getClip();
                clip.open(inputStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                //Thread.sleep(10000); // looping as long as this thread is alive
            } catch(UnsupportedAudioFileException e) {}
            catch(IOException e) {} 
            catch (LineUnavailableException e) {}    
     }
     
     
    public class IncomingReaderNetwork implements Runnable {
        public void run() {
         while(pOneGrid != null && pTwoGrid != null) {

          String result;
          try {
           BufferedReader reader = new BufferedReader(new FileReader("Text.txt"));
           while((result = reader.readLine()) != null) {
            jTextFieldSt.setText(result);
           }
          } catch(Exception ex) {}

          if(pOneGrid.getEndGame() == true || pTwoGrid.getEndGame() == true) {           
              try {
                  proc.send(99, "");
                  resetInterface();
                  break;
              } catch (Exception ex) {
                  Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
              }
              pOneGrid.setEndGame();
          } else {

           if(pOneGrid.getClicked() == true) {
            pTwoGrid.setClicked();
           } else if (pTwoGrid.getClicked() == true) {
               pOneGrid.setClicked();
           }
          }
         }
        }
       }
    
    public class EnableCellsReaderNetwork implements Runnable {
        public void run() {
         while(pOneGrid != null && pTwoGrid != null) {
          while(!pOneGrid.areShipsPlaced() || !pOneGrid.areShipsPlaced()) {
              //jTextFieldSt.setText("Aguarde os navios serem colocados...");
          }
          //auto.setEnabled(false);
          break;
         }
        }
       }
    
    public class IncomingReader implements Runnable {
        public void run() {
         while(grid1 != null && grid2 != null) {

          String result;
          try {
           BufferedReader reader = new BufferedReader(new FileReader("Text.txt"));
           while((result = reader.readLine()) != null) {
            jTextFieldSt.setText(result);
           }
          } catch(Exception ex) {}

          if(grid1.getEndGame() == true || grid2.getEndGame() == true) {
           grid1.setEndGame();
           resetInterface();
           break;
          } else {

           if(grid1.getClicked() == true) {

            try {
             Thread.sleep(2000);
            } catch(InterruptedException ex) {
             ex.printStackTrace();
            }

            grid2.go();
            grid1.setClicked();
           }
          }
         }
        }
       }

    public class EnableCellsReader implements Runnable {
        @Override
        public void run() {
         while(grid1 != null && grid2 != null) {
          while(!grid2.areShipsPlaced()) {System.out.print("");}
          grid1.enableCells();
          //auto.setEnabled(false);
          break;
         }
        }
       }
    
    public void resetInterface() {
        this.dispose();
        this.initComponents();
        playSongs("");
        this.setContentPane(new WelcomePanel(this));
        this.show();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelBackground = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaHist = new javax.swing.JTextArea();
        jTextFieldMsg = new javax.swing.JTextField();
        jTextFieldSt = new javax.swing.JTextField();
        jLabelShip = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuArquivo = new javax.swing.JMenu();
        jNewPCGame = new javax.swing.JMenuItem();
        jNewNetworkGame = new javax.swing.JMenuItem();
        jEnterGame = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Amiral Battı Oyunu");
        setResizable(false);

        jTextAreaHist.setEditable(false);
        jTextAreaHist.setColumns(20);
        jTextAreaHist.setRows(5);
        jTextAreaHist.setText("Aguardando conexão...\n");
        jScrollPane1.setViewportView(jTextAreaHist);

        jTextFieldMsg.setText("Bir mesaj giriniz...");
        jTextFieldMsg.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextFieldMsgFocusGained(evt);
            }
        });
        jTextFieldMsg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldMsgMouseClicked(evt);
            }
        });
        jTextFieldMsg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldMsgActionPerformed(evt);
            }
        });

        jTextFieldSt.setEditable(false);

        javax.swing.GroupLayout jPanelBackgroundLayout = new javax.swing.GroupLayout(jPanelBackground);
        jPanelBackground.setLayout(jPanelBackgroundLayout);
        jPanelBackgroundLayout.setHorizontalGroup(
            jPanelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBackgroundLayout.createSequentialGroup()
                .addComponent(jTextFieldSt, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(jTextFieldMsg)
                    .addComponent(jLabelShip, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelBackgroundLayout.setVerticalGroup(
            jPanelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelBackgroundLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelBackgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldMsg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelShip, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jMenuArquivo.setText("Ayarlar");

        jNewPCGame.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jNewPCGame.setText("Yeni Oyun Vs PC...");
        jNewPCGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNewPCGameActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jNewPCGame);

        jNewNetworkGame.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jNewNetworkGame.setText("Ağ için Yeni Bir Oyun");
        jNewNetworkGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNewNetworkGameActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jNewNetworkGame);

        jEnterGame.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jEnterGame.setText("Ağ oyununa gir");
        jEnterGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEnterGameActionPerformed(evt);
            }
        });
        jMenuArquivo.add(jEnterGame);

        jMenuBar1.add(jMenuArquivo);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBackground, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelBackground, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldMsgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldMsgActionPerformed
        // TODO add your handling code here:
        if(proc == null) {
            sendChat("Aguarde alguém se conectar...");
            return;
        }
        try {
            //proc.send(1, playerName + ": " + jTextFieldMsg.getText());
            proc.send(1, playerName + ": " + jTextFieldMsg.getText());
            this.sendChat(playerName + ": " + jTextFieldMsg.getText());
            jTextFieldMsg.setText("");
        } catch (Exception ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTextFieldMsgActionPerformed

    public void setProc(Process proc) {
        this.proc = proc;
    }

        
    private void jTextFieldMsgMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldMsgMouseClicked
        // TODO add your handling code here:
        if(jTextFieldMsg.getText().equals("Digite uma mensagem..."))
            jTextFieldMsg.setText(null);
    }//GEN-LAST:event_jTextFieldMsgMouseClicked

    private void jTextFieldMsgFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldMsgFocusGained
        // TODO add your handling code here:
        if(jTextFieldMsg.getText().equals("Digite uma mensagem..."))
            jTextFieldMsg.setText(null);
    }//GEN-LAST:event_jTextFieldMsgFocusGained

    private void jEnterGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEnterGameActionPerformed
        this.jPanelBackground.setVisible(false);
        EnterRoom er = new EnterRoom(this);
        er.setBounds(0, 20, this.getWidth(), this.getHeight());
        this.setContentPane(er);
        er.setVisible(true);
    }//GEN-LAST:event_jEnterGameActionPerformed

    private void jNewNetworkGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNewNetworkGameActionPerformed
        this.jPanelBackground.setVisible(false);
        CreateServer cs = new CreateServer(this);
        cs.setBounds(0, 20, this.getWidth(), this.getHeight());
        this.setContentPane(cs);
        cs.setVisible(true);
    }//GEN-LAST:event_jNewNetworkGameActionPerformed

    private void jNewPCGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNewPCGameActionPerformed
        // TODO add your handling code here:
        resetInterface();
        //jNewPCGame.setEnabled(false);
    }//GEN-LAST:event_jNewPCGameActionPerformed
    
    public void createServer(String name, int port) {
        try {
        jPanelBackground.setBounds(0, 20, this.getWidth(), this.getHeight());
        this.setContentPane(jPanelBackground);
        jPanelBackground.setVisible(true);
        this.playerName = name;
        
        // Start server's socket
        serverSock = new ServerThread(port, this);
        serverSock.start();
        System.out.println("MainWindow: Servidor iniciado");
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    public void newClient(String name, int port, String serverIP) {
        try {
        jPanelBackground.setBounds(0, 20, this.getWidth(), this.getHeight());
        this.setContentPane(jPanelBackground);
        jPanelBackground.setVisible(true);
        
        this.playerName = name;
        
        clientSock = new ClientThread(this, name, serverIP, port);
        clientSock.start();
        this.sendChat("User " + name + " connected!");
         } catch (Exception e) {
             System.err.println(e);      
         }
    }

    public String getPlayerName() {
        return playerName;
    }

    public JPanel getjPanelBackground() {
        return jPanelBackground;
    }
    

    public GridGUI getpOneGrid() {
        return pOneGrid;
    }

    public GridGUI getpTwoGrid() {
        return pTwoGrid;
    }
    
    public void sendChat(String msg) {
        this.jTextAreaHist.append(msg + "\n");
    }
    
    public void message(String msg) {
        msg += "\n";
        jTextAreaHist.append(msg);
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
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem jEnterGame;
    public static javax.swing.JLabel jLabelShip;
    private javax.swing.JMenu jMenuArquivo;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jNewNetworkGame;
    private javax.swing.JMenuItem jNewPCGame;
    private javax.swing.JPanel jPanelBackground;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaHist;
    private javax.swing.JTextField jTextFieldMsg;
    private javax.swing.JTextField jTextFieldSt;
    // End of variables declaration//GEN-END:variables
}
