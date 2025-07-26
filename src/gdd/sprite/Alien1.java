package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Alien1 extends Enemy {

    private Bomb bomb;

    public Alien1(int x, int y) {
        super(x, y);
        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        var ii = new ImageIcon(IMG_ENEMY);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR,
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
        
        // Enable animation for this sprite
        setAnimated(true);
    }

    public void act(int direction) {
        // Slower movement - only move every other frame
        if (animationFrame % 2 == 0) {
            this.y++;
        }
        updateAnimation(); // Update animation frame
    }

    public Bomb getBomb() {

        return bomb;
    }

    
}
