package game.model;

import game.utils.Assets; // This import is critical!


public class Barbarian extends Entity {

    // Helper vars for animation
    private String currentAnimation = Assets.ANIM_RUN_FORWARD; 
    private int currentFrame = 0;
    private float animationTimer = 0f;
    private static final float FRAME_SPEED = 0.1f; // Speed of animation
    public static String className = "Barbarian";
    private static int objectID = 1;
    
    public Barbarian(float x, float y, int health, float speed, String action) {
        super(x, y, health, speed, generateName());
    }
    private synchronized static final String generateName(){
        String candidateName = className + objectID;
        objectID++;
        return candidateName;
    }
    public Barbarian(){

    }
    @Override
    public void update(float deltaTime, Assets mainAssets, String action) {
        // 1. Move
    
        this.y += this.speed * deltaTime;
        int framesPerAnimation = mainAssets.getFrameCount("Barbarian", this.currentAnimation);
        // 2. Animate
        animationTimer += deltaTime;
        if (animationTimer >= FRAME_SPEED) {
            currentFrame++;
            if (currentFrame >= framesPerAnimation) { 
                currentFrame = 0; 
                isPerformingAction = false;
                changeCurrentAnimation(Assets.ANIM_RUN_FORWARD);
            }
            animationTimer = 0;
        }
        if (!isPerformingAction && action != null && action.equals("Attack")) {
            currentFrame = 0;
            isPerformingAction = true;
            changeCurrentAnimation(Assets.ANIM_ATTACK_FORWARD); 
         }

    }
    
    public synchronized String getCurrentAnimation() {
        return currentAnimation;
    }

    public synchronized int getCurrentFrame() {
        return currentFrame;
    }

    public void changeCurrentAnimation(String newAnimation){
        
        this.currentAnimation = newAnimation;
           
        
    }
    
    
}