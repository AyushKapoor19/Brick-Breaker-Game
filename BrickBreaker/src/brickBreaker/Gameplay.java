package brickBreaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;
import javax.swing.JPanel;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int totalBricks = 21;

    private Timer timer;
    private int delay = 8;

    private int playerX = 310;
    private int ballposX = 120;
    private int ballposY = 350;

    private int ballXdir = -2;
    private int ballYdir = -4;

    private MapGenerator map;

    public Gameplay() {
        map = new MapGenerator(3, 7);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBackground(g);
        map.draw((Graphics2D) g);
        drawBorders(g);
        drawScores(g);
        drawPaddle(g);
        drawBall(g);
        checkGameStatus(g);
    }

    private void drawBackground(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(1, 1, 692, 592);
    }

    private void drawBorders(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(691, 0, 3, 592);
    }

    private void drawScores(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("Score: " + score, 570, 40);
    }

    private void drawPaddle(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(playerX, 550, 100, 8);
    }

    private void drawBall(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval(ballposX, ballposY, 20, 20);
    }

    private void checkGameStatus(Graphics g) {
        if (totalBricks <= 0) {
            gameWon(g);
        } else if (ballposY > 570) {
            gameOver(g);
        }
    }

    private void gameWon(Graphics g) {
        play = false;
        ballXdir = 0;
        ballYdir = 0;

        g.setColor(Color.RED);
        g.setFont(new Font("serif", Font.BOLD, 30));
        g.drawString("You Won!", 260, 300);

        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Press Enter to Restart", 230, 350);
    }

    private void gameOver(Graphics g) {
        play = false;
        ballXdir = 0;
        ballYdir = 0;

        g.setColor(Color.RED);
        g.setFont(new Font("serif", Font.BOLD, 30));
        g.drawString("Game Over, Scores: " + score, 190, 300);

        g.setFont(new Font("serif", Font.BOLD, 20));
        g.drawString("Press Enter to Restart", 230, 350);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (play) {
            checkPaddleCollision();
            checkBrickCollision();
            updateBallPosition();
        }
        repaint();
    }

    private void checkPaddleCollision() {
        if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
            ballYdir = -ballYdir;
        }
    }

    private void checkBrickCollision() {
        A: for (int i = 0; i < map.map.length; i++) {
            for (int j = 0; j < map.map[0].length; j++) {
                if (map.map[i][j] > 0) {
                    Rectangle brickRect = new Rectangle(j * map.brickWidth + 80, i * map.brickHeight + 50,
                            map.brickWidth, map.brickHeight);
                    Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);

                    if (ballRect.intersects(brickRect)) {
                        handleBrickCollision(i, j, brickRect);
                        break A;
                    }
                }
            }
        }
    }

    private void handleBrickCollision(int i, int j, Rectangle brickRect) {
        map.setBrickValue(0, i, j);
        totalBricks--;
        score += 5;

        if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
            ballXdir = -ballXdir;
        } else {
            ballYdir = -ballYdir;
        }
    }

    private void updateBallPosition() {
        ballposX += ballXdir * 1.22;
        ballposY += ballYdir * 1.2;

        if (ballposX < 0 || ballposX > 670) {
            ballXdir = -ballXdir;
        }

        if (ballposY < 0) {
            ballYdir = -ballYdir;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveRight();
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft();
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            restartGame();
        }
    }

    private void moveRight() {
        if (playerX >= 600) {
            playerX = 600;
        } else {
            play = true;
            playerX += 20;
        }
    }

    private void moveLeft() {
        if (playerX < 10) {
            playerX = 10;
        } else {
            play = true;
            playerX -= 20;
        }
    }

    private void restartGame() {
        play = true;
        ballposX = 120;
        ballposY = 350;
        ballXdir = -2;
        ballYdir = -4;
        playerX = 310;
        score = 0;
        totalBricks = 21;
        map = new MapGenerator(3, 7);
        timer.stop();
        timer.start(); 
        repaint();
    }
}
