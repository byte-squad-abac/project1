package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class Heart extends PowerUp {

    public Heart(int x, int y) {
        super(x, y);
        // Set image (using speedup image for now - you can replace with heart image later)
        ImageIcon ii = new ImageIcon(IMG_POWERUP_HEART);
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth(),
                ii.getIconHeight(),
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act() {
        // Heart specific behavior - move down the screen
        this.y += 2; // Move down by 2 pixels each frame
    }

    public void upgrade(Player player) {
        // Restore one life to the player
        // Note: We'll need to access the Scene1 lives variable
        // For now, this will be handled in Scene1.java
        this.die(); // Remove the power-up after use
    }
}