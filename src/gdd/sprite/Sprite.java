package gdd.sprite;

import java.awt.Image;

abstract public class Sprite {

    protected boolean visible;
    protected Image image;
    protected boolean dying;
    protected int visibleFrames = 10;

    protected int x;
    protected int y;
    protected int dx;
    
    // Animation properties
    protected int animationFrame = 0;
    protected int animationSpeed = 8; // Change frame every 8 game frames
    protected boolean animated = false;

    public Sprite() {
        visible = true;
    }

    abstract public void act();

    public boolean collidesWith(Sprite other) {
        if (other == null || !this.isVisible() || !other.isVisible()) {
            return false;
        }
        return this.getX() < other.getX() + other.getImage().getWidth(null)
                && this.getX() + this.getImage().getWidth(null) > other.getX()
                && this.getY() < other.getY() + other.getImage().getHeight(null)
                && this.getY() + this.getImage().getHeight(null) > other.getY();
    }

    public void die() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void visibleCountDown() {
        if (visibleFrames > 0) {
            visibleFrames--;
        } else {
            visible = false;
        }
    }

    protected void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
    
    public int getDx() {
        return dx;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public boolean isDying() {
        return this.dying;
    }
    
    // Animation methods
    public void updateAnimation() {
        if (animated) {
            animationFrame++;
        }
    }
    
    public int getAnimationFrame() {
        return (animationFrame / animationSpeed) % 2; // Simple 2-frame animation
    }
    
    // Get clipping bounds for current animation frame
    // Uses proper clipping technique with single-frame sprites
    public int[] getFrameClip() {
        if (image == null) {
            return new int[]{0, 0, 0, 0};
        }
        
        if (!animated) {
            // No animation - use full image with clipping technique
            return new int[]{0, 0, image.getWidth(null), image.getHeight(null)};
        }
        
        // For animated single-frame sprites, create subtle animation effect
        // by clipping slightly different regions to create "breathing" effect
        int fullWidth = image.getWidth(null);
        int fullHeight = image.getHeight(null);
        int currentFrame = getAnimationFrame();
        
        if (currentFrame == 0) {
            // Frame 0: Full sprite
            return new int[]{0, 0, fullWidth, fullHeight};
        } else {
            // Frame 1: Slightly smaller clipping for "pulse" effect
            int shrink = 1;
            return new int[]{
                shrink,                    // source X (1 pixel in)
                shrink,                    // source Y (1 pixel in)
                fullWidth - (shrink * 2),  // width (2 pixels smaller)
                fullHeight - (shrink * 2)  // height (2 pixels smaller)
            };
        }
    }
    
    public void setAnimated(boolean animated) {
        this.animated = animated;
    }
    
    public boolean isAnimated() {
        return animated;
    }
}
