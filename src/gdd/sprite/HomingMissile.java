package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Color;

public class HomingMissile extends Sprite {
    
    private double targetX;
    private double targetY;
    private double speed = 2.5;
    private int frameCount = 0;
    private boolean exploded = false;
    private int explosionTimer = 0;
    private boolean trackingPlayer = true;
    
    public HomingMissile(int startX, int startY) {
        super();
        this.x = startX;
        this.y = startY;
        this.targetX = startX;
        this.targetY = startY;
        initMissile();
    }
    
    private void initMissile() {
        // Use bomb sprite for missile (can be replaced with missile image)
        var ii = new ImageIcon(IMG_BOMB);
        
        // Small size for missile
        int targetWidth = 15;
        int targetHeight = 25;
        var scaledImage = ii.getImage().getScaledInstance(
                targetWidth, 
                targetHeight,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }
    
    public void act() {
        frameCount++;
        
        if (exploded) {
            explosionTimer++;
            if (explosionTimer > 30) { // Explosion lasts 30 frames
                setDying(true);
            }
            return;
        }
        
        // Update target position if tracking player
        if (trackingPlayer) {
            // This will be updated by the boss fight scene with player position
        }
        
        // Calculate direction to target
        double deltaX = targetX - x;
        double deltaY = targetY - y;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        
        if (distance > 5) { // If not at target yet
            // Normalize direction and apply speed
            double directionX = deltaX / distance;
            double directionY = deltaY / distance;
            
            x += directionX * speed;
            y += directionY * speed;
        } else {
            // Reached target - explode
            explode();
        }
        
        // Remove if goes off screen
        if (x < 0 || x > BOARD_WIDTH || y < 0 || y > BOARD_HEIGHT) {
            setDying(true);
        }
        
        // Increase speed over time
        if (frameCount % 60 == 0) {
            speed += 0.5; // Get faster every second
        }
    }
    
    public void updateTarget(double newX, double newY) {
        this.targetX = newX;
        this.targetY = newY;
    }
    
    public void explode() {
        exploded = true;
        explosionTimer = 0;
    }
    
    public boolean isExploded() {
        return exploded;
    }
    
    public boolean checkCollision(int playerX, int playerY, int playerWidth, int playerHeight) {
        if (exploded) {
            // Explosion has larger collision area
            int explosionRadius = 30;
            int centerX = x + 7; // Center of missile
            int centerY = y + 12;
            int playerCenterX = playerX + playerWidth / 2;
            int playerCenterY = playerY + playerHeight / 2;
            
            double distance = Math.sqrt(
                Math.pow(centerX - playerCenterX, 2) + 
                Math.pow(centerY - playerCenterY, 2)
            );
            
            return distance < explosionRadius;
        } else {
            // Direct missile collision
            return x < playerX + playerWidth && 
                   x + 15 > playerX && 
                   y < playerY + playerHeight && 
                   y + 25 > playerY;
        }
    }
    
    public void draw(Graphics g, javax.swing.JComponent component) {
        if (isVisible()) {
            if (exploded) {
                // Draw explosion effect
                g.setColor(Color.ORANGE);
                int explosionSize = explosionTimer * 2;
                g.fillOval(x - explosionSize/2, y - explosionSize/2, explosionSize, explosionSize);
                
                g.setColor(Color.RED);
                g.fillOval(x - explosionSize/4, y - explosionSize/4, explosionSize/2, explosionSize/2);
            } else {
                // Draw missile
                g.drawImage(getImage(), x, y, component);
                
                // Draw tracking trail
                g.setColor(Color.YELLOW);
                g.fillOval(x + 5, y + 20, 5, 5);
            }
        }
    }
    
    public double getTargetX() {
        return targetX;
    }
    
    public double getTargetY() {
        return targetY;
    }
}