package game.model;

import game.utils.Assets;

public class Tower extends Player{
    private static int objectID = 1;
    
    private static final String TOWER_CLASS_NAME = "Tower";

    

    public Tower(float x, float y, int health, float speed ){
        super(x, y, health, 0F, generateName());
        this.className = "Tower";
        this.currentAnimation = Assets.ANIM_IDLE;
    }
    private synchronized static final String generateName(){
        String candidateName = TOWER_CLASS_NAME + objectID;
        objectID++;
        return candidateName;
    }
    @Override
    public void update(float deltaTime, Assets mainAssets, String action) {
        super.update(deltaTime, mainAssets, action);
        int framesPerAnimation = mainAssets.getFrameCount(getClassName(), this.currentAnimation);
        // 2. Animate
        animationTimer += deltaTime;
        if (animationTimer >= FRAME_SPEED) {
            currentFrame++;
            if (currentFrame >= framesPerAnimation) { 
                currentFrame = 0; 
            }
            animationTimer = 0;
        }
    }
}
