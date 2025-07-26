package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Boss extends Enemy {

    private Bomb bomb;
    private int health = 20; // Boss requires 20 hits to defeat - much harder!
    private int frameCount = 0;
    private int horizontalDirection = 1;
    private int verticalDirection = 1;
    private static final int BOSS_SPEED = 2;
    private int attackMode = 0; // 0 = normal, 1 = aggressive, 2 = retreat, 3 = barrage, 4 = death spiral
    private int modeTimer = 0;
    
    // Progressive difficulty enhancements
    private double speedMultiplier = 1.0;
    private boolean invincible = false;
    private int invincibilityTimer = 0;
    private static final int INVINCIBILITY_DURATION = 30; // frames
    private double spiralAngle = 0.0;
    private int spiralCenterX;
    private int spiralCenterY;
    
    // Enhanced threat mechanics
    private int chargeTimer = 0;
    private boolean isCharging = false;
    private int chargeDirection = 1;
    private int originalX;
    private boolean wallAttackReady = false;
    private int wallAttackCooldown = 0;

    public Boss(int x, int y) {
        super(x, y);
        initBoss(x, y);
        // Initialize spiral center for death spiral mode
        spiralCenterX = x;
        spiralCenterY = y;
        originalX = x; // Store original position for charge attacks
    }

    private void initBoss(int x, int y) {
        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        // Use the UFO game enemy sprite for boss
        var ii = new ImageIcon("src/images/ufo_game_enemy.png");

        // Scale the boss to be appropriately sized (UFO is probably already large)
        int targetWidth = 80;  // Fixed size that looks good as boss
        int targetHeight = 60; // Fixed size that looks good as boss
        var scaledImage = ii.getImage().getScaledInstance(
                targetWidth, 
                targetHeight,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        
        // Enable animation for boss
        setAnimated(true);
        animationSpeed = 4; // Faster animation for boss
    }

    @Override
    public void act(int direction) {
        frameCount++;
        modeTimer++;
        
        // Handle invincibility frames
        if (invincible) {
            invincibilityTimer--;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }
        
        // Progressive speed increase based on health loss
        updateSpeedMultiplier();
        
        // Handle charge attack cooldown
        if (wallAttackCooldown > 0) {
            wallAttackCooldown--;
        }
        
        // Determine attack mode based on health thresholds (updated for 20 HP)
        if (health <= 4) { // 20% health - Death Spiral
            attackMode = 4;
        } else if (health <= 10) { // 50% health - Bomb Barrage mode
            attackMode = 3;
            // Enable wall attacks at 50% health
            if (wallAttackCooldown <= 0 && frameCount % 360 == 0) { // Every 6 seconds
                wallAttackReady = true;
                wallAttackCooldown = 300; // 5 second cooldown
            }
        } else if (health <= 6) {
            attackMode = 1; // Aggressive when low health
        } else if (health <= 12) {
            attackMode = 2; // Retreat mode
        }
        
        // Switch modes periodically (except for special modes)
        if (attackMode < 3 && modeTimer > 300) { // Change mode every 5 seconds
            attackMode = (attackMode + 1) % 3;
            modeTimer = 0;
        }
        
        // Movement based on attack mode
        switch (attackMode) {
            case 0: // Normal pattern
                normalMovement();
                break;
            case 1: // Aggressive pattern
                aggressiveMovement();
                break;
            case 2: // Retreat pattern
                retreatMovement();
                break;
            case 3: // Bomb barrage pattern
                barrageMovement();
                break;
            case 4: // Death spiral pattern
                deathSpiralMovement();
                break;
        }
        
        updateAnimation(); // Update animation frame
    }
    
    private void updateSpeedMultiplier() {
        // Increase speed by 50% every 25% health lost (updated for 20 HP)
        int healthLost = 20 - health;
        if (healthLost >= 16) { // 80% health lost (4 health remaining)
            speedMultiplier = 3.0; // 200% increase - INSANE SPEED
        } else if (healthLost >= 12) { // 60% health lost (8 health remaining)
            speedMultiplier = 2.5; // 150% increase
        } else if (healthLost >= 8) { // 40% health lost (12 health remaining)
            speedMultiplier = 2.0; // 100% increase
        } else if (healthLost >= 4) { // 20% health lost (16 health remaining)
            speedMultiplier = 1.5; // 50% increase
        } else {
            speedMultiplier = 1.0; // Normal speed
        }
    }
    
    private void normalMovement() {
        // Standard side-to-side movement with speed multiplier
        if (frameCount % 2 == 0) {
            this.x += horizontalDirection * (int)(BOSS_SPEED * speedMultiplier);
            
            // Use actual boss width for boundary detection
            int bossWidth = this.getImage() != null ? this.getImage().getWidth(null) : 100;
            if (this.x <= BORDER_LEFT || this.x >= BOARD_WIDTH - BORDER_RIGHT - bossWidth) {
                horizontalDirection = -horizontalDirection;
            }
        }
        
        // Gentle vertical movement
        if (frameCount % 180 == 0) {
            this.y += (int)(5 * speedMultiplier);
        }
    }
    
    private void aggressiveMovement() {
        // Faster, more erratic movement with speed multiplier
        if (frameCount % 1 == 0) { // Move every frame
            this.x += horizontalDirection * (int)(BOSS_SPEED * 2 * speedMultiplier);
            this.y += verticalDirection * (int)speedMultiplier;
            
            // Use actual boss width for boundary detection
            int bossWidth = this.getImage() != null ? this.getImage().getWidth(null) : 100;
            if (this.x <= BORDER_LEFT || this.x >= BOARD_WIDTH - BORDER_RIGHT - bossWidth) {
                horizontalDirection = -horizontalDirection;
            }
            
            if (this.y <= 50 || this.y >= 200) {
                verticalDirection = -verticalDirection;
            }
        }
    }
    
    private void retreatMovement() {
        // Try to stay away from bottom, move to top with speed multiplier
        if (frameCount % 3 == 0) {
            this.x += horizontalDirection * (int)(BOSS_SPEED * speedMultiplier);
            
            if (this.y > 80) {
                this.y -= (int)speedMultiplier; // Move upward
            }
            
            // Use actual boss width for boundary detection
            int bossWidth = this.getImage() != null ? this.getImage().getWidth(null) : 100;
            if (this.x <= BORDER_LEFT || this.x >= BOARD_WIDTH - BORDER_RIGHT - bossWidth) {
                horizontalDirection = -horizontalDirection;
            }
        }
    }
    
    private void barrageMovement() {
        // Slower, more deliberate movement for bomb barrage mode
        if (frameCount % 4 == 0) {
            this.x += horizontalDirection * (int)(BOSS_SPEED * 0.7 * speedMultiplier);
            
            // Use actual boss width for boundary detection
            int bossWidth = this.getImage() != null ? this.getImage().getWidth(null) : 100;
            if (this.x <= BORDER_LEFT || this.x >= BOARD_WIDTH - BORDER_RIGHT - bossWidth) {
                horizontalDirection = -horizontalDirection;
            }
        }
        
        // Slight vertical hovering
        if (frameCount % 60 == 0) {
            this.y += (this.y < 100) ? 2 : -2;
        }
    }
    
    private void deathSpiralMovement() {
        // Chaotic spiral movement for final phase
        spiralAngle += 0.2 * speedMultiplier;
        
        int radius = 80 + (int)(Math.sin(spiralAngle * 0.5) * 30); // Variable radius
        this.x = spiralCenterX + (int)(Math.cos(spiralAngle) * radius);
        this.y = spiralCenterY + (int)(Math.sin(spiralAngle) * radius * 0.6); // Elliptical
        
        // Keep boss on screen
        int bossWidth = this.getImage() != null ? this.getImage().getWidth(null) : 100;
        if (this.x < BORDER_LEFT) this.x = BORDER_LEFT;
        if (this.x > BOARD_WIDTH - BORDER_RIGHT - bossWidth) this.x = BOARD_WIDTH - BORDER_RIGHT - bossWidth;
        if (this.y < 50) this.y = 50;
        if (this.y > 250) this.y = 250;
        
        // Update spiral center occasionally
        if (frameCount % 180 == 0) {
            spiralCenterX = BOARD_WIDTH / 2 + (int)(Math.random() * 200 - 100);
            spiralCenterY = 120 + (int)(Math.random() * 80);
        }
    }

    public Bomb getBomb() {
        return bomb;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage() {
        if (!invincible) { // Only take damage if not invincible
            health--;
            invincible = true;
            invincibilityTimer = INVINCIBILITY_DURATION;
            
            if (health <= 0) {
                setDying(true);
            }
        }
    }

    public boolean isDefeated() {
        return health <= 0;
    }

    // Boss drops bombs more frequently with different patterns
    @Override
    public Bomb dropBomb() {
        // Get actual boss size for better bomb positioning
        int bossWidth = this.getImage().getWidth(null);
        int bossHeight = this.getImage().getHeight(null);
        
        Bomb bomb = new Bomb(this.x + bossWidth/2, this.y + bossHeight, true); // Boss bomb = larger
        bomb.setDestroyed(false);
        return bomb;
    }
    
    // Multiple bomb drop for aggressive mode
    public Bomb[] dropMultipleBombs() {
        Bomb[] bombs = new Bomb[3];
        int bossWidth = this.getImage().getWidth(null);
        int bossHeight = this.getImage().getHeight(null);
        
        // Drop 3 large bombs spread across boss width
        bombs[0] = new Bomb(this.x + bossWidth/4, this.y + bossHeight, true);         // Left
        bombs[1] = new Bomb(this.x + bossWidth/2, this.y + bossHeight, true);         // Center
        bombs[2] = new Bomb(this.x + (3 * bossWidth)/4, this.y + bossHeight, true);   // Right
        
        for (Bomb bomb : bombs) {
            bomb.setDestroyed(false);
        }
        
        return bombs;
    }
    
    // Bomb barrage attack - 5 bombs in spread pattern
    public Bomb[] dropBombBarrage() {
        Bomb[] bombs = new Bomb[5];
        int bossWidth = this.getImage().getWidth(null);
        int bossHeight = this.getImage().getHeight(null);
        int centerX = this.x + bossWidth/2;
        int centerY = this.y + bossHeight;
        
        // Create 5 large bombs in a wide spread pattern
        bombs[0] = new Bomb(centerX - 60, centerY, true);     // Far left
        bombs[1] = new Bomb(centerX - 30, centerY, true);     // Left
        bombs[2] = new Bomb(centerX, centerY, true);          // Center
        bombs[3] = new Bomb(centerX + 30, centerY, true);     // Right
        bombs[4] = new Bomb(centerX + 60, centerY, true);     // Far right
        
        for (Bomb bomb : bombs) {
            bomb.setDestroyed(false);
        }
        
        return bombs;
    }
    
    // Bomb wall attack - horizontal line of bombs
    public Bomb[] dropBombWall() {
        Bomb[] bombs = new Bomb[7];
        int bossHeight = this.getImage().getHeight(null);
        int centerY = this.y + bossHeight;
        
        // Create 7 large bombs across the screen width
        for (int i = 0; i < 7; i++) {
            int x = BORDER_LEFT + (i * (BOARD_WIDTH - BORDER_LEFT - BORDER_RIGHT) / 6);
            bombs[i] = new Bomb(x, centerY, true); // All boss bombs are larger
            bombs[i].setDestroyed(false);
        }
        
        return bombs;
    }
    
    public int getAttackMode() {
        return attackMode;
    }
    
    public boolean isInvincible() {
        return invincible;
    }
    
    public boolean shouldFlash() {
        // Flash every 4 frames while invincible for visual feedback
        return invincible && (invincibilityTimer / 4) % 2 == 0;
    }
    
    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
    
    public boolean isWallAttackReady() {
        return wallAttackReady;
    }
    
    public void resetWallAttack() {
        wallAttackReady = false;
    }
    
    public int getBombSpeed() {
        // Bombs get faster as boss gets angrier
        if (health <= 4) return 8;  // Death spiral - VERY fast bombs
        if (health <= 8) return 6;  // Low health - fast bombs
        if (health <= 12) return 4; // Medium health - medium speed
        return 3; // High health - normal speed
    }
}