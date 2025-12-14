package game.model;

import game.utils.Assets;

public abstract class GameObject {
    protected float x,y;
    protected final int OBJECT_SIZE = 0;
    protected final int FRAME_OFFSET = 0; 
    boolean isPerformingAction = false; // Default animations dont count
    protected String objectName = null;
    GameObject(float x, float y){
        this.x = x;
        this.y = y;
    }
    GameObject(){

    }
    
    public float getX(){return this.x;}
    public float getY(){return this.y;}
    public abstract void changeCurrentAnimation(String animation, boolean forcefulExit);
    public void update(float deltaTime, Assets mainAssets, String action) {
    }
    public String getObjectName(){
        return this.objectName;
    };
    public synchronized int getObjectSize(){
        return this.OBJECT_SIZE;
    }
    public synchronized int getFrameOffset(){
        return this.FRAME_OFFSET;
    }

}
