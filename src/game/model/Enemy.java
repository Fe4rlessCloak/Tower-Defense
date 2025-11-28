package game.model;

import java.util.List;
import java.awt.Point;
import game.utils.Assets;

public abstract class Enemy extends Entity {
    protected final List<Point> path = List.of(
        new Point(100,350), // Fixed Paths; Will Change later
        new Point(245,650)
    ); 
    protected static int currentWaypointIndex = 0;
    protected Player finalTarget;
    protected int attackRange;
    protected boolean hasAttackedThisRound = false;
    private float attackCooldownTimer = 0.0f;
    private static final float ATTACK_RATE_DURATION = 1.0f; 

    public Enemy(float x, float y, int health, float speed, String name) {
        super(x, y, health, speed, name);
        this.finalTarget = null;
    }
    public void setTarget(Player finalTarget){
        this.finalTarget = finalTarget;
        
    }
    
    @Override
    public void update(float deltaTime, Assets mainAssets, String action) {
        super.update(deltaTime, mainAssets, action);
        if(finalTarget==null){
           this.y += this.speed * deltaTime;
        }else{
            if(currentWaypointIndex < path.size()){
                Point target = path.get(currentWaypointIndex);
                float stepDistance = this.speed * deltaTime;
                float dx = target.x - this.x;
                float dy = target.y - this.y;
                float dist = (float)Math.hypot(dx, dy);
                if (attackCooldownTimer > 0) {
                    this.changeCurrentAnimation(Assets.ANIM_IDLE);
                    attackCooldownTimer -= deltaTime;
                }

                if (dist<this.attackRange) {
                    if(attackCooldownTimer<=0){
                        this.currentAnimation = Assets.ANIM_ATTACK_FORWARD;
                        if(this.currentFrame==5){
                            finalTarget.takeDamage(50);
                            attackCooldownTimer = ATTACK_RATE_DURATION;
                        }
                    }
                    
                    if(!finalTarget.isAlive()){
                        this.setFinalTarget(null);
                        this.attackCooldownTimer = 0;
                        if(currentWaypointIndex< path.size()){
                            currentWaypointIndex++;
                        }
                    }
                    
                }else{
                    float vx = (dx / dist) * stepDistance; // Velcoity vectors
                    float vy = (dy / dist) * stepDistance;
                    this.x += vx;
                    this.y += vy;
                }
            }
            
        }
        
    }
    public Player getFinalTarget(){
        return this.finalTarget;
    }
    public void setFinalTarget(Player target){
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
    
}
