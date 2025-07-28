package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class TankMinion extends Enemy {
    
    private int health = 4; // Dies in 4 hits - very tanky
    private static final int TANK_SPEED = 1;
    private int frameCount = 0;
    private int bombDropChance = 200; // Very frequent bombs
    
    public TankMinion(int x, int y) {
        super(x, y);
        initMinion();
    }
    
    private void initMinion() {
        // Use UFO sprite for tank minion (looks more intimidating)
        var ii = new ImageIcon("src/images/ufo_game_enemy.png");
        
        // Tank size - largest minion for intimidation
        int targetWidth = 45;  // Largest for tank visibility and intimidation
        int targetHeight = 45;
        var scaledImage = ii.getImage().getScaledInstance(
                targetWidth, 
                targetHeight,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        
        // Enable animation
        setAnimated(true);
        animationSpeed = 8; // Very slow animation
    }
    
    @Override
    public void act(int direction) {
        // Slowest - move every 4th frame (tank is heavy)
        if (animationFrame % 4 == 0) {
            this.y++; // Move down 1 pixel every 4 frames (slowest)
        }
        updateAnimation(); // Update animation frame
        
        // Keep on screen
        if (this.x < BORDER_LEFT) {
            this.x = BORDER_LEFT;
        } else if (this.x > BOARD_WIDTH - BORDER_RIGHT - 45) {
            this.x = BOARD_WIDTH - BORDER_RIGHT - 45;
        }
        
        // Remove if goes off bottom
        if (this.y > BOARD_HEIGHT) {
            setDying(true);
        }
        
        updateAnimation();
    }
    
    @Override
    public Bomb dropBomb() {
        // Large bombs like boss
        Bomb bomb = new Bomb(this.x + 22, this.y + 35, true); // Boss-style bomb
        bomb.setDestroyed(false);
        return bomb;
    }
    
    public int getBombDropChance() {
        return bombDropChance;
    }
    
    public void takeDamage() {
        health--;
        if (health <= 0) {
            setDying(true);
        }
    }
    
    public int getHealth() {
        return health;
    }
}