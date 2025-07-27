package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import gdd.GameState;
import static gdd.Global.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public class LevelSelect extends JPanel {
    
    private int frame = 0;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private Game game;
    private int selectedLevel = 1;
    private GameState gameState;
    
    public LevelSelect(Game game) {
        this.game = game;
        this.gameState = GameState.getInstance();
        initBoard();
    }
    
    private void initBoard() {
        
    }
    
    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.black);
        
        // Force focus immediately and repeatedly until gained
        requestFocusInWindow();
        
        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();
        
        // Force focus again after a short delay
        Timer focusTimer = new Timer(100, e -> {
            if (!hasFocus()) {
                requestFocusInWindow();
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
        focusTimer.start();
    }
    
    public void stop() {
        try {
            if (timer != null) {
                timer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error stopping level select.");
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        
        // Title
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String title = "LEVEL SELECT";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (d.width - titleWidth) / 2, 100);
        
        // High Score
        g.setColor(Color.yellow);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String highScoreText = "High Score: " + gameState.getHighScore();
        int hsWidth = g.getFontMetrics().stringWidth(highScoreText);
        g.drawString(highScoreText, (d.width - hsWidth) / 2, 130);
        
        // Level 1
        drawLevelOption(g, 1, 200, gameState.isLevel1Completed());
        
        // Level 2 (Boss Fight)
        drawLevelOption(g, 2, 280, gameState.isLevel2Unlocked());
        
        // Instructions
        g.setColor(Color.gray);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        String[] instructions = {
            "Use UP/DOWN arrows to select level",
            "Press SPACE to start selected level",
            "Press ESC to return to title"
        };
        
        for (int i = 0; i < instructions.length; i++) {
            int instWidth = g.getFontMetrics().stringWidth(instructions[i]);
            g.drawString(instructions[i], (d.width - instWidth) / 2, 400 + (i * 20));
        }
        
        Toolkit.getDefaultToolkit().sync();
    }
    
    private void drawLevelOption(Graphics g, int level, int y, boolean unlocked) {
        String levelName = (level == 1) ? "Stage 1: Space Invaders" : "Stage 2: Boss Fight";
        String status = "";
        
        if (level == 1) {
            status = gameState.isLevel1Completed() ? " [COMPLETED]" : " [15 KILLS TO WIN]";
        } else {
            status = unlocked ? " [UNLOCKED]" : " [LOCKED]";
        }
        
        // Highlight selected level
        if (selectedLevel == level && unlocked) {
            g.setColor(Color.yellow);
            g.fillRect(50, y - 25, d.width - 100, 40);
            g.setColor(Color.black);
        } else if (selectedLevel == level && !unlocked) {
            g.setColor(Color.red);
        } else if (unlocked) {
            g.setColor(Color.white);
        } else {
            g.setColor(Color.gray);
        }
        
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String fullText = "Level " + level + ": " + levelName + status;
        int textWidth = g.getFontMetrics().stringWidth(fullText);
        g.drawString(fullText, (d.width - textWidth) / 2, y);
        
        if (!unlocked && selectedLevel == level) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            String lockText = "Complete previous level to unlock";
            int lockWidth = g.getFontMetrics().stringWidth(lockText);
            g.drawString(lockText, (d.width - lockWidth) / 2, y + 20);
        }
    }
    
    private void update() {
        frame++;
    }
    
    private void doGameCycle() {
        update();
        repaint();
    }
    
    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }
    
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            
            if (key == KeyEvent.VK_UP) {
                selectedLevel = Math.max(1, selectedLevel - 1);
            } else if (key == KeyEvent.VK_DOWN) {
                selectedLevel = Math.min(2, selectedLevel + 1);
            } else if (key == KeyEvent.VK_SPACE) {
                if (selectedLevel == 1) {
                    gameState.setCurrentLevel(1);
                    game.loadscene1();
                } else if (selectedLevel == 2 && gameState.isLevel2Unlocked()) {
                    gameState.setCurrentLevel(2);
                    game.bossfight();
                }
            } else if (key == KeyEvent.VK_ESCAPE) {
                stop();
                game.loadTitle();
            }
        }
    }
}