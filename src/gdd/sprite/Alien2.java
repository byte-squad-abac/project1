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
    }

    public void act(int direction) {
        this.y ++;

        // Horizontal oscillation: 2-frame steps left/right
        frameCount++;
        if (frameCount % 2 == 0) {
            this.x += horizontalDirection;
        }
        if (frameCount % 70 == 0) {
            horizontalDirection = -horizontalDirection;
        }
    }

    public Bomb getBomb() {

        return bomb;
    }

 
}
