package game.model;

public abstract class Entity extends GameObject {
    protected int health;
    protected float speed;

    Entity(float x, float y, int health, float speed){
        super(x, y);
        this.health = health;
        this.speed = speed;
    }
    public void takeDamage(int damage){
        this.health = this.health - damage;
    }
    public boolean isAlive(){
        return health>0;
    }
}
