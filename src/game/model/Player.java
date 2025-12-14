package game.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import game.utils.Assets;

public abstract class Player extends Entity{
    protected String defaultAnimation = Assets.ANIM_IDLE_TOP;
    protected List<Point> path;
    
    protected Enemy finalTarget;
    protected int attackRange;
    protected boolean hasAttackedThisRound = false;
    private float attackCooldownTimer = 0.0f;
    private boolean isTargetingEnemy = false;
    protected float ATTACK_RATE_DURATION = 1.25f; 
    protected String lastDirectionalAnimation = Assets.ANIM_IDLE_TOP;   
    protected int attackDamage;

    protected int attackFrame; // Important for syncing animations across different characters
    
    // 
    public Player(float x, float y){
        super(x,y);
        this.defaultAnimation = Assets.ANIM_IDLE_TOP;
        this.finalTarget = null;
        
        this.path = new ArrayList<>();
        
    }
     public boolean hasValidTarget() {
        // Return TRUE if the target object exists AND is alive.
        if(this.finalTarget!=null){
            return this.finalTarget.isAlive();
        }
        return false;
    }
    @Override
    public void update(float deltaTime, Assets mainAssets, String action) {
        // TODO Auto-generated method stub
        super.update(deltaTime, mainAssets, action);
        
        if (this.finalTarget != null) { // Target changes target to NULL if the current target is dead
            if(!this.finalTarget.isAlive()){
                this.resetTargetEnemy();
                this.attackCooldownTimer = 0.0f;
            }else{
                this.setFinalTarget(finalTarget); // Important: This updates the path every tick
            }
        }

        if (this.finalTarget == null || this.path.isEmpty()) { // If there is no target, the player will idle
            String idleAnimation = getIdleAnimation();
            this.changeCurrentAnimation(idleAnimation, false);
            this.attackCooldownTimer = 0.0f;
            return; 
        }

        if (attackCooldownTimer > 0) { 
            String idleAnimation = getIdleAnimation();
            this.changeCurrentAnimation(idleAnimation, false);
            attackCooldownTimer -= deltaTime;
        }

        Point target = path.get(0);
        float dx = target.x - this.x;
        float dy = target.y - this.y;
        float dist = (float)Math.hypot(dx, dy);
        
        if (dist<this.attackRange && attackCooldownTimer <= 0) { // If player is within range and there's no cooldown
            if (!this.currentAnimation.startsWith("attack")) { // If the player has not attacked yet
                this.resetCooldown();
                String attackAnim = determineAttackAnimation(dx, dy);
                changeCurrentAnimation(attackAnim, true);
                lastDirectionalAnimation = attackAnim;
            }
            if (this.currentAnimation.startsWith("attack") && this.currentFrame == this.attackFrame) { // If the enemy is attacking, and hits frame 8
                System.out.println("TOWER HIT FRAME 4: Target: " + this.finalTarget.getObjectName() + 
                            " | Health BEFORE: " + this.finalTarget.getHealth());
                this.finalTarget.takeDamage(this.attackDamage);
                this.attackCooldownTimer = ATTACK_RATE_DURATION; // Start cooldown
                System.out.println("TOWER HIT FRAME 4: Health AFTER: " + this.finalTarget.getHealth());
            }   
            
        }else if(dist >= this.attackRange){ // The enemy is not within range
            this.resetCooldown();
            this.changeCurrentAnimation(this.defaultAnimation, false);
        }




    }

    public void resetCooldown(){
        this.attackCooldownTimer = 0f;
    }

    public void setFinalTarget(Enemy target){
        
        if (target != null) {
            path.clear();
            this.path.add(new Point((int)target.getX(), (int)target.getY()));
            
        } 
        this.finalTarget = target;
        
    }
    public void resetTargetEnemy() {
        this.finalTarget = null;
    }


    private String determineAttackAnimation(float dx, float dy) {
        // Math.atan2 returns the angle in radians
        double angleRad = Math.atan2(dy, dx);
        
        // Convert angle to degrees (0 to 360) and normalize it.
        double angleDeg = Math.toDegrees(angleRad);
        if (angleDeg < 0) {
            angleDeg += 360; 
        }
        // --- Mapping Angles to 8 Directions (Same as run, but returns ATTACK IDs) ---
        
        if (angleDeg >= 337.5 || angleDeg < 22.5) {
            return Assets.ANIM_ATTACK_LEFT; 
        } else if (angleDeg >= 22.5 && angleDeg < 67.5) {
            return Assets.ANIM_ATTACK_DOWN_LEFT; 
        } else if (angleDeg >= 67.5 && angleDeg < 112.5) {
            return Assets.ANIM_ATTACK_DOWNWARD; 
        } else if (angleDeg >= 112.5 && angleDeg < 157.5) {
            return Assets.ANIM_ATTACK_DOWN_RIGHT; 
        } else if (angleDeg >= 157.5 && angleDeg < 202.5) {
            return Assets.ANIM_ATTACK_RIGHT; 
        } else if (angleDeg >= 202.5 && angleDeg < 247.5) {
            return Assets.ANIM_ATTACK_TOP_RIGHT; // Assuming UP is TOP
        } else if (angleDeg >= 247.5 && angleDeg < 292.5) {
            return Assets.ANIM_ATTACK_TOP;       // Assuming UP is TOP
        } else { // 292.5 to 337.5
            return Assets.ANIM_ATTACK_TOP_LEFT; // Assuming UP is TOP
        }
    }
    private String getIdleAnimation() { // No longer needs a parameter
        String currentAnim = this.lastDirectionalAnimation; // Use the stored variable
        if (currentAnim.startsWith("attack")){
            return "idle" + currentAnim.substring(6);
        }
        
        // Fallback if the stored string is somehow corrupted
        return Assets.ANIM_IDLE_TOP; 
    }
     @Override
    public void changeCurrentAnimation(String newAnimation, boolean forcefulExit){  
        if(forcefulExit){
            this.isPerformingAnimation = true;
            this.currentAnimation = newAnimation;
            this.currentFrame = 0;
            return;
        }
        if(this.isPerformingAnimation){
            return;
        } 
        if(this.currentAnimation.equals(newAnimation)){
            this.isPerformingAnimation = true;
            return;
        }
        this.isPerformingAnimation = true;
        this.currentAnimation = newAnimation;
        this.currentFrame = 0;
    }
}
