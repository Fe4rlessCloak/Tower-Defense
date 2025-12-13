package game.model;

import java.awt.Point;
import java.util.List;

import game.utils.Assets;

public abstract class Player extends Entity{
    protected String defaultAnimation = Assets.ANIM_IDLE;
    protected List<Point> path;
    
    protected int playerDetectionRadius;
    
    protected Player finalTarget;
    protected int attackRange;
    protected boolean hasAttackedThisRound = false;
    private float attackCooldownTimer = 0.0f;
    private boolean isTargetingPlayer = false;
    protected float ATTACK_RATE_DURATION = 1.25f; 
    protected String lastDirectionalAnimation = Assets.ANIM_RUN_DOWNWARD;

    @Override
    public void changeCurrentAnimation(String animation) {
    
        throw new UnsupportedOperationException("Unimplemented method 'changeCurrentAnimation'");
    }
    // 
    public Player(float x, float y, int health, float speed, String objectName){
        super(x,y,health,speed,objectName);
        this.defaultAnimation = Assets.ANIM_IDLE;
        
    }

}
