package game.model;

import game.utils.Assets;

public abstract class Entity extends GameObject {
    protected int health;
    protected float speed;
    protected int OBJECT_SIZE = 96;
    protected int FRAME_OFFSET = OBJECT_SIZE / 4; 

    protected String currentAnimation = Assets.ANIM_RUN_DOWNWARD; 
    protected String defaultAnimation = Assets.ANIM_RUN_DOWNWARD;
    protected int currentFrame = 0;
    protected float animationTimer = 0f;
    protected static final float FRAME_SPEED = 0.1f; // Speed of animation

    protected String className;

    Entity(float x, float y){
        super(x, y);
        this.health = health;
        this.speed = speed;
    }
    Entity(){

    }
    public void takeDamage(int damage){
        this.health = this.health - damage;
        System.out.println("Current Health:" + this.health);
    }
    public boolean isAlive(){
        return health>0;
    }
    public int getObjectSize(){
        return this.OBJECT_SIZE;
    }
    public int getFrameOffset(){
        return this.FRAME_OFFSET;
    }

    public synchronized String getCurrentAnimation() {
        return currentAnimation;
    }

    public synchronized int getCurrentFrame() {
        return currentFrame;
    }

    public String getClassName(){
        return className;
    }
    public int getHealth(){
        return this.health;
    }
    @Override
    public void update(float deltaTime, Assets mainAssets, String action) {
        // 1. Move
        int framesPerAnimation = mainAssets.getFrameCount(getClassName(), this.currentAnimation);
        // 2. Animate
        animationTimer += deltaTime;
        if (animationTimer >= FRAME_SPEED) {
            this.currentFrame++;
            if (currentFrame >= framesPerAnimation) { 
                currentFrame = 0; 
                isPerformingAction = false;
                this.currentAnimation = defaultAnimation;
            }
            animationTimer = 0;
        }
        // if (!isPerformingAction && action != null && action.equals("Attack")) {
        //     currentFrame = 0;
        //     isPerformingAction = true;
        //     changeCurrentAnimation(Assets.ANIM_ATTACK_DOWNWARD); 
        //  }
    }
}
