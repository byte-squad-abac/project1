package gdd.sprite;

import javax.swing.ImageIcon;
import static gdd.Global.*;


public class Bomb extends Sprite {

        private boolean destroyed;
        private boolean isBossBomb = false; // Track if this is a boss bomb for bigger size

        public Bomb(int x, int y) {
            initBomb(x, y);
        }
        
        // Constructor for boss bombs (larger)
        public Bomb(int x, int y, boolean isBossBomb) {
            this.isBossBomb = isBossBomb;
            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

            setDestroyed(true);

            this.x = x;
            this.y = y;

            var bombImg = "src/images/bomb.png";
            var ii = new ImageIcon(bombImg);
            
            if (isBossBomb) {
                // Make boss bombs 2x larger for better visibility
                try {
                    var scaledImage = ii.getImage().getScaledInstance(
                        ii.getIconWidth() * 2, 
                        ii.getIconHeight() * 2,
                        java.awt.Image.SCALE_FAST);
                    setImage(scaledImage);
                } catch (Exception e) {
                    setImage(ii.getImage()); // Fallback to original
                }
            } else {
                setImage(ii.getImage());
            }
        }

        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {

            return destroyed;
        }
        @Override
        public void act() {
            // Bomb behavior is handled externally in Game loop
        }

    }