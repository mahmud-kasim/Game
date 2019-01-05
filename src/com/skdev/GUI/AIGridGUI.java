package com.skdev.GUI;

// Grid do Player
import com.skdev.classes.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AIGridGUI extends JPanel {

    ArrayList<BSButton> buttons = new ArrayList<BSButton>();
    ArrayList<Ship> allShips = new ArrayList<Ship>();
    ArrayList<SetShipsListener> listeners = new ArrayList<SetShipsListener>();
    int[] testLocations;
    int numOfGuesses = 0;
    String text;
    int rows;
    int columns;
    boolean clicked = false;
    boolean endGame = false;
    boolean[] cellsGuessed;
    boolean[] cellsHit;
    boolean randomGuess = true;
    int firstHit;
    Color darkRed = new Color(100, 0, 0);
    Border loweredBevel = BorderFactory.createLoweredBevelBorder();
    Border defaultBorder;
    Ship shipToPlace;
    boolean vertical = false;
    boolean clear;
    boolean shipsPlaced = false;

    Ship destroyer = new Ship(2, "destroyer");
    Ship cruiser = new Ship(3, "cruiser");
    Ship submarine = new Ship(3, "submarine");
    Ship battleship = new Ship(4, "battleship");
    Ship aircraftCarrier = new Ship(5, "aircraft carrier");

    ArrayList<Direction> directions = new ArrayList<Direction>();
    Direction up = new Direction();
    Direction down = new Direction();
    Direction right = new Direction();
    Direction left = new Direction();

    public AIGridGUI(int r, int c) {

        rows = r;
        columns = c;

        cellsGuessed = new boolean[(rows * columns)];
        cellsHit = new boolean[(rows * columns)];
        for (int i = 0; i < (rows * columns); i++) {
            cellsGuessed[i] = false;
            cellsHit[i] = false;
        }

        allShips.add(destroyer);
        allShips.add(cruiser);
        allShips.add(submarine);
        allShips.add(battleship);
        allShips.add(aircraftCarrier);

        directions.add(up);
        directions.add(down);
        directions.add(right);
        directions.add(left);

        GridLayout g = new GridLayout(rows, columns);
        this.setLayout(g);

        for (int i = 0; i < (rows * columns); i++) {
            BSButton b = new BSButton();
            b.setGridLocation(i);
            buttons.add(b);
            this.add(b);
        }

        defaultBorder = buttons.get(0).getBorder();
    }

    public void autoPlaceShips() {

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

        for (BSButton bsb : buttons) {
            if (bsb.getCellContents() != null) {
                bsb.setBackground(Color.blue);
                bsb.setBorder(loweredBevel);
            }
            bsb.setEnabled(false);
        }

        text = "Ready to start the game.";
        shipsPlaced = true;
    }

    public void setTestLocations(int l) {
        vertical = new Random().nextBoolean();
        int x;
        int y;

        if (vertical) {
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

    public void placeShips() {
        for (int i = 0; i < buttons.size(); i++) {
            listeners.add(new SetShipsListener());
            buttons.get(i).addMouseListener(listeners.get(i));
        }

        shipToPlace = allShips.get(0);
        text = "Place " + shipToPlace.getName() + ". Right click to toggle horizontal/vertical.";
        try {
            FileWriter writer = new FileWriter("Text.txt");
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean getEndGame() {
        return endGame;
    }

    public void go() {

        int guessLocation = 0;
        boolean gameOver = true;
        numOfGuesses++;
        BSButton b = null;
        Ship s = null;
        boolean killed = false;

        boolean clear = false;
        int attempts = 1;

        if (randomGuess) {

            while (clear == false) {
                guessLocation = new Random().nextInt(cellsGuessed.length);

                if (cellsGuessed[guessLocation] == false) {
                    clear = true;
                }

                if (attempts < 5) {

                    int upLocation = guessLocation;
                    for (int i = 0; i < 2; i++) {
                        upLocation = moveUp(upLocation);
                        if (upLocation >= 0) {
                            if (cellsGuessed[upLocation]) {
                                clear = false;
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    int downLocation = guessLocation;
                    for (int i = 0; i < 2; i++) {
                        downLocation = moveDown(downLocation);
                        if (downLocation >= 0) {
                            if (cellsGuessed[downLocation]) {
                                clear = false;
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    int rightLocation = guessLocation;
                    for (int i = 0; i < 2; i++) {
                        rightLocation = moveRight(rightLocation);
                        if (rightLocation >= 0) {
                            if (cellsGuessed[rightLocation]) {
                                clear = false;
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    int leftLocation = guessLocation;
                    for (int i = 0; i < 2; i++) {
                        leftLocation = moveLeft(leftLocation);
                        if (leftLocation >= 0) {
                            if (cellsGuessed[leftLocation]) {
                                clear = false;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                attempts++;
            }
        } else {

            while (clear == false) {

                if (attempts == 1) {

                    int u = firstHit;
                    int upCount = -1;
                    while (u >= 0 && cellsHit[u]) {
                        u = moveUp(u);
                        upCount++;
                    }
                    up.setCell(u);
                    up.setCount(upCount);

                    int d = firstHit;
                    int downCount = -1;
                    while (d >= 0 && cellsHit[d]) {
                        d = moveDown(d);
                        downCount++;
                    }
                    down.setCell(d);
                    down.setCount(downCount);

                    int r = firstHit;
                    int rightCount = -1;
                    while (r >= 0 && cellsHit[r]) {
                        r = moveRight(r);
                        rightCount++;
                    }
                    right.setCell(r);
                    right.setCount(rightCount);

                    int l = firstHit;
                    int leftCount = -1;
                    while (l >= 0 && cellsHit[l]) {
                        l = moveLeft(l);
                        leftCount++;
                    }

                    left.setCell(l);
                    left.setCount(leftCount);

                    DirectionCompare dc = new DirectionCompare();
                    Collections.sort(directions, dc);
                    guessLocation = directions.get(0).getCell();
                }

                if (attempts == 2) {
                    guessLocation = directions.get(1).getCell();
                }

                if (attempts == 3) {
                    guessLocation = directions.get(2).getCell();
                }

                if (attempts == 4) {
                    guessLocation = directions.get(3).getCell();
                }

                if (attempts > 4) {
                    guessLocation = new Random().nextInt(cellsGuessed.length);
                }

                if (guessLocation >= 0) {
                    if (!cellsGuessed[guessLocation]) {
                        clear = true;
                    }
                }

                attempts++;
            }
        }

        cellsGuessed[guessLocation] = true;
        b = buttons.get(guessLocation);
        s = b.getCellContents();
        b.setBorder(loweredBevel);

        if (s == null) {
            text = "Other player missed. Your turn.";
            playSounds("splash");
            MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/splash.jpg")));
            b.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/splashIcon.png")));
            b.setBackground(Color.lightGray);
        } else {
            killed = s.counter();
            if (killed) {
                text = "Your " + s.getName() + " was sunk. Your turn.";
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

                int remainingHits = 0;
                for (BSButton bu : buttons) {
                    if (bu.getCellContents() == s) {
                        bu.setBackground(darkRed);
                        b.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/explosionIcon.png")));
                        cellsHit[bu.getGridLocation()] = false;
                    }
                    if (cellsHit[bu.getGridLocation()]) {
                        firstHit = bu.getGridLocation();
                        remainingHits++;
                    }
                }
                if (remainingHits == 0) {
                    randomGuess = true;
                }
            } else {
                text = "Other player got a hit. Your turn.";
                playSounds("hit");
                MainWindow.jLabelShip.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/01-kaboom.jpg")));
                b.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/com/skdev/resources/img/explosionIcon.png")));
                b.setBackground(Color.red);
                if (randomGuess) {
                    firstHit = b.getGridLocation();
                }
                randomGuess = false;
                cellsHit[guessLocation] = true;
            }
        }

        for (Ship sh : allShips) {
            if (sh.isKilled() == false) {
                gameOver = false;
            }
        }

        if (gameOver) {
            text = "You Lost in " + numOfGuesses + " guesses.";
            JOptionPane.showMessageDialog(null, "error " + numOfGuesses + " error");
            endGame = true;
        }

        try {
            FileWriter writer = new FileWriter("Text.txt");
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int moveUp(int u) {
        int dirUp = u - columns;
        if (dirUp < 0) {
            return -1;
        } else {
            return dirUp;
        }
    }

    public int moveDown(int d) {
        int dirDown = d + columns;
        if (dirDown >= (rows * columns)) {
            return -1;
        } else {
            return dirDown;
        }
    }

    public int moveRight(int r) {
        int dirRight = r + 1;
        if ((dirRight >= (rows * columns)) || (dirRight % columns == 0)) {
            return -1;
        } else {
            return dirRight;
        }
    }

    public int moveLeft(int l) {
        int dirLeft = l - 1;
        if ((dirLeft < 0) || (l % columns == 0)) {
            return -1;
        } else {
            return dirLeft;
        }
    }

    class DirectionCompare implements Comparator<Direction> {

        public int compare(Direction one, Direction two) {
            return ((Integer) two.getCount()).compareTo((Integer) one.getCount());
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
                    text = "Ready to start the game.";
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

    public boolean areShipsPlaced() {
        return shipsPlaced;
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
