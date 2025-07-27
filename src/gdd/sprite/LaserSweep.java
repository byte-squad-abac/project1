package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class LaserSweep extends Sprite {
    
    private int warningTimer = 120; // 2 seconds warning
    private int activeTimer = 180; // 3 seconds active
    private boolean isWarning = true;
    private boolean isActive = false;
    private boolean isFinished = false;
    private int sweepDirection; // 0 = horizontal, 1 = vertical
    private int sweepPosition;
    private int sweepSpeed = 3;
    
    public LaserSweep(int sweepDir) {
        super();
        this.sweepDirection = sweepDir;
        
        if (sweepDirection == 0) { // Horizontal sweep
            this.x = 0;
            this.y = 50; // Start from top
            this.sweepPosition = 50;
        } else { // Vertical sweep
            this.x = 50; // Start from left
            this.y = 0;
            this.sweepPosition = 50;
        }
    }
    
    public void act() {
        if (isWarning) {
            warningTimer--;
            if (warningTimer <= 0) {
                isWarning = false;
                isActive = true;
            }
        } else if (isActive) {
            activeTimer--;
            
            // Move the laser sweep
            if (sweepDirection == 0) { // Horizontal sweep
                sweepPosition += sweepSpeed;
                this.y = sweepPosition;
                if (sweepPosition > BOARD_HEIGHT - 100) {
                    isActive = false;
                    isFinished = true;
                }
            } else { // Vertical sweep
                sweepPosition += sweepSpeed;
                this.x = sweepPosition;
                if (sweepPosition > BOARD_WIDTH - 100) {
                    isActive = false;
                    isFinished = true;
                }
            }
            
            if (activeTimer <= 0) {
                isActive = false;
                isFinished = true;
            }
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        if (isWarning) {
            // Draw warning line
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{10, 5}, 0));
            
            if (sweepDirection == 0) { // Horizontal warning
                g2d.drawLine(0, sweepPosition, BOARD_WIDTH, sweepPosition);
                g2d.drawLine(0, sweepPosition + 20, BOARD_WIDTH, sweepPosition + 20);
            } else { // Vertical warning
                g2d.drawLine(sweepPosition, 0, sweepPosition, BOARD_HEIGHT);
                g2d.drawLine(sweepPosition + 20, 0, sweepPosition + 20, BOARD_HEIGHT);
            }
        } else if (isActive) {
            // Draw active laser
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(20, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            if (sweepDirection == 0) { // Horizontal laser
                g2d.drawLine(0, sweepPosition, BOARD_WIDTH, sweepPosition);
                
                // Add glow effect
                g2d.setColor(new Color(255, 100, 100, 100));
                g2d.setStroke(new BasicStroke(30, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(0, sweepPosition, BOARD_WIDTH, sweepPosition);
            } else { // Vertical laser
                g2d.drawLine(sweepPosition, 0, sweepPosition, BOARD_HEIGHT);
                
                // Add glow effect
                g2d.setColor(new Color(255, 100, 100, 100));
                g2d.setStroke(new BasicStroke(30, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(sweepPosition, 0, sweepPosition, BOARD_HEIGHT);
            }
        }
    }
    
    public boolean isWarning() {
        return isWarning;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public boolean isFinished() {
        return isFinished;
    }
    
    public boolean checkCollision(int playerX, int playerY, int playerWidth, int playerHeight) {
        if (!isActive) return false;
        
        if (sweepDirection == 0) { // Horizontal laser
            return playerY < sweepPosition + 25 && playerY + playerHeight > sweepPosition - 5;
        } else { // Vertical laser
            return playerX < sweepPosition + 25 && playerX + playerWidth > sweepPosition - 5;
        }
    }
    
    public int getSweepDirection() {
        return sweepDirection;
    }
    
    public int getSweepPosition() {
        return sweepPosition;
    }
}