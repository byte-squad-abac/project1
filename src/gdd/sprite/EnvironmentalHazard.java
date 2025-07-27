package gdd.sprite;

import static gdd.Global.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class EnvironmentalHazard extends Sprite {
    
    private int warningTimer = 90; // 1.5 seconds warning
    private boolean isWarning = true;
    private boolean isActive = false;
    private boolean isFinished = false;
    private int debrisSpeed = 4;
    private int debrisWidth;
    private int debrisHeight;
    private int hazardType; // 0 = falling debris, 1 = danger zone
    
    public EnvironmentalHazard(int hazardType, int x, int y) {
        super();
        this.hazardType = hazardType;
        this.x = x;
        this.y = y;
        
        if (hazardType == 0) { // Falling debris
            this.debrisWidth = 30 + (int)(Math.random() * 40); // 30-70 width
            this.debrisHeight = 20 + (int)(Math.random() * 30); // 20-50 height
            this.y = -debrisHeight; // Start above screen
        } else { // Danger zone
            this.debrisWidth = 80 + (int)(Math.random() * 60); // 80-140 width
            this.debrisHeight = 80 + (int)(Math.random() * 60); // 80-140 height
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
            if (hazardType == 0) { // Falling debris
                y += debrisSpeed;
                
                // Remove when goes off bottom
                if (y > BOARD_HEIGHT + 50) {
                    isActive = false;
                    isFinished = true;
                }
            } else { // Danger zone
                // Danger zones are stationary but time out
                warningTimer++; // Reuse timer for duration
                if (warningTimer > 180) { // Active for 3 seconds
                    isActive = false;
                    isFinished = true;
                }
            }
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        if (isWarning) {
            // Draw warning indication
            if (hazardType == 0) { // Falling debris warning
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5, 3}, 0));
                g2d.drawRect(x, 0, debrisWidth, BOARD_HEIGHT);
                
                // Warning text
                g2d.setColor(Color.RED);
                g2d.drawString("DEBRIS!", x + 5, 30);
            } else { // Danger zone warning
                g2d.setColor(new Color(255, 255, 0, 100)); // Transparent yellow
                g2d.fillOval(x, y, debrisWidth, debrisHeight);
                g2d.setColor(Color.YELLOW);
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8, 4}, 0));
                g2d.drawOval(x, y, debrisWidth, debrisHeight);
            }
        } else if (isActive) {
            if (hazardType == 0) { // Active falling debris
                g2d.setColor(Color.GRAY);
                g2d.fillRect(x, y, debrisWidth, debrisHeight);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(x, y, debrisWidth, debrisHeight);
                
                // Add some detail lines
                g2d.setColor(Color.BLACK);
                g2d.drawLine(x + 5, y + 5, x + debrisWidth - 5, y + debrisHeight - 5);
                g2d.drawLine(x + debrisWidth - 5, y + 5, x + 5, y + debrisHeight - 5);
            } else { // Active danger zone
                g2d.setColor(new Color(255, 0, 0, 150)); // Transparent red
                g2d.fillOval(x, y, debrisWidth, debrisHeight);
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(4));
                g2d.drawOval(x, y, debrisWidth, debrisHeight);
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
        
        return playerX < x + debrisWidth && 
               playerX + playerWidth > x && 
               playerY < y + debrisHeight && 
               playerY + playerHeight > y;
    }
    
    public int getHazardType() {
        return hazardType;
    }
}