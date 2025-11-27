package game;

import game.model.Entity;
import java.util.ArrayList;
import java.util.List;

import game.model.GameObject;
import game.utils.Assets;

public class GameManager {
    public Assets mainAssets;
    private List<GameObject> objectList;

    GameManager(Assets mainAssets){
        this.objectList = new ArrayList<GameObject>();
        this.mainAssets = mainAssets;
    }

    public void update(float deltaTime){
        java.util.Iterator<GameObject> iterator = objectList.iterator();
        while(iterator.hasNext()){
            GameObject gameObject = iterator.next();
            gameObject.update(deltaTime, mainAssets);
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
