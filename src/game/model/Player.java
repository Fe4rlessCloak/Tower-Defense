package game.model;

import game.utils.Assets;

public class Player extends Entity{
    protected String defaultAnimation = Assets.ANIM_IDLE;
    
    
    
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
