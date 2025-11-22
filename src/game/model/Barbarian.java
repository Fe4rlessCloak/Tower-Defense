package game.model;

public class Barbarian extends Entity{

    public Barbarian(float x, float y, int health, float speed) {
        super(x, y, health, speed);
        
    }

    @Override
    public void update(float deltaTime) {
        this.x += this.speed * deltaTime;
    }
    
    
}
