package game;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

class Game {
    private static int rows, columns;
    private static int numMines;
    private static boolean firstClicked, won;
    private static int redX, redY;
    private static Timer timer;
    private static TimerTask timerTask;
    private static int minesLeft, seconds, clickedCells;
    private static Cell[][] cells;

    public static void setLevel(int level) {
        clickedCells = 0;
        switch (level) {
            case 1:
                rows = 8;
                columns = 8;
                numMines = 10;
                break;
            case 2:
                rows = 16;
                columns = 16;
                numMines = 40;
                break;
            case 3:
                rows = 16;
                columns = 30;
                numMines = 99;
                break;
            default:
                break;
        }
    }

    public static void restartGame() {
        if (!Cell.isActive()) {
            Cell.setActive(true);
            if (!won) {
                cells[redX][redY].getButton().setBackground(GameView.getBackgroundColor());
            } else {
                won = false;
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j].setMined(false);
                cells[i][j].getButton().setContentAreaFilled(true);
                cells[i][j].getButton().setIcon(null);
                cells[i][j].clearText();
                cells[i][j].setClickedToFalse();
                cells[i][j].setFlaggedToFalse();
            }
        }
        GameView.getLabelMines().setText(Integer.toString(numMines));
        GameView.getLabelSeconds().setText("0");
        timer.cancel();
        seconds = 0;
        clickedCells = 0;
        minesLeft = numMines;
        firstClicked = false;
    }

    public static void initTimer() {
        seconds = 0;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                GameView.getLabelSeconds().setText(Integer.toString(++seconds));
            }
        };
    }

    public static void generateCells() {
        cells = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new Cell();
                GameView.getPanelMain().add(cells[i][j].makeCell(i, j));
            }
        }
    }

    public static void clickedNeighbours(int row, int col) {
        int value = cells[row][col].getValue();
        int numFlagged = 0;

        ArrayList<Coordinates> neighbours = findNeighbours(row, col);

        for (Coordinates neighbour : neighbours) {
            int x = neighbour.getRow();
            int y = neighbour.getCol();
            if (!cells[x][y].isClicked() && cells[x][y].isFlagged()) {
                numFlagged++;
            }
        }
        if (value == numFlagged) {
            for (Coordinates neighbour : neighbours) {
                int x = neighbour.getRow();
                int y = neighbour.getCol();
                if (!cells[x][y].isClicked() && !cells[x][y].isFlagged()) {
                    cells[x][y].getButton().doClick(0);
                }
            }
        }
    }

    private static ArrayList<Coordinates> findNeighbours(int row, int col) {
        ArrayList<Coordinates> neighbours = new ArrayList<>();
        if ((row > 0 && row < rows - 1) && (col > 0 && col < columns - 1)) {
            neighbours.add(new Coordinates(row - 1, col - 1));
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row + 1, col - 1));
            neighbours.add(new Coordinates(row - 1, col + 1));
            neighbours.add(new Coordinates(row, col + 1));
            neighbours.add(new Coordinates(row + 1, col + 1));
            neighbours.add(new Coordinates(row - 1, col));
            neighbours.add(new Coordinates(row + 1, col));
        } else if (row == 0 && col == 0) {
            neighbours.add(new Coordinates(0, 1));
            neighbours.add(new Coordinates(1, 0));
            neighbours.add(new Coordinates(1, 1));
        } else if (row == 0 && col == columns - 1) {
            neighbours.add(new Coordinates(0, col - 1));
            neighbours.add(new Coordinates(1, col - 1));
            neighbours.add(new Coordinates(1, col));
        } else if (row == rows - 1 && col == columns - 1) {
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row - 1, col - 1));
            neighbours.add(new Coordinates(row - 1, col));
        } else if (row == rows - 1 && col == 0) {
            neighbours.add(new Coordinates(row, 1));
            neighbours.add(new Coordinates(row - 1, 1));
            neighbours.add(new Coordinates(row - 1, 0));
        } else if (row == 0) {
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row, col + 1));
            neighbours.add(new Coordinates(row + 1, col - 1));
            neighbours.add(new Coordinates(row + 1, col));
            neighbours.add(new Coordinates(row + 1, col + 1));
        } else if (row == rows - 1) {
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row, col + 1));
            neighbours.add(new Coordinates(row - 1, col - 1));
            neighbours.add(new Coordinates(row - 1, col));
            neighbours.add(new Coordinates(row - 1, col + 1));
        } else if (col == 0) {
            neighbours.add(new Coordinates(row - 1, col));
            neighbours.add(new Coordinates(row + 1, col));
            neighbours.add(new Coordinates(row - 1, col + 1));
            neighbours.add(new Coordinates(row, col + 1));
            neighbours.add(new Coordinates(row + 1, col + 1));
        } else if (col == columns - 1) {
            neighbours.add(new Coordinates(row - 1, col));
            neighbours.add(new Coordinates(row + 1, col));
            neighbours.add(new Coordinates(row - 1, col - 1));
            neighbours.add(new Coordinates(row, col - 1));
            neighbours.add(new Coordinates(row + 1, col - 1));
        }
        return neighbours;
    }

    public static void addClickedCellsCounter() {
        clickedCells++;
    }

    public static void generateMines(int numMines, int row, int col) {
        Random random = new Random();
        ArrayList<Coordinates> temp = new ArrayList<>();

        if (Settings.isSaferFirstClick()) {
            temp = findNeighbours(row, col);
        }
        temp.add(new Coordinates(row, col));

        int generatedMinesCounter = 0;
        while (generatedMinesCounter < numMines) {
            Coordinates coordinate = convertPositionToCoordinates(random.nextInt(rows * columns));
            if (!temp.contains(coordinate)) {
                temp.add(coordinate);
                cells[coordinate.getRow()][coordinate.getCol()].setMined(true);
                generatedMinesCounter++;
            }
        }
        checkValues();
    }

    private static Coordinates convertPositionToCoordinates(int position) {
        int row = position / columns;
        while (position >= columns) {
            position %= columns;
        }
        int col = position;
        return new Coordinates(row, col);
    }

    private static void checkValues() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].isMined() == 0) {
                    int number = countNearbyMines(i, j);
                    cells[i][j].setColor(number);
                    cells[i][j].setValue(number);
                }
            }
        }
    }

    private static int countNearbyMines(int row, int col) {
        int count = 0;
        ArrayList<Coordinates> neighbours;
        neighbours = findNeighbours(row, col);
        for (Coordinates neighbour : neighbours) {
            count += cells[neighbour.getRow()][neighbour.getCol()].isMined();
        }
        return count;
    }

    public static void startTimer() {
        seconds = 0;
        timer = new Timer();
        timerTask.cancel();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GameView.getLabelSeconds().setText(Integer.toString(seconds++));
            }
        }, 0, 1000);
    }

    public static void revealNeighbours(int row, int col) {
        ArrayList<Coordinates> neighbours;
        neighbours = findNeighbours(row, col);
        for (Coordinates neighbour : neighbours) {
            int x = neighbour.getRow();
            int y = neighbour.getCol();
            if (!cells[x][y].isClicked()) {
                cells[x][y].getButton().doClick(0);
            }
        }
    }

    public static void checkWin() {
        if (clickedCells == rows * columns - numMines) {
            won = true;
            freezeGame();
            new Win(Integer.parseInt(GameView.getLabelSeconds().getText()), GameView.getWindowLocation(), Settings.getCurrentLevel());
        }
    }

    public static void freezeGame() {
        timer.cancel();
        Cell.setActive(false);
    }

    public static void makeBackgroundRed(int row, int col) {
        redX = row;
        redY = col;
        cells[row][col].getButton().setOpaque(true);
        cells[row][col].getButton().setBackground(Color.RED);
    }

    public static void revealMines() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].isMined() == 1 && !cells[i][j].isFlagged()) {
                    cells[i][j].getButton().setIcon(new ImageIcon(GameView.getImgMine()));
                }
            }
        }
    }

    public static void revealWrongFlagged() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j].isMined() == 0 && cells[i][j].isFlagged()) {
                    cells[i][j].setImgMineCrossed();
                }
            }
        }
    }

    public static void plusMine() {
        GameView.getLabelMines().setText(Integer.toString(++minesLeft));
    }

    public static void minusMine() {
        GameView.getLabelMines().setText(Integer.toString(--minesLeft));
    }

    public static void restartFrame(int level, Point point) {
        GameView.getFrame().removeAll();
        GameView.getFrame().dispose();
        timer.cancel();
        Settings.setCurrentLevel(level);
        new GameView(point);
    }

    public static void setSaferFirstClickText() {
        if (Settings.isSaferFirstClick()) {
            GameView.getSettingsSaferFirstClick().setText("\u221ASafer first click");
        } else {
            GameView.getSettingsSaferFirstClick().setText("Safer first click");
        }
    }

    public static void setSafeRevealText() {
        if (Settings.isSafeReveal()) {
            GameView.getSettingsSafeReveal().setText("\u221ASafe reveal");
        } else {
            GameView.getSettingsSafeReveal().setText("Safe reveal");
        }
    }

    public static void clickCheckForUpdates() {
        boolean upToDate;
        try {
            upToDate = VersionCheck.checkForNewestVersion();
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(GameView.getFrame(), "Unable to check for updates.");
            return;
        }
        String message;
        if (upToDate) {
            message = "You are using the latest version: " + VersionCheck.getCurrentVersion();
        } else {
            message = "There is a new version released.\nVisit github.com/exusar/Minesweeper/releases/latest to download.";
        }
        JOptionPane.showMessageDialog(GameView.getFrame(), message);
    }

    public static boolean isFirstClicked() {
        return firstClicked;
    }

    public static void setFirstClicked(boolean firstClicked) {
        Game.firstClicked = firstClicked;
    }

    public static int getNumMines() {
        return numMines;
    }

    public static int getRows() {
        return rows;
    }

    public static int getColumns() {
        return columns;
    }

    public static void setMinesLeft(int minesLeft) {
        Game.minesLeft = minesLeft;
    }

    public static void setWon(boolean won) {
        Game.won = won;
    }
}