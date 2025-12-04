package game.model;

import game.utils.Assets; // This import is critical!


public class DarkKnight extends Enemy {

    // Helper vars for animation
    private static final String DARKKNIGHT_CLASS_NAME = "DarkKnight"; // Dont bother with this. Its only to fix java's inherent problemss
    private static int objectID = 1;
    
    public DarkKnight(float x, float y, int health, float speed, String action) {
        super(x, y, health, speed, generateName());
        this.className = "DarkKnight";
        this.attackRange = 65;
        ATTACK_RATE_DURATION = 2f;
    }
    private synchronized static final String generateName(){
        String candidateName = DARKKNIGHT_CLASS_NAME + objectID;
        objectID++;
        return candidateName;
    }
    @Override
    public void update(float deltaTime, Assets mainAssets, String action) {
        super.update(deltaTime, mainAssets, action);

    }
    
    // @Override
    // public void setFinalTarget(Player target) {
    //     if(target==null){
    //         this.finalTarget = target;
    //         return;
    //     }
    //     if(!(target instanceof Player) ){
    //         System.err.println("Invalid target assigned to " + this.getObjectName());
    //     }else{
    //         this.finalTarget = target;
    //     }
    // }
    
    
}