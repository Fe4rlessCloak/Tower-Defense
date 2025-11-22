package game;

import game.model.Entity;
import java.util.ArrayList;
import java.util.List;

import game.model.GameObject;

public class GameManager {
    private List<GameObject> objectList;

    GameManager(){
        this.objectList = new ArrayList<GameObject>();
    }

    public void update(float deltaTime){
        java.util.Iterator<GameObject> iterator = objectList.iterator();
        while(iterator.hasNext()){
            GameObject gameObject = iterator.next();
            gameObject.update(deltaTime);
            if (gameObject instanceof Entity) {
                Entity entity = (Entity) gameObject;
                if (!entity.isAlive()) {
                    iterator.remove(); 
                }
            }
        }
    }
    public void spawnObject(GameObject toAdd){
        objectList.add(toAdd);
    }
    public List<GameObject> getGameObjects(){
        return List.copyOf(this.objectList);
    }


}
