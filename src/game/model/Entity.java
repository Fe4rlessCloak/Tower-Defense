package game.model;

public abstract class Entity extends GameObject {
    protected int health;
    protected float speed;
    private final int OBJECT_SIZE = 96;
    final int FRAME_OFFSET = OBJECT_SIZE / 4; 
    Entity(float x, float y, int health, float speed, String objectName){
        super(x, y, objectName);
        this.health = health;
        this.speed = speed;
    }
    Entity(){

    }
    public void takeDamage(int damage){
        this.health = this.health - damage;
    }
    public boolean isAlive(){
        return health>0;
    }
    public int getObjectSize(){
        return this.OBJECT_SIZE;
    }
    public int getFrameOffset(){
        return this.FRAME_OFFSET;
    }
    
}
