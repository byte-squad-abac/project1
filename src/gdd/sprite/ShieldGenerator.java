package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Color;

public class ShieldGenerator extends Sprite {
    
    private int health = 3; // Takes 3 hits to destroy
    private boolean destroyed = false;
    private int frameCount = 0;
    private boolean flashing = false;
    private int flashTimer = 0;
    
    public ShieldGenerator(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        initGenerator();
    }
    
    private void initGenerator() {
        // Use powerup sprite for now (can be replaced with shield generator image)
        var ii = new ImageIcon(IMG_POWERUP_MULTISHOT);
        
        // Medium size for visibility
        int targetWidth = 40;
        int targetHeight = 40;
        var scaledImage = ii.getImage().getScaledInstance(
                targetWidth, 
                targetHeight,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }
    
    public void act() {
        frameCount++;
        
        // Pulsing animation effect
        if (frameCount % 30 == 0) {
            flashing = !flashing;
        }
        
        // Handle damage flash
        if (flashTimer > 0) {
            flashTimer--;
        }
    }
    
    public void takeDamage() {
        if (!destroyed) {
            health--;
            flashTimer = 10; // Flash for 10 frames when hit
            
            if (health <= 0) {
                destroyed = true;
                setDying(true);
            }
        }
    }
    
    public boolean isDestroyed() {
        return destroyed;
    }
    
    public int getHealth() {
        return health;
    }
    
    public boolean shouldFlash() {
        return flashTimer > 0 || flashing;
    }
    
    // Override draw method to add visual effects
    public void draw(Graphics g, javax.swing.JComponent component) {
        if (isVisible() && !destroyed) {
            if (shouldFlash()) {
                // Draw with flash effect
                g.setColor(Color.CYAN);
                g.fillOval(getX() - 5, getY() - 5, 50, 50);
            }
            
            // Draw the main sprite
            g.drawImage(getImage(), getX(), getY(), component);
            
            // Draw health indicator
            g.setColor(Color.GREEN);
            int barWidth = 30;
            int barHeight = 4;
            int healthWidth = (barWidth * health) / 3; // 3 max health
            
            g.fillRect(getX() + 5, getY() - 8, healthWidth, barHeight);
            g.setColor(Color.RED);
            g.fillRect(getX() + 5 + healthWidth, getY() - 8, barWidth - healthWidth, barHeight);
        }
    }
}