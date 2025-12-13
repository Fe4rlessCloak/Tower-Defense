package game.model;

import game.utils.Assets;

public abstract class Entity extends GameObject {
    protected int health;
    protected float speed;
    protected int OBJECT_SIZE = 96;
    protected int FRAME_OFFSET = OBJECT_SIZE / 4; 
    protected boolean isPerformingAnimation = false;
    protected String currentAnimation = Assets.ANIM_RUN_DOWNWARD; 
    protected String defaultAnimation = Assets.ANIM_RUN_DOWNWARD;
    protected int currentFrame = 0;
    protected float animationTimer = 0f;
    protected static final float FRAME_SPEED = 0.1f; // Speed of animation
    protected boolean deathAnimationCompleted = false;   
    protected String className;
    public float DEATH_FADE_DURATON = 0.25f;
    protected float DEATH_TIMER = DEATH_FADE_DURATON;
    
    protected float damageFlashTimer = 0.0f;
    private static final float FLASH_DURATION = 0.15f;

    protected boolean aliveStatus = true;

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
        this.damageFlashTimer = FLASH_DURATION;
    }
    
    public boolean isAlive(){
        return health>0;
    }
    public boolean removalCandidate(){
        if(DEATH_TIMER<=0){
            return true;
        }
        return false;
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
        if(this.damageFlashTimer > 0) {
            this.damageFlashTimer -= deltaTime;
        }
        if (this.damageFlashTimer <= 0) {
            this.damageFlashTimer = 0.0f;
        }
       if (!isAlive() && this.damageFlashTimer == 0.0f) {
            // This handles the transition and the fade countdown
            this.DEATH_TIMER -= deltaTime;
            this.isPerformingAnimation = false; // Freeze the frame
            
            if (this.DEATH_TIMER <= 0) {
                this.aliveStatus = false; // Mark for permanent removal
            }
            return; // CRITICAL: Stop all further movement/animation/attack
        }
        
        if(isPerformingAnimation){
            int framesPerAnimation = mainAssets.getFrameCount(getClassName(), this.currentAnimation);
            // 2. Animate
            animationTimer += deltaTime;
            if (animationTimer >= FRAME_SPEED) {
                this.currentFrame++;
                if (currentFrame >= framesPerAnimation) { 
                    isPerformingAnimation = false;
                    currentFrame = 0; 
                    isPerformingAction = false;
                }
                animationTimer = 0;
            }
        }
        
       
        // if (!isPerformingAction && action != null && action.equals("Attack")) {
        //     currentFrame = 0;
        //     isPerformingAction = true;
        //     changeCurrentAnimation(Assets.ANIM_ATTACK_DOWNWARD); 
        //  }
    }

    public float getDamageFlashTimer(){
        return this.damageFlashTimer;
    }
    public float getDeathTimer(){
        return this.DEATH_TIMER;
    }   
    public float getDeathFadeDuration(){
        return this.DEATH_FADE_DURATON;
    }


    public boolean isFading() {
       return !isAlive() && this.DEATH_TIMER > 0 && this.DEATH_TIMER < this.DEATH_FADE_DURATON;
    }
}
