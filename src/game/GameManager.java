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
        for(GameObject iterator : objectList){
            iterator.update(deltaTime);
            if(iterator instanceof Entity){
                if(!((Entity)iterator).isAlive()){
                    objectList.remove(iterator);
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
