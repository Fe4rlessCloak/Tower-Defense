package game.model;

import game.utils.Assets; // This import is critical!

public class Barbarian extends Entity {

    // Helper vars for animation
    private String currentAnimation = Assets.ANIM_RUN_FORWARD; 
    private int currentFrame = 0;
    private float animationTimer = 0f;
    private static final float FRAME_SPEED = 0.1f; // Speed of animation

    public Barbarian(float x, float y, int health, float speed) {
        super(x, y, health, speed);
    }

    @Override
    public void update(float deltaTime) {
        // 1. Move
        this.x += this.speed * deltaTime;

        // 2. Animate
        animationTimer += deltaTime;
        if (animationTimer >= FRAME_SPEED) {
            currentFrame++;
            // We don't know exact max frames here easily, so we rely on visual loop or Assets check
            // For now, reset every 6 frames (or whatever your max is)
            if (currentFrame >= 6) { 
                currentFrame = 0; 
            }
            animationTimer = 0;
        }
    }

    public String getCurrentAnimation() {
        return currentAnimation;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}