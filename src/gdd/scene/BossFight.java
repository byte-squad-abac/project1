package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Boss;
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




public class BossFight extends JPanel {


    
    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<Bomb> bossBombs;
    private Player player;
    private Boss boss;
    private boolean bossSpawned = false;
    // private Shot shot;


    public int score; // Score is passed from Scene1
    private int playerSpeed = 5; // Player speed is reset to 5 for boss level
    private int lives = 3; // Player lives for boss fight

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();
    
    // Screen shake and arena effects
    private int shakeX = 0;
    private int shakeY = 0;
    private int shakeTimer = 0;
    private int arenaWidth = BOARD_WIDTH;
    private int arenaLeft = 0;

    private Timer timer;
    private final Game game;

    private int currentRow = -1;
    private int mapOffset = 0;
    private int[][] MAP;

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    public BossFight(Game game, int score) {
        this.game = game;
        this.score = score; // get the score from Scene1, ref in Game.java
        // Load stage data from CSV files
        loadSpawnDetails();
        MAP = gdd.CSVLoader.loadStageMap("src/data/stage1_map.csv"); // Reuse same background
    }

    private void initAudio() {
        try {
            if (audioPlayer != null) {
            audioPlayer.stop();
        }
            String filePath = "src/audio/scene1.wav";
            // need boss fight music. Add later

            audioPlayer = new AudioPlayer(filePath);
            System.out.println("Playing Boss Fight audio");
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    private void loadSpawnDetails() {
        // Load boss fight spawn details from CSV file
        spawnMap = gdd.CSVLoader.loadSpawnMap("src/data/boss_spawns.csv");
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
                System.out.println("Stopping Boss Fight audio");
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    
    public void restartBoss() {
        // Reset game state
        inGame = true;
        deaths = 0;
        frame = 0;
        shakeTimer = 0;
        shakeX = 0;
        shakeY = 0;
        arenaWidth = BOARD_WIDTH;
        arenaLeft = 0;
        bossSpawned = false;
        boss = null;
        playerSpeed = 5; // Reset player speed
        lives = 3; // Reset lives for boss fight
        
        // Reset score to starting value (keep score from Scene1)
        // score remains the same as passed from Scene1
        
        // Clear all existing objects
        if (enemies != null) enemies.clear();
        if (shots != null) shots.clear();
        if (powerups != null) powerups.clear();
        if (explosions != null) explosions.clear();
        if (bossBombs != null) bossBombs.clear();
        
        // Reinitialize game objects
        gameInit();
        
        // Ensure player is properly reset
        if (player != null) {
            player.fullReset(); // Use full reset including image
            System.out.println("[DEBUG] Player reset in restartBoss - Visible: " + player.isVisible() + ", Image: " + (player.getImage() != null ? "EXISTS" : "NULL"));
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
        bossBombs = new ArrayList<>();

        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 6; j++) {
        // var enemy = new Enemy(ALIEN_INIT_X + (ALIEN_WIDTH + ALIEN_GAP) * j,
        // ALIEN_INIT_Y + (ALIEN_HEIGHT + ALIEN_GAP) * i);
        // enemies.add(enemy);
        // }
        // }
        player = new Player();
        System.out.println("[DEBUG] Created new player in gameInit - Visible: " + player.isVisible() + ", Image: " + (player.getImage() != null ? "EXISTS" : "NULL"));
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
                // Apply animation effects if sprite is animated
                if (enemy.isAnimated()) {
                    // Enhanced pulsing for boss
                    int animFrame = enemy.getAnimationFrame();
                    if (enemy instanceof Boss) {
                        Boss bossEnemy = (Boss) enemy;
                        
                        // Flash red when invincible
                        if (bossEnemy.shouldFlash()) {
                            g.setColor(Color.RED);
                            g.fillRect(enemy.getX() - 2, enemy.getY() - 2, 
                                      enemy.getImage().getWidth(null) + 4,
                                      enemy.getImage().getHeight(null) + 4);
                        }
                        
                        // Use sprite clipping for boss animation
                        int[] clip = enemy.getFrameClip();
                        g.drawImage(enemy.getImage(),
                                   enemy.getX(), enemy.getY(),                           // destination
                                   enemy.getX() + clip[2], enemy.getY() + clip[3],       // destination bounds
                                   clip[0], clip[1],                                     // source start
                                   clip[0] + clip[2], clip[1] + clip[3],                 // source bounds
                                   this);
                    } else {
                        // Regular enemy animation with sprite clipping
                        int[] clip = enemy.getFrameClip();
                        g.drawImage(enemy.getImage(),
                                   enemy.getX(), enemy.getY(),                           // destination
                                   enemy.getX() + clip[2], enemy.getY() + clip[3],       // destination bounds
                                   clip[0], clip[1],                                     // source start
                                   clip[0] + clip[2], clip[1] + clip[3],                 // source bounds
                                   this);
                    }
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
        // Debug: Always show player state every second
        if (frame % 60 == 0) { 
            System.out.println("[DEBUG] Drawing player - Visible: " + player.isVisible() + ", Dying: " + player.isDying() + ", Position: (" + player.getX() + ", " + player.getY() + "), Image: " + (player.getImage() != null ? "EXISTS" : "NULL"));
        }

        if (player.isVisible()) {
            if (player.getImage() != null) {
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
            } else {
                System.out.println("[ERROR] Player visible but image is NULL!");
                // Draw a red rectangle as fallback
                g.setColor(Color.RED);
                g.fillRect(player.getX(), player.getY(), 30, 20);
            }
        } else {
            // Debug: Show when player is not visible
            if (frame % 60 == 0) {
                System.out.println("[DEBUG] Player NOT VISIBLE - not drawing!");
            }
        }

        if (player.isDying()) {
            lives--;
            if (lives <= 0) {
                player.die();
                inGame = false;
                message = "Game Over - No Lives Left";
            } else {
                // Reset player but continue boss fight
                System.out.println("BEFORE RESET - Player visible: " + player.isVisible() + ", dying: " + player.isDying());
                
                // Use full reset method which handles image restoration
                player.fullReset();
                playerSpeed = 5; // Update local speed variable
                
                System.out.println("AFTER RESET - Player visible: " + player.isVisible() + ", position: (" + player.getX() + ", " + player.getY() + ")");
                
                // Clear nearby boss bombs to give player a chance
                bossBombs.removeIf(bomb -> 
                    Math.abs(bomb.getX() - player.getX()) < 100 && 
                    Math.abs(bomb.getY() - player.getY()) < 100);
                    
                // Brief screen shake when losing a life
                shakeTimer = 20;
                
                System.out.println("Player lost a life! Lives remaining: " + lives);
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
        // Legacy method - not used in boss fight
    }
    
    private void drawBossBombing(Graphics g) {
        for (Bomb bomb : bossBombs) {
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
        // Handle screen shake effect
        if (shakeTimer > 0) {
            shakeTimer--;
            shakeX = (int)(Math.random() * 8 - 4); // Random shake between -4 and 4
            shakeY = (int)(Math.random() * 8 - 4);
        } else {
            shakeX = 0;
            shakeY = 0;
        }
        
        // Apply screen shake to all drawing
        g.translate(shakeX, shakeY);

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);
        
        // Draw arena boundaries if shrunk
        if (arenaWidth < BOARD_WIDTH) {
            g.setColor(Color.RED);
            g.fillRect(0, 0, arenaLeft, BOARD_HEIGHT); // Left wall
            g.fillRect(arenaLeft + arenaWidth, 0, BOARD_WIDTH - (arenaLeft + arenaWidth), BOARD_HEIGHT); // Right wall
            
            // Add warning text about shrinking arena
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            String warningText = "ARENA SHRINKING! RED ZONES = INSTANT DEATH!";
            int textWidth = g.getFontMetrics().stringWidth(warningText);
            g.drawString(warningText, (BOARD_WIDTH - textWidth) / 2, BOARD_HEIGHT - 50);
        }

        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 10, 10);

        String Stage = "Boss Fight";
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
            drawBossBombing(g);

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
        g.drawString("Speed: " + playerSpeed, 10, 90);
        g.drawString("Shot Type: " + player.getShotType(), 10, 110);
        
        // Display boss health and attack mode if boss is active
        if (boss != null && boss.isVisible() && !boss.isDefeated()) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("BOSS HEALTH: " + boss.getHealth() + "/20", BOARD_WIDTH / 2 - 80, 30);
            
            // Show attack mode
            String modeText = "";
            Color modeColor = Color.white;
            switch (boss.getAttackMode()) {
                case 0:
                    modeText = "NORMAL MODE";
                    modeColor = Color.white;
                    break;
                case 1:
                    modeText = "AGGRESSIVE MODE";
                    modeColor = Color.red;
                    break;
                case 2:
                    modeText = "RETREAT MODE";
                    modeColor = Color.yellow;
                    break;
                case 3:
                    modeText = "BARRAGE MODE";
                    modeColor = Color.orange;
                    break;
                case 4:
                    modeText = "DEATH SPIRAL";
                    modeColor = Color.magenta;
                    break;
            }
            g.setColor(modeColor);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(modeText, BOARD_WIDTH / 2 - 60, 50);
        }

        Toolkit.getDefaultToolkit().sync();
        
        // Reset translation after drawing
        g.translate(-shakeX, -shakeY);
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

        // Add retry options
        if (message.equals("Boss defeated! Game Complete!")) {
            g.setColor(Color.green);
            g.setFont(new Font("Helvetica", Font.BOLD, 12));
            String victoryText = "Press ESC for Level Select";
            int victoryWidth = g.getFontMetrics().stringWidth(victoryText);
            g.drawString(victoryText, (BOARD_WIDTH - victoryWidth) / 2, BOARD_WIDTH / 2 + 30);
        } else {
            g.setColor(Color.yellow);
            g.setFont(new Font("Helvetica", Font.BOLD, 12));
            String retryText = "Press R to Retry Boss or ESC to Level Select";
            int retryWidth = g.getFontMetrics().stringWidth(retryText);
            g.drawString(retryText, (BOARD_WIDTH - retryWidth) / 2, BOARD_WIDTH / 2 + 30);
            
            // Show final lives count if game over
            if (message.equals("Game Over - No Lives Left")) {
                g.setColor(Color.red);
                g.setFont(new Font("Helvetica", Font.BOLD, 10));
                String livesText = "All lives lost fighting the boss!";
                int livesWidth = g.getFontMetrics().stringWidth(livesText);
                g.drawString(livesText, (BOARD_WIDTH - livesWidth) / 2, BOARD_WIDTH / 2 + 50);
            }
        }
    }

    private void update() {


        // Check enemy spawn
        // TODO this approach can only spawn one enemy at a frame
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "Boss":
                    if (!bossSpawned) {
                        boss = new Boss(sd.x, sd.y);
                        enemies.add(boss);
                        bossSpawned = true;
                        System.out.println("Boss spawned!");
                    }
                    break;
                case "PowerUp-SpeedUp":
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
        
        // Check if boss is defeated
        if (boss != null && boss.isDefeated()) {
            inGame = false;
            timer.stop();
            message = "Boss defeated! Game Complete!";
            stop();
            
            // Update game state
            gdd.GameState.getInstance().updateHighScore(score);
        }
        
        // Check red zone death EVERY frame (not just when boss takes damage)
        checkRedZoneDeath();

        // player
        player.act();

        // Power-ups
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                    playerSpeed = player.getSpeed(); // Update speed if powerup affects speed
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
                    
                    // Use actual sprite size for collision detection
                    int enemyWidth, enemyHeight;
                    if (enemy instanceof Boss) {
                        enemyWidth = enemy.getImage().getWidth(null);
                        enemyHeight = enemy.getImage().getHeight(null);
                    } else {
                        enemyWidth = ALIEN_WIDTH;
                        enemyHeight = ALIEN_HEIGHT;
                    }

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX)
                            && shotX <= (enemyX + enemyWidth)
                            && shotY >= (enemyY)
                            && shotY <= (enemyY + enemyHeight)) {

                        explosions.add(new Explosion(shotX, shotY));
                        
                        if (enemy instanceof Boss) {
                            Boss bossEnemy = (Boss) enemy;
                            // Only damage boss if not invincible
                            if (!bossEnemy.isInvincible()) {
                                bossEnemy.takeDamage();
                                score += 50;  // Higher score for hitting boss
                                
                                // Screen shake when boss takes damage
                                shakeTimer = 15; // Shake for 15 frames
                                
                                // Shrink arena as boss gets damaged
                                updateArenaSize();
                                
                                System.out.println("Boss hit! Health: " + bossEnemy.getHealth() + ", Score: " + score);
                                
                                if (bossEnemy.isDefeated()) {
                                    var ii = new ImageIcon(IMG_EXPLOSION);
                                    enemy.setImage(ii.getImage());
                                    enemy.setDying(true);
                                    score += 500; // Bonus for defeating boss
                                    System.out.println("Boss defeated! Final score: " + score);
                                }
                            }
                        } else {
                            var ii = new ImageIcon(IMG_EXPLOSION);
                            enemy.setImage(ii.getImage());
                            enemy.setDying(true);
                            score += 10;
                        }
                        
                        shot.die();
                        shotsToRemove.add(shot);
                    }
                }

                //int y = shot.getY();
                // y -= 4;
                //y -= 20;

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
                    inGame = false;
                    message = "Invasion!";
                }
                enemy.act(direction);
            }
        }
        // Boss bomb mechanics with different attack patterns
        if (boss != null && boss.isVisible()) {
            int attackMode = boss.getAttackMode();
            int bombChance = 0;
            
            switch (attackMode) {
                case 0: // Normal mode
                    bombChance = randomizer.nextInt(300);
                    if (bombChance == CHANCE) {
                        Bomb bomb = boss.dropBomb();
                        bossBombs.add(bomb);
                        System.out.println("Boss normal bomb at: " + bomb.getX() + ", " + bomb.getY());
                    }
                    break;
                case 1: // Aggressive mode - multiple bombs
                    bombChance = randomizer.nextInt(180);
                    if (bombChance == CHANCE) {
                        Bomb[] bombs = boss.dropMultipleBombs();
                        for (Bomb bomb : bombs) {
                            bossBombs.add(bomb);
                        }
                        System.out.println("Boss aggressive multi-bomb attack!");
                    }
                    break;
                case 2: // Retreat mode - fewer bombs
                    bombChance = randomizer.nextInt(450);
                    if (bombChance == CHANCE) {
                        Bomb bomb = boss.dropBomb();
                        bossBombs.add(bomb);
                        System.out.println("Boss retreat bomb at: " + bomb.getX() + ", " + bomb.getY());
                    }
                    break;
                case 3: // Barrage mode - 5 bomb spread
                    bombChance = randomizer.nextInt(240);
                    if (bombChance == CHANCE) {
                        Bomb[] bombs = boss.dropBombBarrage();
                        for (Bomb bomb : bombs) {
                            bossBombs.add(bomb);
                        }
                        System.out.println("Boss barrage attack - 5 bombs!");
                    }
                    break;
                case 4: // Death spiral - frequent single bombs
                    bombChance = randomizer.nextInt(120);
                    if (bombChance == CHANCE) {
                        Bomb bomb = boss.dropBomb();
                        bossBombs.add(bomb);
                        System.out.println("Boss death spiral bomb!");
                    }
                    break;
            }
            
            // Check for wall attack
            if (boss.isWallAttackReady()) {
                Bomb[] wallBombs = boss.dropBombWall();
                for (Bomb bomb : wallBombs) {
                    bossBombs.add(bomb);
                }
                boss.resetWallAttack();
                System.out.println("BOSS WALL ATTACK - INCOMING!");
                
                // Extra screen shake for wall attack
                shakeTimer = 30;
            }
        }

        // Update boss bombs
        Iterator<Bomb> bombIterator = bossBombs.iterator();
        while (bombIterator.hasNext()) {
            Bomb bomb = bombIterator.next();
            
            if (bomb.isDestroyed()) {
                bombIterator.remove();
                continue;
            }
            
            // Use boss's dynamic bomb speed
            int bombSpeed = boss != null ? boss.getBombSpeed() : 3;
            bomb.setY(bomb.getY() + bombSpeed);
            
            // Collision with player
            if (player.isVisible()) {
                int bombX = bomb.getX();
                int bombY = bomb.getY();
                int playerX = player.getX();
                int playerY = player.getY();

                if (bombX >= playerX && bombX <= (playerX + PLAYER_WIDTH)
                && bombY >= playerY && bombY <= (playerY + PLAYER_HEIGHT)) {
                    System.out.println("PLAYER HIT BY BOMB! Setting explosion image and dying=true");
                    player.setImage(new ImageIcon(IMG_EXPLOSION).getImage());
                    player.setDying(true);
                    bomb.setDestroyed(true);
                }
            }

            // Remove bomb if it hits the ground
            if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                bomb.setDestroyed(true);
            }
        }
        
        // Remove destroyed bombs
        bossBombs.removeIf(Bomb::isDestroyed);
    }
    
    private void checkRedZoneDeath() {
        if (boss != null && player.isVisible() && !player.isDying()) {
            // Calculate current arena bounds
            int healthLost = 20 - boss.getHealth();
            int shrinkAmount = healthLost * 15; // 15 pixels per health lost
            
            int currentArenaWidth = Math.max(BOARD_WIDTH - shrinkAmount, BOARD_WIDTH / 2);
            int currentArenaLeft = (BOARD_WIDTH - currentArenaWidth) / 2;
            
            // Check if arena has shrunk from original size
            if (currentArenaWidth < BOARD_WIDTH) {
                // Check if player is in red death zone (with better collision detection)
                int playerLeft = player.getX();
                int playerRight = player.getX() + PLAYER_WIDTH;
                int safeZoneLeft = currentArenaLeft;
                int safeZoneRight = currentArenaLeft + currentArenaWidth;
                
                boolean inRedZone = (playerLeft < safeZoneLeft) || (playerRight > safeZoneRight);
                
                if (inRedZone) {
                    // Player entered red zone - instant death!
                    System.out.println("PLAYER ENTERED RED DEATH ZONE! Player X: " + player.getX() + 
                                     ", Arena: " + currentArenaLeft + " to " + (currentArenaLeft + currentArenaWidth));
                    player.setImage(new ImageIcon(IMG_EXPLOSION).getImage());
                    player.setDying(true);
                    
                    // Extra screen shake for red zone death
                    shakeTimer = 25;
                }
            }
        }
    }
    
    private void updateArenaSize() {
        if (boss != null) {
            // Shrink arena based on boss health (more damage = smaller arena)
            int healthLost = 20 - boss.getHealth();
            int shrinkAmount = healthLost * 15; // 15 pixels per health lost
            
            arenaWidth = Math.max(BOARD_WIDTH - shrinkAmount, BOARD_WIDTH / 2); // Don't shrink below half
            arenaLeft = (BOARD_WIDTH - arenaWidth) / 2; // Center the arena
        }
        
        // bombs - collision detection
        // Bomb is with enemy, so it loops over enemies
        /*
        for (Enemy enemy : enemies) {

            int chance = randomizer.nextInt(15);
            Enemy.Bomb bomb = enemy.getBomb();

            if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(enemy.getX());
                bomb.setY(enemy.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()
                    && bombX >= (playerX)
                    && bombX <= (playerX + PLAYER_WIDTH)
                    && bombY >= (playerY)
                    && bombY <= (playerY + PLAYER_HEIGHT)) {

                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                bomb.setDestroyed(true);
            }

            if (!bomb.isDestroyed()) {
                bomb.setY(bomb.getY() + 1);
                if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                }
            }
        }
         */
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
            System.out.println("BossFight.keyPressed: " + e.getKeyCode());

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
                if (shots.size() < 5) {
                    if (player.getShotType() == 1) {
                        // One straight shot
                        shots.add(new Shot(x, y, 0, -20));
                    } else if (player.getShotType() == 2) {
                        // Two diagonal shots
                        shots.add(new Shot(x - 10, y, -5, -20)); // left
                        shots.add(new Shot(x + 10, y, 5, -20));  // right
                    } else if (player.getShotType() == 3) {
                        // Two diagonals + one center
                        shots.add(new Shot(x, y, 0, -20));       // center
                        shots.add(new Shot(x - 10, y, -5, -20)); // left
                        shots.add(new Shot(x + 10, y, 5, -20));  // right
                    }
                    else if (player.getShotType() == 4) {
                        shots.add(new Shot(x - 8, y, 0, -20)); // left straight
                        shots.add(new Shot(x + 8, y, 0, -20)); // right straight
                        // Two diagonal
                        shots.add(new Shot(x - 15, y, -5, -20)); // diagonal left
                        shots.add(new Shot(x + 15, y, 5, -20));  // diagonal right
                    }
                }
            }
            
            // Game over controls
            if (!inGame) {
                if (message.equals("Boss defeated! Game Complete!")) {
                    // Victory screen - only allow return to level select
                    if (key == KeyEvent.VK_ESCAPE) {
                        stop();
                        game.loadLevelSelect();
                    }
                } else {
                    // Game over screen controls
                    if (key == KeyEvent.VK_R) {
                        // Restart boss fight
                        restartBoss();
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
