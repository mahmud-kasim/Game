
package com.skdev.GUI;

import com.skdev.classes.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GridGUI extends JPanel {

    ArrayList<BSButton> buttons = new ArrayList<BSButton>();
    ArrayList<Ship> allShips = new ArrayList<Ship>();
    int[] testLocations;
    int numOfGuesses = 0;
    String text = "";
    int rows;
    int columns;
    boolean clicked = false;
    boolean endGame = false;
    Color darkRed = new Color(100, 0, 0);
    Border loweredBevel = BorderFactory.createLoweredBevelBorder();
    static int clickedLocation;
    // Can be used for a possible English version of this game
// Ship destroyer = new Ship(2, "destroyer");
// Ship cruiser = new Ship(3, "cruiser");
// Ship submarine = new Ship(3, "submarine");
// Ship battleship = new Ship(4, "battleship");
// Ship aircraftCarrier = new Ship(5, "aircraft carrier");
    Ship destroyer = new Ship(2, "destróier");
    Ship cruiser = new Ship(3, "cruzador");
    Ship submarine = new Ship(3, "submarino");
    Ship battleship = new Ship(4, "encouraçado");
    Ship aircraftCarrier = new Ship(5, "porta-aviões");
    // Atributos de AIGridGUI
    ArrayList<SetShipsListener> listeners = new ArrayList<SetShipsListener>();
    Border defaultBorder;
    Ship shipToPlace;
    boolean vertical = false;
    boolean clear;
    boolean shipsPlaced = false;
 // Fim atributos de AIGridGUI

    public GridGUI(int r, int c) {
        rows = r;
        columns = c;

        try {
            FileWriter writer = new FileWriter("Text.txt");
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void build(boolean gameType) {

        allShips.add(destroyer);
        allShips.add(cruiser);
        allShips.add(submarine);
        allShips.add(battleship);
        allShips.add(aircraftCarrier);

        for (int i = 0; i < (rows * columns); i++) {
            BSButton b = new BSButton();
            b.setEnabled(false);
            b.setGridLocation(i);
            buttons.add(b);
        }

        if (gameType == false) {
            setShipLocations();
        }

        GridLayout g = new GridLayout(rows, columns);
        this.setLayout(g);

        for (BSButton bsb : buttons) {
            if (gameType == false) // If it's Player Vs PC, add PC's listener
            {
                bsb.addActionListener(new MyCellListener());
            }
            this.add(bsb);
        }
    }

    public void addMyCellListener() {
        for (BSButton bsb : buttons) {
            bsb.addActionListener(new MyCellListener());
            this.add(bsb);
        }
    }

    // Métodos de AIGridGUI
    public void placeShips() {
        for (int i = 0; i < buttons.size(); i++) {
            listeners.add(new SetShipsListener());
            buttons.get(i).addMouseListener(listeners.get(i));
        }

        shipToPlace = allShips.get(0);
        //text = "Place " + shipToPlace.getName() + ". Right click to toggle horizontal/vertical.";
        text = "Coloque o " + shipToPlace.getName() + ". Botão direito para alternar entre horizontal/vertical.";
        try {
            FileWriter writer = new FileWriter("Text.txt");
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    class SetShipsListener implements MouseListener {

        public void mouseEntered(MouseEvent e) {
            BSButton cell = (BSButton) e.getSource();
            highlightCells(cell, 0);
        }

        public void mouseReleased(MouseEvent e) {
            BSButton cell = (BSButton) e.getSource();
            if (e.getButton() == MouseEvent.BUTTON1 && clear) {
                highlightCells(cell, 1);
                if (allShips.indexOf(shipToPlace) < (allShips.size() - 1)) {
                    int nextShip = allShips.indexOf(shipToPlace) + 1;
                    shipToPlace = allShips.get(nextShip);
                    text = "Place " + shipToPlace.getName() + ". Right click to toggle horizontal/vertical.";
                } else {
                    for (int i = 0; i < buttons.size(); i++) {
                        BSButton bsb = buttons.get(i);
                        bsb.removeMouseListener(listeners.get(i));
                        bsb.setEnabled(false);
                    }
                    text = "Let the battle begins!";
                    shipsPlaced = true;
                }

                try {
                    FileWriter writer = new FileWriter("Text.txt");
                    writer.write(text);
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (e.getButton() == MouseEvent.BUTTON3) {
                vertical = !vertical;
                for (BSButton bsb : buttons) {
                    if (bsb.getCellContents() == null) {
                        bsb.setBorder(defaultBorder);
                    }
                }
                highlightCells(cell, 0);
            }
        }

        public void mouseExited(MouseEvent e) {
            BSButton cell = (BSButton) e.getSource();
            highlightCells(cell, 2);
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }
    }

    public void highlightCells(BSButton b, int x) {
        BSButton cell = b;
        int actionToTake = x;
        clear = true;

        if (vertical) {
            for (int i = 0; i < shipToPlace.getLength(); i++) {
                int testing = cell.getGridLocation() + (i * columns);
                if (testing > (rows * columns) || buttons.get(testing).getCellContents() != null) {
                    clear = false;
                }
            }
        } else {

            for (int i = 0; i < shipToPlace.getLength(); i++) {
                int testing = cell.getGridLocation() + i;
                if ((i > 0 && (testing % columns) == 0) || buttons.get(testing).getCellContents() != null) {
                    clear = false;
                }
            }
        }

        if (clear) {

            if (vertical) {

                for (int i = 0; i < shipToPlace.getLength(); i++) {
                    BSButton bsb = buttons.get(cell.getGridLocation() + (i * columns));
                    if (actionToTake == 0) {
                        bsb.setBorder(loweredBevel);
                    } else {
                        bsb.setBorder(defaultBorder);
                        if (actionToTake == 1) {
                            bsb.setCellContents(shipToPlace);
                            bsb.setBackground(Color.blue);
                            bsb.setBorder(loweredBevel);
                            // For avoiding placing two ships at the same location
                            bsb.removeMouseListener(listeners.get(cell.getGridLocation() + i));
                        }
                    }
                }
            } else {

                for (int i = 0; i < shipToPlace.getLength(); i++) {
                    BSButton bsb = buttons.get(cell.getGridLocation() + i);
                    if (actionToTake == 0) {
                        bsb.setBorder(loweredBevel);
                    } else {
                        bsb.setBorder(defaultBorder);
                        if (actionToTake == 1) {
                            bsb.setCellContents(shipToPlace);
                            bsb.setBackground(Color.blue);
                            bsb.setBorder(loweredBevel);
                            // For avoiding placing two ships at the same location
                            bsb.removeMouseListener(listeners.get(cell.getGridLocation() + i));
                        }
                    }
                }
            }
        }
    }
 //Fim métodos AIGridGUI

    public void setShipLocations() {

        for (Ship s : allShips) {
            int le = s.getLength();
            int clear = 0;
            testLocations = new int[le];

            while (clear < le) {
                setTestLocations(le);
                clear = 0;
                for (int i = 0; i < le; i++) {
                    if (buttons.get(testLocations[i]).getCellContents() == null) {
                        clear++;
                    }
                }
            }

            for (int i = 0; i < le; i++) {
                buttons.get(testLocations[i]).setCellContents(s);
            }

            testLocations = null;
        }
    }

    public void setTestLocations(int l) {
        boolean vertical = new Random().nextBoolean();
        int x;
        int y;

        if (vertical == true) {
            x = (int) (Math.random() * (columns));
            y = (int) (Math.random() * (rows - l));
            for (int i = 0; i < l; i++) {
                testLocations[i] = x + (columns * (y + i));
            }
        } else {
            x = (int) (Math.random() * (columns - l));
            y = (int) (Math.random() * (rows));
            for (int i = 0; i < l; i++) {
                testLocations[i] = x + i + (columns * y);
            }
        }
    }

    public boolean getClicked() {
        return clicked;
    }

    public void setClicked() {
        clicked = false;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public boolean getEndGame() {
        return endGame;
    }

    public void setEndGame() {
        for (JButton j : buttons) {
            j.setEnabled(false);
        }
    }

    public class MyCellListener implements ActionListener {

        public void actionPerformed(ActionEvent a) {
            if (clicked == false) {
                BSButton cell = (BSButton) a.getSource();
                clickedLocation = cell.getGridLocation();
                Ship s = cell.getCellContents();
                boolean killed = false;
                numOfGuesses++;
                boolean gameOver = true;

                cell.setEnabled(false);
                cell.setBorder(loweredBevel);

                if (s == null) {
                    text = "You missed. Other player's turn...";
                    playSounds("splash");
                    MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/splash.jpg")));
                    cell.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/splashIcon.png")));
                    //cell.setBackground(Color.lightGray);
                } else {
                    killed = s.counter();
                    if (killed == true) {
                        text = "You sunk the " + s.getName() + "! Other player's turn...";
                        playSounds("sunken");
                        if (s.getName().equals("destróier")) {
                            MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/destroyer_sinking.jpg")));
                        } else if (s.getName().equals("cruzador")) {
                            MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/cruiser_sinking.jpg")));
                        } else if (s.getName().equals("submarino")) {
                            MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/submarine_exploding.jpg")));
                        } else if (s.getName().equals("encouraçado")) {
                            MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/battleship_sinking.jpeg")));
                        } else if (s.getName().equals("porta-aviões")) {
                            MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/aircraftcarrier_sinking.jpg")));
                        }
                        for (BSButton bu : buttons) {
                            if (bu.getCellContents() == s) {
                                bu.setBackground(darkRed);
                                cell.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/explosionIcon.png")));
                            }
                        }
                    } else {
                        text = "You got a hit. Other player's turn...";         
                        playSounds("hit");
                        MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/01-kaboom.jpg")));
                        cell.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/explosionIcon.png")));
                        cell.setBackground(Color.red);
                    }
                }

                for (Ship sh : allShips) {
                    if (sh.isKilled() == false) {
                        gameOver = false;
                    }
                }

                if (gameOver == true) {
                    text = "You win! You took " + numOfGuesses + " guesses.";
                    JOptionPane.showMessageDialog(null, "winner " + numOfGuesses);
                    endGame = true;
                }

                try {
                    FileWriter writer = new FileWriter("Text.txt");
                    writer.write(text);
                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                clicked = true;
            }
        }
    }

    public void endPlayerGame() {

        text = "You win! You took " + numOfGuesses + " guesses.";
        JOptionPane.showMessageDialog(null, "winner " + numOfGuesses);
        endGame = true;

        try {
            FileWriter writer = new FileWriter("Text.txt");
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setEndGame();
    }

    public void enableCells() {
        for (BSButton bsb : buttons) {
            bsb.setEnabled(true);
        }
    }

    public boolean areShipsPlaced() {
        return shipsPlaced;
    }

    public void setButtons(ArrayList<BSButton> buttonsAux) {
        for (int i = 0; i < buttonsAux.size(); i++) {
            //bsb.addActionListener(new MyCellListener());
            buttons.get(i).setCellContents(buttonsAux.get(i).getCellContents());
        }
    }

    public int getClickedLocation() {
        return clickedLocation;
    }

    // For network game only
    public void setClickedButton(int location) {
        System.out.println("Button clicked: " + location);
        // if(clicked == false) {
        BSButton cell = buttons.get(location);
        Ship s = cell.getCellContents();
        boolean killed = false;
        numOfGuesses++;
        boolean gameOver = true;

        cell.setEnabled(false);
        cell.setBorder(loweredBevel);

        if (s == null) {
            text = "You missed. Other player's turn...";
            playSounds("splash");
            MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/splash.jpg")));
            cell.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/splashIcon.png")));
            cell.setBackground(Color.lightGray);
        } else {
            killed = s.counter();
            if (killed == true) {
                text = "You sunk the " + s.getName() + "! Other player's turn...";
                if (s.getName().equals("destróier")) {
                    MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/destroyer_sinking.jpg")));
                } else if (s.getName().equals("cruzador")) {
                    MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/cruiser_sinking.jpg")));
                } else if (s.getName().equals("submarino")) {
                    MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/submarine_exploding.jpg")));
                } else if (s.getName().equals("encouraçado")) {
                    MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/battleship_sinking.jpeg")));
                } else if (s.getName().equals("porta-aviões")) {
                    MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/aircraftcarrier_sinking.jpg")));
                }

                for (BSButton bu : buttons) {
                    if (bu.getCellContents() == s) {
                        bu.setBackground(darkRed);
                        cell.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/explosionIcon.png")));
                    }
                }
            } else {
                text = "You got a hit. Other player's turn...";
                playSounds("hit");
                MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/01-kaboom.jpg")));
                cell.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/explosionIcon.png")));
                cell.setBackground(Color.red);
            }
        }

        for (Ship sh : allShips) {
            if (sh.isKilled() == false) {
                gameOver = false;
            }
        }

        if (gameOver == true) {
            text = "You win! You took " + numOfGuesses + " guesses.";
            JOptionPane.showMessageDialog(null, "win " + numOfGuesses);
            endGame = true;
        }

        try {
            FileWriter writer = new FileWriter("Text.txt");
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //}
        // clicked = true;
    }

    public ArrayList<BSButton> getButtons() {
        return buttons;
    }

    private void playSounds(String sound) {
        AudioInputStream inputStream = null;
        try {
            if (sound.equals("splash")) {
                inputStream = AudioSystem.getAudioInputStream(getClass().getResource("/com/skdev/resources/sounds/splash.wav"));
            } else if (sound.equals("hit")) {
                inputStream = AudioSystem.getAudioInputStream(getClass().getResource("/com/skdev/resources/sounds/hit.wav"));
            } else if (sound.equals("sunken")) {
                inputStream = AudioSystem.getAudioInputStream(getClass().getResource("/com/skdev/resources/sounds/shipsunken.wav"));
            }
            Clip clip = AudioSystem.getClip();
            clip.open(inputStream);
            clip.start();
        } catch (UnsupportedAudioFileException e) {

        } catch (IOException e) {

        } catch (LineUnavailableException e) {

        }
    }
}
