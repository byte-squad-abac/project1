package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class SlowMinion extends Enemy {
    
    private int health = 2; // Dies in 2 hits
    private static final int SLOW_SPEED = 1;
    private int frameCount = 0;
    private int bombDropChance = 400; // More frequent bombs than fast minion
    
    public SlowMinion(int x, int y) {
        super(x, y);
        initMinion();
    }
    
    private void initMinion() {
        // Use alien2 sprite for slow minion
        var ii = new ImageIcon(IMG_ENEMY);
        
        // Excellent visibility size
        int targetWidth = 42;  // Increased for better visibility
        int targetHeight = 42;
        var scaledImage = ii.getImage().getScaledInstance(
                targetWidth, 
                targetHeight,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        
        // Enable animation
        setAnimated(true);
        animationSpeed = 6; // Slower animation
    }
    
    @Override
    public void act(int direction) {
        // Slower than FastMinion - move every 3rd frame
        if (animationFrame % 3 == 0) {
            this.y++; // Move down 1 pixel every 3 frames (slower than FastMinion)
        }
        updateAnimation(); // Update animation frame
        
        // Simple side-to-side with screen boundaries
        if (this.x < BORDER_LEFT) {
            this.x = BORDER_LEFT;
        } else if (this.x > BOARD_WIDTH - BORDER_RIGHT - 30) {
            this.x = BOARD_WIDTH - BORDER_RIGHT - 30;
        }
        
        // Remove if goes off bottom
        if (this.y > BOARD_HEIGHT) {
            setDying(true);
        }
        
        updateAnimation();
    }
    
    @Override
    public Bomb dropBomb() {
        // Medium bombs
        Bomb bomb = new Bomb(this.x + 15, this.y + 30, false);
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