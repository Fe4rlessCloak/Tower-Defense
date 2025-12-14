package game.model;

import game.utils.Assets;

public class Player extends Entity{
    protected String defaultAnimation = Assets.ANIM_IDLE;
    private int gold = 0;
    private int score =0;
    
    
    @Override
    public void changeCurrentAnimation(String animation) {
    
        throw new UnsupportedOperationException("Unimplemented method 'changeCurrentAnimation'");
    }
    // 
    public Player(float x, float y, int health, float speed, String objectName){
        super(x,y,health,speed,objectName);
        this.defaultAnimation = Assets.ANIM_IDLE;
    }

    public synchronized int getGold(){
        return gold;
    }

    public synchronized void addGold(int amount){
        this.gold += amount;
    }

    public synchronized boolean spendGold(int amount){
        if (amount <= 0){
            return false;
        }
        if (this.gold >= amount){
            this.gold -= amount;
            return true;
        }
        return false;
    }

    public synchronized int getScore(){
        return score;
    }

    public synchronized void addScore(int amount){
        this.score += amount;
    }

}
