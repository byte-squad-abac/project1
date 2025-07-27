package gdd.sprite;

import static gdd.Global.*;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite {

    private static final int START_X = 270;
    private static final int START_Y = 540;
    private int width;
    private int currentSpeed = 2;

    private int shotType = 1; // default: 1 = normal, 2 = double shot, 3 = triple shot


    private Rectangle bounds = new Rectangle(175,135,17,32);

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        var ii = new ImageIcon(IMG_PLAYER);

        // Scale the image to use the global scaling factor with safer scaling
        try {
            var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                    ii.getIconHeight() * SCALE_FACTOR,
                    java.awt.Image.SCALE_FAST); // Use SCALE_FAST instead of SCALE_SMOOTH to avoid ClassCastException
            setImage(scaledImage);
        } catch (Exception e) {
            System.err.println("Error scaling player image, using original: " + e.getMessage());
            setImage(ii.getImage()); // Fallback to original image
        }

        setX(START_X);
        setY(START_Y);
        
        // Enable animation for player
        setAnimated(true);
    }

    public int getShotType() {
        return shotType;
    }

    public void setShotType(int shotType) {
        this.shotType = shotType;
    }


    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public void act() {
        x += dx;

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - 2 * width) {
            x = BOARD_WIDTH - 2 * width;
        }
        
        updateAnimation(); // Update animation frame
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }
    
    public void reset() {
        // Reset player to initial state
        setX(START_X);
        setY(START_Y);
        setDying(false);
        setShotType(1);
        setSpeed(5);
        dx = 0; // Stop any movement
        visible = true; // Ensure player is visible
    }
    
    // Public method to make player visible (for boss fight respawning)
    public void makeVisible() {
        System.out.println("[DEBUG] makeVisible() called - setting visible=true");
        visible = true;
    }
    
    // Full reset including image restoration (for boss fight respawning)
    public void fullReset() {
        System.out.println("[DEBUG] fullReset() called - before: visible=" + visible + ", dying=" + isDying());
        
        // Reset position and state
        setX(START_X);
        setY(START_Y);
        setDying(false);
        setShotType(1);
        setSpeed(5);
        dx = 0;
        visible = true;
        
        System.out.println("[DEBUG] fullReset() - after state reset: visible=" + visible + ", position=(" + getX() + "," + getY() + ")");
        
        // Reset image back to normal player sprite
        var ii = new ImageIcon(IMG_PLAYER);
        try {
            var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                    ii.getIconHeight() * SCALE_FACTOR,
                    java.awt.Image.SCALE_FAST);
            setImage(scaledImage);
            System.out.println("[DEBUG] fullReset() - image reset successful");
        } catch (Exception e) {
            System.err.println("[ERROR] Error scaling player image during reset: " + e.getMessage());
            setImage(ii.getImage()); // Fallback to original
            System.out.println("[DEBUG] fullReset() - using fallback image");
        }
        
        System.out.println("[DEBUG] fullReset() completed - visible=" + visible + ", image=" + (getImage() != null ? "EXISTS" : "NULL"));
    }
}
