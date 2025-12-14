package game.model;

import game.utils.Assets;

public class Tower extends Player{
    private static int objectID = 1;
    
    private static final String TOWER_CLASS_NAME = "Tower";

    {
        speed = 0F;
        health = 500;
        maxHealth = health;
        objectName = generateName();
        attackDamage = 25;
        attackFrame = 4;
    }
    

    public Tower(float x, float y ){
        super(x, y);
        this.className = "Tower";
        this.currentAnimation = Assets.ANIM_IDLE_TOP;
        this.OBJECT_SIZE = 192;
        this.attackRange = 300;

    }
    private synchronized static final String generateName(){
        String candidateName = TOWER_CLASS_NAME + objectID;
        objectID++;
        return candidateName;
    }
    @Override
    public void update(float deltaTime, Assets mainAssets, String action) {
        super.update(deltaTime, mainAssets, action);
        
    }
}
