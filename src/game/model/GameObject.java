package game.model;

public abstract class GameObject {
    protected float x,y;


    GameObject(float x, float y){
        this.x = x;
        this.y = y;
    }
    public abstract void update(float deltaTime);
    public float getX(){return this.x;}
    public float getY(){return this.y;}
    


}
