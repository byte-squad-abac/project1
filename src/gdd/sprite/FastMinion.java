package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class FastMinion extends Enemy {
    
    private int health = 1; // Dies in 1 hit
    private static final int FAST_SPEED = 4;
    private int frameCount = 0;
    private int bombDropChance = 800; // Less frequent bombs
    
    public FastMinion(int x, int y) {
        super(x, y);
        initMinion();
    }
    
    private void initMinion() {
        // Use alien1 sprite but smaller for fast minion
        var ii = new ImageIcon(IMG_ENEMY_1);
        
        // Larger size for excellent visibility
        int targetWidth = 40;  // Increased for better visibility
        int targetHeight = 40;
        var scaledImage = ii.getImage().getScaledInstance(
                targetWidth, 
                targetHeight,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        
        // Enable animation
        setAnimated(true);
        animationSpeed = 2; // Fast animation
    }
    
    @Override
    public void act(int direction) {
        // Exactly like Alien1 in Wave 1 - slower movement, only move every other frame
        if (animationFrame % 2 == 0) {
            this.y++; // Move down 1 pixel every 2 frames (like Alien1)
        }
        updateAnimation(); // Update animation frame (like Alien1)
        
        // Keep on screen horizontally
        if (this.x < BORDER_LEFT) {
            this.x = BORDER_LEFT;
        } else if (this.x > BOARD_WIDTH - BORDER_RIGHT - 20) {
            this.x = BOARD_WIDTH - BORDER_RIGHT - 20;
        }
        
        // Remove if goes off bottom
        if (this.y > BOARD_HEIGHT) {
            setDying(true);
        }
        
        updateAnimation();
    }
    
    @Override
    public Bomb dropBomb() {
        // Small, fast bombs
        Bomb bomb = new Bomb(this.x + 10, this.y + 20, false); // Not boss bomb
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