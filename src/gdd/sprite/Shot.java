package gdd.sprite;

import static gdd.Global.*;
import javax.swing.ImageIcon;

public class Shot extends Sprite {

    private static final int H_SPACE = 20;
    private static final int V_SPACE = 1;
    private int dx = 0; // horizontal speed
    private int dy = -20; // vertical speed (upward)


    public Shot(int x, int y) {
        this(x, y, 0, -20); // default straight shot
    }

    public Shot(int x, int y, int dx, int dy) {
        initShot(x, y);
        this.dx = dx;
        this.dy = dy;
    }

    public void act() {
    setX(getX() + dx);
    setY(getY() + dy);

    // Remove shot if it goes out of bounds
    if (getY() < 0 || getX() < 0 || getX() > BOARD_WIDTH) {
        die();
    }
}


    private void initShot(int x, int y) {

        var ii = new ImageIcon(IMG_SHOT);

        // Scale the image to use the global scaling factor
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth() * SCALE_FACTOR,
                ii.getIconHeight() * SCALE_FACTOR, 
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);

        setX(x + H_SPACE);
        setY(y - V_SPACE);
    }
}
