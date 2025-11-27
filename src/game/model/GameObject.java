package game.model;

import game.utils.Assets;

public abstract class GameObject {
    protected float x,y;


    GameObject(float x, float y){
        this.x = x;
        this.y = y;
    }
    public abstract void update(float deltaTime, Assets mainAssets);
    public float getX(){return this.x;}
    public float getY(){return this.y;}
    


}
