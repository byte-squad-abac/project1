package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Boss extends Enemy {

    private Bomb bomb;
    private int health = 100; // EPIC BOSS: 100 HP for 5-minute fight!
    private int maxHealth = 100;
    private int frameCount = 0;
    private int horizontalDirection = 1;
    private int verticalDirection = 1;
    private static final int BOSS_SPEED = 2;
    private int phase = 1; // 7 phases for epic fight
    private int attackMode = 0; // Multiple attack modes per phase
    private int modeTimer = 0;
    private int phaseTimer = 0;
    
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
    
    // NEW EPIC BOSS MECHANICS
    private int minionSpawnTimer = 0;
    private boolean shielded = false;
    private int shieldGeneratorsActive = 0;
    private boolean shieldPhaseCompleted = false;
    private boolean teleportReady = false;
    private int teleportCooldown = 0;
    private boolean isTeleporting = false;
    private int teleportTimer = 0;
    private int laserSweepTimer = 0;
    private int homingMissileTimer = 0;
    private int healingTimer = 0;
    private boolean healingPhase = false;
    private int environmentalHazardTimer = 0;
    private double teleportTargetX = 0;
    private double teleportTargetY = 0;

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
        phaseTimer++;
        
        // Handle invincibility frames
        if (invincible) {
            invincibilityTimer--;
            if (invincibilityTimer <= 0) {
                invincible = false;
            }
        }
        
        // Determine current phase based on health
        updatePhase();
        
        // Update all timers
        updateTimers();
        
        // Progressive speed increase based on health loss
        updateSpeedMultiplier();
        
        // Handle teleportation
        if (isTeleporting) {
            handleTeleportation();
            return; // Skip normal movement during teleport
        }
        
        // Phase-specific behaviors
        handlePhaseSpecificMechanics();
        
        // Movement based on current phase and attack mode
        handleMovement();
        
        updateAnimation(); // Update animation frame
    }
    
    private void updatePhase() {
        int oldPhase = phase;
        
        // 7 phases for epic boss fight
        if (health > 85) {
            phase = 1; // Awakening
        } else if (health > 70) {
            phase = 2; // First Assault
        } else if (health > 55) {
            phase = 3; // Shield Barrier
        } else if (health > 40) {
            phase = 4; // Minion Army
        } else if (health > 25) {
            phase = 5; // Laser Fury
        } else if (health > 10) {
            phase = 6; // Chaos Mode
        } else {
            phase = 7; // Death Spiral
            healingPhase = true; // Boss starts healing in final phase
        }
        
        // Reset shield phase completion when exiting Phase 3
        if (oldPhase == 3 && phase != 3) {
            shieldPhaseCompleted = false;
        }
    }
    
    private void updateTimers() {
        if (wallAttackCooldown > 0) wallAttackCooldown--;
        if (teleportCooldown > 0) teleportCooldown--;
        if (minionSpawnTimer > 0) minionSpawnTimer--;
        if (laserSweepTimer > 0) laserSweepTimer--;
        if (homingMissileTimer > 0) homingMissileTimer--;
        if (healingTimer > 0) healingTimer--;
        if (environmentalHazardTimer > 0) environmentalHazardTimer--;
    }
    
    private void handlePhaseSpecificMechanics() {
        switch (phase) {
            case 1: // Awakening - Tutorial phase
                handleAwakeningPhase();
                break;
            case 2: // First Assault - Minions start
                handleFirstAssaultPhase();
                break;
            case 3: // Shield Barrier - Invulnerability shields
                handleShieldBarrierPhase();
                break;
            case 4: // Minion Army - Heavy minion spawning
                handleMinionArmyPhase();
                break;
            case 5: // Laser Fury - Laser sweeps and homing missiles
                handleLaserFuryPhase();
                break;
            case 6: // Chaos Mode - All mechanics combined
                handleChaosModePhase();
                break;
            case 7: // Death Spiral - Final desperate phase
                handleDeathSpiralPhase();
                break;
        }
    }
    
    private void handleMovement() {
        // Determine attack mode based on phase and health
        if (phase >= 7) {
            attackMode = 4; // Death spiral
        } else if (phase >= 5) {
            attackMode = 3; // Barrage mode
        } else if (health <= maxHealth * 0.3) {
            attackMode = 1; // Aggressive
        } else if (modeTimer > 300) { // Change mode every 5 seconds
            attackMode = (attackMode + 1) % 3;
            modeTimer = 0;
        }
        
        // Movement based on attack mode
        switch (attackMode) {
            case 0: normalMovement(); break;
            case 1: aggressiveMovement(); break;
            case 2: retreatMovement(); break;
            case 3: barrageMovement(); break;
            case 4: deathSpiralMovement(); break;
        }
    }
    
    private void updateSpeedMultiplier() {
        // Progressive speed increase based on health percentage (for 100 HP)
        double healthPercent = (double) health / maxHealth;
        
        if (healthPercent <= 0.1) { // 10% health - INSANE SPEED
            speedMultiplier = 4.0;
        } else if (healthPercent <= 0.25) { // 25% health - VERY FAST
            speedMultiplier = 3.0;
        } else if (healthPercent <= 0.4) { // 40% health - FAST
            speedMultiplier = 2.5;
        } else if (healthPercent <= 0.55) { // 55% health - MEDIUM-FAST
            speedMultiplier = 2.0;
        } else if (healthPercent <= 0.7) { // 70% health - MEDIUM
            speedMultiplier = 1.5;
        } else if (healthPercent <= 0.85) { // 85% health - SLIGHTLY FAST
            speedMultiplier = 1.2;
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
    
    // ==================== EPIC BOSS PHASE HANDLERS ====================
    
    private void handleAwakeningPhase() {
        // Phase 1: Tutorial phase - basic attacks only
        // Standard bomb dropping every 3 seconds
        if (frameCount % 180 == 0) {
            wallAttackReady = true;
        }
    }
    
    private void handleFirstAssaultPhase() {
        // Phase 2: Minions start spawning
        if (minionSpawnTimer <= 0) {
            // Spawn 2-3 minions every 10 seconds
            minionSpawnTimer = 600; // 10 seconds
            // Signal to spawn minions (handled in boss fight scene)
        }
        
        // More frequent bomb attacks
        if (frameCount % 120 == 0) { // Every 2 seconds
            wallAttackReady = true;
        }
    }
    
    private void handleShieldBarrierPhase() {
        // Phase 3: Boss becomes shielded (only once per phase)
        if (!shielded && !shieldPhaseCompleted && phaseTimer > 60) { // 1 second after entering phase
            shielded = true;
            shieldGeneratorsActive = 3; // Signal to spawn 3 shield generators
        }
        
        // Continue attacking while shielded
        if (frameCount % 150 == 0) { // Every 2.5 seconds
            wallAttackReady = true;
        }
    }
    
    private void handleMinionArmyPhase() {
        // Phase 4: Minion spawning is now handled by Wave 1 style system in BossFight.java
        // Boss retreats to top and moves more defensively
        if (this.y > 60) {
            this.y -= (int)(speedMultiplier);
        }
        
        // Reduced direct attacks while minions are active
        if (frameCount % 240 == 0) { // Every 4 seconds
            wallAttackReady = true;
        }
    }
    
    private void handleLaserFuryPhase() {
        // Phase 5: Laser sweeps and homing missiles
        
        // Laser sweep every 8 seconds
        if (laserSweepTimer <= 0) {
            laserSweepTimer = 480; // 8 seconds
            // Signal to create laser sweep
        }
        
        // Homing missiles every 6 seconds
        if (homingMissileTimer <= 0) {
            homingMissileTimer = 360; // 6 seconds
            // Signal to spawn homing missiles
        }
        
        // Teleportation every 10 seconds
        if (teleportCooldown <= 0 && !isTeleporting) {
            teleportReady = true;
            teleportCooldown = 600; // 10 seconds
        }
        
        // Regular bomb barrage
        if (frameCount % 180 == 0) { // Every 3 seconds
            wallAttackReady = true;
        }
    }
    
    private void handleChaosModePhase() {
        // Phase 6: ALL mechanics combined
        
        // Frequent minion spawning
        if (minionSpawnTimer <= 0) {
            minionSpawnTimer = 360; // 6 seconds
        }
        
        // Frequent laser sweeps
        if (laserSweepTimer <= 0) {
            laserSweepTimer = 300; // 5 seconds
        }
        
        // Frequent homing missiles
        if (homingMissileTimer <= 0) {
            homingMissileTimer = 240; // 4 seconds
        }
        
        // Environmental hazards
        if (environmentalHazardTimer <= 0) {
            environmentalHazardTimer = 420; // 7 seconds
        }
        
        // Frequent teleportation
        if (teleportCooldown <= 0 && !isTeleporting) {
            teleportReady = true;
            teleportCooldown = 360; // 6 seconds
        }
        
        // Constant bomb barrage
        if (frameCount % 90 == 0) { // Every 1.5 seconds
            wallAttackReady = true;
        }
    }
    
    private void handleDeathSpiralPhase() {
        // Phase 7: Final desperate phase with healing
        
        // Boss healing every 15 seconds
        if (healingTimer <= 0) {
            healingTimer = 900; // 15 seconds
            if (health < maxHealth) {
                health += 2; // Heal 2 HP
                if (health > maxHealth) health = maxHealth;
            }
        }
        
        // Maximum frequency everything
        if (minionSpawnTimer <= 0) {
            minionSpawnTimer = 240; // 4 seconds
        }
        
        if (laserSweepTimer <= 0) {
            laserSweepTimer = 180; // 3 seconds
        }
        
        if (homingMissileTimer <= 0) {
            homingMissileTimer = 150; // 2.5 seconds
        }
        
        if (environmentalHazardTimer <= 0) {
            environmentalHazardTimer = 240; // 4 seconds
        }
        
        // Desperate teleportation
        if (teleportCooldown <= 0 && !isTeleporting) {
            teleportReady = true;
            teleportCooldown = 180; // 3 seconds
        }
        
        // Bullet hell - constant bombs
        if (frameCount % 60 == 0) { // Every 1 second
            wallAttackReady = true;
        }
    }
    
    private void handleTeleportation() {
        if (teleportTimer <= 0) {
            // Start teleport - disappear
            isTeleporting = true;
            teleportTimer = 60; // 1 second teleport duration
            
            // Calculate random teleport destination
            int bossWidth = this.getImage() != null ? this.getImage().getWidth(null) : 100;
            teleportTargetX = BORDER_LEFT + Math.random() * (BOARD_WIDTH - BORDER_LEFT - BORDER_RIGHT - bossWidth);
            teleportTargetY = 50 + Math.random() * 150; // Stay in upper area
        } else {
            teleportTimer--;
            if (teleportTimer <= 0) {
                // Complete teleport - reappear
                this.x = (int) teleportTargetX;
                this.y = (int) teleportTargetY;
                isTeleporting = false;
                teleportReady = false;
            }
        }
    }

    public Bomb getBomb() {
        return bomb;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage() {
        if (!invincible && !shielded) { // Only take damage if not invincible AND not shielded
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
        // Bombs get faster as boss gets angrier (updated for 100 HP)
        if (health <= 10) return 8;  // Death spiral - VERY fast bombs
        if (health <= 25) return 6;  // Low health - fast bombs
        if (health <= 40) return 4; // Medium health - medium speed
        return 3; // High health - normal speed
    }
    
    // ==================== NEW EPIC BOSS GETTERS ====================
    
    public int getPhase() {
        return phase;
    }
    
    public boolean shouldSpawnMinions() {
        return false; // Disabled - using Wave 1 style spawning in BossFight.java
    }
    
    public void resetMinionSpawnTimer() {
        if (phase == 2) {
            minionSpawnTimer = 900; // 15 seconds for phase 2 (increased from 10)
        } else if (phase == 4) {
            minionSpawnTimer = 720; // 12 seconds for phase 4 (increased from 8)
        } else if (phase >= 6) {
            minionSpawnTimer = phase == 7 ? 360 : 540; // 6s for phase 7, 9s for phase 6 (increased)
        }
    }
    
    public int getMinionsToSpawn() {
        if (phase == 2) return 1 + (int)(Math.random() * 2); // 1-2 minions (reduced)
        if (phase == 4) return 2 + (int)(Math.random() * 2); // 2-3 minions (reduced from 5-6)
        if (phase >= 6) return 3 + (int)(Math.random() * 2); // 3-4 minions (reduced from 4-6)
        return 0;
    }
    
    public boolean isShielded() {
        return shielded;
    }
    
    public boolean shouldSpawnShieldGenerators() {
        return shieldGeneratorsActive > 0;
    }
    
    public int getShieldGeneratorsToSpawn() {
        int count = shieldGeneratorsActive;
        shieldGeneratorsActive = 0; // Reset after spawning
        return count;
    }
    
    public void removeShield() {
        shielded = false;
        shieldGeneratorsActive = 0;
        if (phase == 3) {
            shieldPhaseCompleted = true; // Mark Phase 3 shield phase as completed
        }
    }
    
    public boolean shouldCreateLaserSweep() {
        return laserSweepTimer <= 0 && phase >= 5;
    }
    
    public void resetLaserSweepTimer() {
        if (phase == 5) {
            laserSweepTimer = 480; // 8 seconds
        } else if (phase == 6) {
            laserSweepTimer = 300; // 5 seconds
        } else if (phase == 7) {
            laserSweepTimer = 180; // 3 seconds
        }
    }
    
    public boolean shouldFireHomingMissiles() {
        return homingMissileTimer <= 0 && phase >= 5;
    }
    
    public void resetHomingMissileTimer() {
        if (phase == 5) {
            homingMissileTimer = 360; // 6 seconds
        } else if (phase == 6) {
            homingMissileTimer = 240; // 4 seconds
        } else if (phase == 7) {
            homingMissileTimer = 150; // 2.5 seconds
        }
    }
    
    public boolean shouldTeleport() {
        if (teleportReady && !isTeleporting && phase >= 5) {
            teleportReady = false;
            return true;
        }
        return false;
    }
    
    public boolean isTeleporting() {
        return isTeleporting;
    }
    
    public boolean shouldCreateEnvironmentalHazard() {
        return environmentalHazardTimer <= 0 && phase >= 6;
    }
    
    public void resetEnvironmentalHazardTimer() {
        if (phase == 6) {
            environmentalHazardTimer = 420; // 7 seconds
        } else if (phase == 7) {
            environmentalHazardTimer = 240; // 4 seconds
        }
    }
    
    public boolean isHealingPhase() {
        return healingPhase && phase == 7;
    }
    
    public String getPhaseTitle() {
        switch (phase) {
            case 1: return "AWAKENING";
            case 2: return "FIRST ASSAULT";
            case 3: return "SHIELD BARRIER";
            case 4: return "MINION ARMY";
            case 5: return "LASER FURY";
            case 6: return "CHAOS MODE";
            case 7: return "DEATH SPIRAL";
            default: return "UNKNOWN PHASE";
        }
    }
    
    public double getHealthPercentage() {
        return (double) health / maxHealth;
    }
}