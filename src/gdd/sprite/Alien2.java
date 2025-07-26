package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien2 extends Enemy {

    private Bomb bomb;
    private int frameCount = 0;
    private int horizontalDirection = 1;

    public Alien2(int x, int y) {
        super(x, y);
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY_1);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        
        // Enable animation for this sprite
        setAnimated(true);
    }

    public void act(int direction) {
        // Slower vertical movement - only move every other frame
        frameCount++;
        if (frameCount % 2 == 0) {
            this.y++;
        }

        // Horizontal oscillation: slower movement
        if (frameCount % 4 == 0) {
            this.x += horizontalDirection;
        }
        if (frameCount % 120 == 0) { // Slower direction change
            horizontalDirection = -horizontalDirection;
        }
        
        updateAnimation(); // Update animation frame
    }

    public Bomb getBomb() {

        return bomb;
    }

 
}
