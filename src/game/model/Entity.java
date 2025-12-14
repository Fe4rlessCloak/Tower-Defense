package game.model;

import java.util.Random;

import game.utils.Assets;

public abstract class Entity extends GameObject {
    private final java.util.List<FloatingText> activeTexts = new java.util.concurrent.CopyOnWriteArrayList<>();
    private static final Random RNG = new Random();
    private static final float CRIT_CHANCE = 0.20f; // Can be overridden
    protected int damagePerHit; // Override
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



    protected int maxHealth;
    protected boolean aliveStatus = true;
    protected
    
    Entity(float x, float y){
        super(x, y);
        this.health = health;
        this.speed = speed;
    }
    Entity(){
        
    }
    
    public void takeDamage(int damage, boolean hasTakenCritical){
        this.health = this.health - damage;
        System.out.println("Current Health:" + this.health);
        this.damageFlashTimer = FLASH_DURATION;
        synchronized (this.activeTexts) {
            this.activeTexts.add(new FloatingText(damage, hasTakenCritical));
        }
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
    public java.util.List<FloatingText> getActiveTexts() {
        return this.activeTexts;
    }
    @Override
    public void update(float deltaTime, Assets mainAssets, String action) {
        if(this.damageFlashTimer > 0) {
            this.damageFlashTimer -= deltaTime;
        }
        if (this.damageFlashTimer <= 0) {
            this.damageFlashTimer = 0.0f;
        }
         for (int i = this.activeTexts.size() - 1; i >= 0; i--) {
            // Get the element at the current index 'i'
            FloatingText text = this.activeTexts.get(i);
            
            // 1. Update the position and timer
            text.update(deltaTime);
            
            // 2. Check for expiration
            if (text.isExpired()) {
                // Remove the text using its index.
                // We iterate backward to ensure that removing an item does not
                // skip the next item in the list.
                this.activeTexts.remove(i); 
            }
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
    public int getMaxHealth() {
        return this.maxHealth;
    }

    
    public boolean isCritical(){
        float roll = RNG.nextFloat(); 
        
        
        if (roll <= CRIT_CHANCE) {
            
            return true;
        } else {
            
            return false;
        }
    }
    public int getDamagePerHit(){
        return this.damagePerHit;
    }
   
    public static class FloatingText {
        public final int damageValue;
        public float lifeTimer;
        public float currentYOffset; // How far up from the starting point it has moved
        public final boolean isCritical;
        
        private static final float LIFESPAN = 1.2f;
        private static final float VERTICAL_SPEED = 80.0f; 

        public FloatingText(int damage, boolean isCrit) {
            this.damageValue = damage;
            this.isCritical = isCrit;
            this.lifeTimer = LIFESPAN;
            this.currentYOffset = 0.0f;
        }
        
        public void update(float deltaTime) {
            this.lifeTimer -= deltaTime;
            // Move the text upward over time
            this.currentYOffset += VERTICAL_SPEED * deltaTime;
        }
        
        public float getOpacity() {
            float fadeDuration = 0.2f; 
            if (lifeTimer <= 0.0f) { // <-- FIX 1: If the text is dead, opacity must be zero
                return 0.0f;
            }
            if (lifeTimer < fadeDuration) {
                return lifeTimer / fadeDuration;
            }
            return 1.0f;
        }
        
        public boolean isExpired() {
            return lifeTimer <= 0;
        }
    }
}
