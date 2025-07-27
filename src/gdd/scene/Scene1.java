package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Alien2;
import gdd.sprite.Bomb;
import gdd.sprite.Enemy;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;





public class Scene1 extends JPanel {

    private final List<Bomb> enemyBombs = new ArrayList<>();

    
    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;
    // private Shot shot;

    public int score = 0; // Score for the player, to be passed to BossFight
    private int playerSpeed = 5; // Player speed, can be modified by power-ups
    private int lives = 3; // Player lives
    private int killsRequired = 15; // Kills needed to complete stage (reduced for better balance)
    private int currentKills = 0; // Current kill count

    //private int shotType = player.getShotType(); // 1 = single, 2 = double, 3 = triple, etc.
    
    // shotType is declared in Player.java

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private boolean bossFightStarted = false;
    private boolean sceneStarted = false;

    private int currentRow = -1;
    private int mapOffset = 0;
    private int[][] MAP;

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    public Scene1(Game game) {
        this.game = game;
        // Load stage data from CSV files
        loadSpawnDetails();
        MAP = gdd.CSVLoader.loadStageMap("src/data/stage1_map.csv");
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/scene1.wav";
            audioPlayer = new AudioPlayer(filePath);

            System.out.println("Playing Scene 1 audio");
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetails() {
        // Load spawn details from CSV file
        spawnMap = gdd.CSVLoader.loadSpawnMap("src/data/stage1_spawns_balanced.csv");
    }

    private void initBoard() {

    }

    public void start() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();
        sceneStarted = true;

        gameInit();
        initAudio();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            System.out.println("Stopping Scene 1 audio");
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
        sceneStarted = false;
    }
    
    public boolean isStarted() {
        return sceneStarted;
    }
    
    public void restartScene() {
        // Reset game state
        inGame = true;
        deaths = 0;
        score = 0;
        frame = 0;
        bossFightStarted = false;
        playerSpeed = 5; // Reset player speed
        lives = 3; // Reset lives
        currentKills = 0; // Reset kill count
        
        // Clear all existing objects
        if (enemies != null) enemies.clear();
        if (shots != null) shots.clear();
        if (powerups != null) powerups.clear();
        if (explosions != null) explosions.clear();
        if (enemyBombs != null) enemyBombs.clear();
        
        // Reinitialize game objects
        gameInit();
        
        // Ensure player is properly reset
        if (player != null) {
            player.reset(); // Reset all player state
        }
        
        // Restart timer if needed
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }
    }

    private void gameInit() {

        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();

        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 6; j++) {
        // var enemy = new Enemy(ALIEN_INIT_X + (ALIEN_WIDTH + ALIEN_GAP) * j,
        // ALIEN_INIT_Y + (ALIEN_HEIGHT + ALIEN_GAP) * i);
        // enemies.add(enemy);
        // }
        // }
        player = new Player();
        // shot = new Shot();
    }

    private void drawMap(Graphics g) {
        // Draw scrolling starfield background

        // Calculate smooth scrolling offset (1 pixel per frame)
        int scrollOffset = (frame) % BLOCKHEIGHT;

        // Calculate which rows to draw based on screen position
        int baseRow = (frame) / BLOCKHEIGHT;
        int rowsNeeded = (BOARD_HEIGHT / BLOCKHEIGHT) + 2; // +2 for smooth scrolling

        // Loop through rows that should be visible on screen
        for (int screenRow = 0; screenRow < rowsNeeded; screenRow++) {
            // Calculate which MAP row to use (with wrapping)
            int mapRow = (baseRow + screenRow) % MAP.length;

            // Calculate Y position for this row
            // int y = (screenRow * BLOCKHEIGHT) - scrollOffset;
            int y = BOARD_HEIGHT - ( (screenRow * BLOCKHEIGHT) - scrollOffset );

            // Skip if row is completely off-screen
            if (y > BOARD_HEIGHT || y < -BLOCKHEIGHT) {
                continue;
            }

            // Draw each column in this row
            for (int col = 0; col < MAP[mapRow].length; col++) {
                if (MAP[mapRow][col] == 1) {
                    // Calculate X position
                    int x = col * BLOCKWIDTH;

                    // Draw a cluster of stars
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }

    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        // Set star color to white
        g.setColor(Color.WHITE);

        // Draw multiple stars in a cluster pattern
        // Main star (larger)
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        // Smaller surrounding stars
        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        // Tiny stars for more detail
        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    private void drawAliens(Graphics g) {

        for (Enemy enemy : enemies) {

            if (enemy.isVisible()) {
                // Apply proper sprite clipping animation
                if (enemy.isAnimated()) {
                    // Use sprite clipping for animation frames
                    int[] clip = enemy.getFrameClip();
                    g.drawImage(enemy.getImage(),
                               enemy.getX(), enemy.getY(),                           // destination
                               enemy.getX() + clip[2], enemy.getY() + clip[3],       // destination bounds
                               clip[0], clip[1],                                     // source start
                               clip[0] + clip[2], clip[1] + clip[3],                 // source bounds
                               this);
                } else {
                    g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
                }
            }

            if (enemy.isDying()) {
                enemy.die();
            }
        }
    }

    private void drawPowreUps(Graphics g) {

        for (PowerUp p : powerups) {

            if (p.isVisible()) {

                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }

            if (p.isDying()) {

                p.die();
            }
        }
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {
            // Apply proper sprite clipping animation for player
            if (player.isAnimated()) {
                // Use sprite clipping for animation frames
                int[] clip = player.getFrameClip();
                g.drawImage(player.getImage(),
                           player.getX(), player.getY(),                           // destination
                           player.getX() + clip[2], player.getY() + clip[3],       // destination bounds
                           clip[0], clip[1],                                       // source start
                           clip[0] + clip[2], clip[1] + clip[3],                   // source bounds
                           this);
            } else {
                g.drawImage(player.getImage(), player.getX(), player.getY(), this);
            }
        }

        if (player.isDying()) {
            lives--;
            if (lives <= 0) {
                player.die();
                inGame = false;
                message = "Game Over - No Lives Left";
            } else {
                // Reset player but continue game
                player.setDying(false);
                
                // CRITICAL: Reset player image back to normal (was set to explosion)
                player.fullReset(); // This restores the player image and position
                
                // Clear enemy bombs near player
                enemyBombs.removeIf(bomb -> 
                    Math.abs(bomb.getX() - player.getX()) < 100 && 
                    Math.abs(bomb.getY() - player.getY()) < 100);
            }
        }
    }

    private void drawShot(Graphics g) {

        for (Shot shot : shots) {

            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {
        for (Bomb bomb : enemyBombs) {
            if (!bomb.isDestroyed()) {
                g.drawImage(bomb.getImage(), bomb.getX(), bomb.getY(), this);
            }
        }

    }

    private void drawExplosions(Graphics g) {

        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {

            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 10, 10);

        String Stage = "Level 1";
        g.setColor(Color.green);
        // g.setFont(g.getFont().deriveFont(20f));
        g.drawString(Stage, 10, 30);

        g.setColor(Color.green);

        if (inGame) {

            drawMap(g);  // Draw background stars first
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Score: " + score, 10, 50);
        g.drawString("Lives: " + lives, 10, 70);
        g.drawString("Kills: " + currentKills + "/" + killsRequired, 10, 90);
        g.drawString("Speed: " + playerSpeed, 10, 110);
        g.drawString("Shot Type: " + player.getShotType(), 10, 130);



        Toolkit.getDefaultToolkit().sync();

    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_WIDTH / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_WIDTH / 2);

        // Add retry option for game over (not for victory)
        if (!message.equals("Stage 1 Complete! Level 2 Unlocked!")) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Helvetica", Font.BOLD, 12));
            String retryText = "Press R to Retry or ESC to Level Select";
            int retryWidth = g.getFontMetrics().stringWidth(retryText);
            g.drawString(retryText, (BOARD_WIDTH - retryWidth) / 2, BOARD_WIDTH / 2 + 30);
        } else {
            // Victory screen options
            g.setColor(Color.green);
            g.setFont(new Font("Helvetica", Font.BOLD, 12));
            String victoryText = "Press SPACE for Level 2 or ESC for Level Select";
            int victoryWidth = g.getFontMetrics().stringWidth(victoryText);
            g.drawString(victoryText, (BOARD_WIDTH - victoryWidth) / 2, BOARD_WIDTH / 2 + 30);
        }
    }

    private void update() {

        


        // Check enemy spawn
        // TODO this approach can only spawn one enemy at a frame
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y);
                    enemies.add(enemy);
                    break;
                // Add more cases for different enemy types if needed
                case "Alien2":
                    Enemy enemy2 = new Alien2(sd.x, sd.y);
                    enemies.add(enemy2);
                    break;
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                case "PowerUp-MultiShot":
                    PowerUp multiShot = new MultiShot(sd.x, sd.y);
                    powerups.add(multiShot);
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }
        // Check if stage is completed based on kills
        if (currentKills >= killsRequired) {
            inGame = false;
            timer.stop();
            message = "Stage 1 Complete! Level 2 Unlocked!";
            
            // Update game state
            gdd.GameState.getInstance().setLevel1Completed(true);
            gdd.GameState.getInstance().updateHighScore(score);
        }

        // player
        player.act();

        // Power-ups
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                    playerSpeed = player.getSpeed(); // Update speed if powerup affects speed
                    // shotType = Math.min(shotType + 1, 3); // Testing shotType with power-ups for now. Later shot upgrade sprint will be implemented

                }
            }
        }

        // Enemies
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
            }
        }

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX)
                            && shotX <= (enemyX + ALIEN_WIDTH)
                            && shotY >= (enemyY)
                            && shotY <= (enemyY + ALIEN_HEIGHT)) {

                        var ii = new ImageIcon(IMG_EXPLOSION);
                        enemy.setImage(ii.getImage());
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        score += 10;  // Add score when enemy is hit
                        currentKills++; // Increment kill count
                        System.out.println("Score: " + score + ", Kills: " + currentKills);
                        deaths++;
                        shot.die();
                        shotsToRemove.add(shot);
                    }
                }

                // int y = shot.getY();
                // // y -= 4;
                // y -= 20;

                // if (y < 0) {
                //     shot.die();
                //     shotsToRemove.add(shot);
                // } else {
                //     shot.setY(y);
                // }
                shot.act();

                if (!shot.isVisible()) {
                    shotsToRemove.add(shot);
                }

            }
        }
        shots.removeAll(shotsToRemove);

        // enemies
        for (Enemy enemy : enemies) {
            int x = enemy.getX();
            if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
                direction = -1;
                for (Enemy e2 : enemies) {
                    e2.setY(e2.getY() + GO_DOWN);
                }
            }
            if (x <= BORDER_LEFT && direction != 1) {
                direction = 1;
                for (Enemy e : enemies) {
                    e.setY(e.getY() + GO_DOWN);
                }
            }
        }
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                int y = enemy.getY();
                if (y > GROUND - ALIEN_HEIGHT) {
                    // Enemy reached bottom - lose a life
                    lives--;
                    enemy.die(); // Remove the enemy
                    if (lives <= 0) {
                        inGame = false;
                        message = "Game Over - Enemies Invaded!";
                    }
                }
                enemy.act(direction);
            }
        }

        

        // bombs - collision detection
        // Bomb is with enemy, so it loops over enemies

        for (Enemy enemy : enemies) {
            if (!enemy.isVisible()) continue;

            int chance = randomizer.nextInt(600); // Much lower bomb frequency
            if (chance == CHANCE) {
                Bomb bomb = new Bomb(enemy.getX(), enemy.getY());
                bomb.setDestroyed(false);
                enemyBombs.add(bomb);
                // System.out.println("Dropping bomb at: " + enemy.getX() + ", " + enemy.getY()); // Commented out - too verbose
            }
        }

        // Bomb update and collision logic
        Iterator<Bomb> iterator = enemyBombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();

            if (bomb.isDestroyed()) continue;

            // System.out.println("Updating bomb at: " + bomb.getX() + ", " + bomb.getY()); // Commented out - too verbose


            bomb.setY(bomb.getY() + 2); // Slower bomb movement

            // Collision with player
            if (player.isVisible()) {
                int bombX = bomb.getX();
                int bombY = bomb.getY();
                int playerX = player.getX();
                int playerY = player.getY();

                if (bombX >= playerX && bombX <= (playerX + PLAYER_WIDTH)
                && bombY >= playerY && bombY <= (playerY + PLAYER_HEIGHT)) {
                    player.setImage(new ImageIcon(IMG_EXPLOSION).getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                }
            }

            // Hit the ground
            if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                bomb.setDestroyed(true);
            }
        }

 
    }

    private void doGameCycle() {
        frame++;
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
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("Scene1.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            // Shooting Only one

            // if (key == KeyEvent.VK_SPACE && inGame) {
            //     System.out.println("Shots: " + shots.size());
            //     if (shots.size() < 4) {
            //         for (int i = 0; i < shotType; i++) {
            //             // Spread out shots horizontally for visual clarity
            //             int offsetX = (i - (shotType - 1) / 2) * 10;
            //             shots.add(new Shot(x + offsetX, y));
            //         }
            //     }
            // }

            // Shooting with shotType
            
            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                if (shots.size() < 4) {
                    if (player.getShotType() == 1) {
                        // One straight shot
                        shots.add(new Shot(x, y, 0, -20));
                    } else if (player.getShotType() == 2) {
                        // Two diagonal shots
                        shots.add(new Shot(x - 10, y, -5, -20)); // left
                        shots.add(new Shot(x + 10, y, 5, -20));  // right
                    } else if (player.getShotType() >= 3) {
                        // Two diagonals + one center
                        shots.add(new Shot(x, y, 0, -20));       // center
                        shots.add(new Shot(x - 10, y, -5, -20)); // left
                        shots.add(new Shot(x + 10, y, 5, -20));  // right
                    }
                }
            }
            
            // Game over controls
            if (!inGame) {
                if (message.equals("Stage 1 Complete! Level 2 Unlocked!")) {
                    // Victory screen controls
                    if (key == KeyEvent.VK_SPACE) {
                        // Go to boss fight
                        stop();
                        game.bossfight();
                    } else if (key == KeyEvent.VK_ESCAPE) {
                        // Return to level select
                        stop();
                        game.loadLevelSelect();
                    }
                } else {
                    // Game over screen controls
                    if (key == KeyEvent.VK_R) {
                        // Restart current scene
                        stop();
                        restartScene();
                    } else if (key == KeyEvent.VK_ESCAPE) {
                        // Return to level select
                        stop();
                        game.loadLevelSelect();
                    }
                }
            }


        }
    }
}
