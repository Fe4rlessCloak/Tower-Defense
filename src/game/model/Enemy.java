package game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Point;
import game.utils.Assets;

public abstract class Enemy extends Entity {
    protected List<Point> path;

    protected final int playerDetectionRadius;
    
    protected Player finalTarget;
    protected int attackRange;
    protected boolean hasAttackedThisRound = false;
    private float attackCooldownTimer = 0.0f;
    private boolean isTargetingPlayer = false;
    protected float ATTACK_RATE_DURATION = 1.25f; 
    protected String lastDirectionalAnimation = Assets.ANIM_RUN_DOWNWARD;

    public Enemy(float x, float y, int health, float speed, String name) {
        super(x, y, health, speed, name);
        this.finalTarget = null;
        this.playerDetectionRadius = 100;
        this.path = new ArrayList<>();
    }
    public void setTarget(Player finalTarget){
        this.finalTarget = finalTarget;
        
    }
    
    @Override
    public void update(float deltaTime, Assets mainAssets, String action) {
        super.update(deltaTime, mainAssets, action);
       
        if (this.finalTarget != null) {
            if(!this.finalTarget.isAlive()){
                this.resetTargetPlayer();
                this.attackCooldownTimer = 0.0f;
            }else{
                setFinalTarget(this.finalTarget);
            }
            
        }

        if (this.finalTarget == null || this.path.isEmpty()) {
            String idleAnimation = getIdleAnimation();
            this.changeCurrentAnimation(idleAnimation);
            this.attackCooldownTimer = 0.0f;
            return; 
        }
        

        Point target = path.get(0);
        float stepDistance = this.speed * deltaTime;
        float dx = target.x - this.x;
        float dy = target.y - this.y;
        float dist = (float)Math.hypot(dx, dy);
        if (attackCooldownTimer > 0) {
            String idleAnimation = getIdleAnimation();
            this.changeCurrentAnimation(idleAnimation);
            attackCooldownTimer -= deltaTime;
        }
        
        
        if (dist<this.attackRange && attackCooldownTimer <= 0) {
            if (!this.currentAnimation.startsWith("attack")) {
                this.resetCooldown();
                String attackAnim = determineAttackAnimation(dx, dy);
                changeCurrentAnimation(attackAnim);
                lastDirectionalAnimation = attackAnim.replace("attack", "run"); // keep run direction in sync
            }
            if (this.currentAnimation.startsWith("attack") && this.currentFrame == 8) {
                System.out.println("BARBARIAN HIT FRAME 8: Target: " + this.finalTarget.getObjectName() + 
                            " | Health BEFORE: " + this.finalTarget.getHealth());
                this.finalTarget.takeDamage(50);
                this.attackCooldownTimer = ATTACK_RATE_DURATION; // Start cooldown
                System.out.println("BARBARIAN HIT FRAME 8: Health AFTER: " + this.finalTarget.getHealth());
            }   
            
        }else if(dist >= this.attackRange){
            
            String newAnimation = determineRunAnimation(dx, dy);
            this.lastDirectionalAnimation = newAnimation;
            this.changeCurrentAnimation(newAnimation);
            float vx = (dx / dist) * stepDistance; // Velocity vectors
            float vy = (dy / dist) * stepDistance;
            this.x += vx;
            this.y += vy;
        }
    }
    public Player getFinalTarget(){
        return this.finalTarget;
    }
    public void setFinalTarget(Player target){
        
        if (target != null) {
            if(target instanceof Tower){
                this.isTargetingPlayer = false; 
            }else{
                this.isTargetingPlayer = true;
            }
            path.clear();
            this.path.add(new Point((int)target.getX(), (int)target.getY()));
            
        } 
        this.finalTarget = target;
        
    }
    public void changeCurrentAnimation(String newAnimation){   
        this.currentAnimation = newAnimation;
    }
    public int getAttackRange(){
        return this.attackRange;
    }
    public boolean isWithinAttackRange(){
        if (this.finalTarget == null || !this.finalTarget.isAlive()) {
            return false;
        }
        float dx = finalTarget.getX() - this.getX();
        float dy = finalTarget.getY() - this.getY();
        float dist = (float)Math.hypot(dx, dy); // Attack radius should be defined as a constant
        
        // 3. Check Range (Using a defined attack distance, e.g., 50 units)
        if (dist <= this.getAttackRange()) {
            return true;
        }
        
        return false;
    }
    public boolean hasValidTarget() {
        // Return TRUE if the target object exists AND is alive.
        if(this.finalTarget!=null){
            return this.finalTarget.isAlive();
        }
        return false;
    }

    public void resetTargetPlayer() {
        this.finalTarget = null;
        this.isTargetingPlayer = false;
    }
    public int getPlayerDetectionRadius() {
        return this.playerDetectionRadius;
    }
    public boolean isTargetingPlayer() {
        if(this.getFinalTarget() instanceof Tower){
            return false;
        }
        return true;
    }
    public void setIsTargetingPlayer(boolean targetingPlayer) {
        this.isTargetingPlayer = targetingPlayer;
    }
    public void resetCooldown(){
        this.attackCooldownTimer = 0f;
    }

    // Inside the Enemy.java class

    private String determineRunAnimation(float dx, float dy) {
        // Math.atan2 returns the angle in radians between the positive X-axis 
        // and the point (dx, dy), ranging from -PI to PI.
        double angleRad = Math.atan2(dy, dx);

        double angleDeg = Math.toDegrees(angleRad);
        if (angleDeg < 0) {
            angleDeg += 360; 
        }

        if (angleDeg >= 337.5 || angleDeg < 22.5) {
            return Assets.ANIM_RUN_LEFT; // Right (0/360)
        } else if (angleDeg >= 22.5 && angleDeg < 67.5) {
            return Assets.ANIM_RUN_DOWN_LEFT; // Down-Right (45)
        } else if (angleDeg >= 67.5 && angleDeg < 112.5) {
            return Assets.ANIM_RUN_DOWNWARD; // Down (90)
        } else if (angleDeg >= 112.5 && angleDeg < 157.5) {
            return Assets.ANIM_RUN_DOWN_RIGHT; // Down-Left (135)
        } else if (angleDeg >= 157.5 && angleDeg < 202.5) {
            return Assets.ANIM_RUN_RIGHT; // Left (180)
        } else if (angleDeg >= 202.5 && angleDeg < 247.5) {
            return Assets.ANIM_RUN_TOP_RIGHT; // Top-Left (225)
        } else if (angleDeg >= 247.5 && angleDeg < 292.5) {
            return Assets.ANIM_RUN_TOP; // Top (270)
        } else { // 292.5 to 337.5
            return Assets.ANIM_RUN_TOP_LEFT; // Top-Right (315)
        }
    }
    // Inside the Enemy.java class

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
        
        
        if (currentAnim.startsWith("run")) {
            return "idle" + currentAnim.substring(3); 
            
        } else if (currentAnim.startsWith("attack")){

            return "idle" + currentAnim.substring(6);
        }
        
        // Fallback if the stored string is somehow corrupted
        return Assets.ANIM_IDLE_DOWNWARD; 
    }
}
