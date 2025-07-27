package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;
import javax.swing.ImageIcon;

public class MultiShot extends PowerUp {

    public MultiShot(int x, int y) {
        super(x, y);
        ImageIcon ii = new ImageIcon(IMG_POWERUP_MULTISHOT); // <-- add to Global.java
        var scaledImage = ii.getImage().getScaledInstance(ii.getIconWidth(),
                ii.getIconHeight(),
                java.awt.Image.SCALE_SMOOTH);
        setImage(scaledImage);
    }

    public void act() {
        this.y += 2;
    }

    public void upgrade(Player player) {
        System.out.println("Multishot power-up triggered!");
        System.out.println("Current shot type: " + player.getShotType());

        int current = player.getShotType();
        if (current < 4) {
            player.setShotType(current + 1);
        }
        this.die();
    }
}
