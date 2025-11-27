package game;


import game.model.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



import game.model.GameObject;
import game.utils.Assets;
import game.utils.Command;
import game.utils.CommandBuffer;

public class GameManager {
    public Assets mainAssets;
    private List<GameObject> objectList;
    
    private CommandBuffer commandBuffer;
    
    GameManager(Assets mainAssets, CommandBuffer commandBuffer ){
        this.objectList = new ArrayList<GameObject>();
        this.commandBuffer = commandBuffer;
        this.mainAssets = mainAssets;
    }

    public void update(float deltaTime){
        java.util.Iterator<GameObject> iterator = objectList.iterator();

        List<Command> pendingCommands = new ArrayList<>();
        commandBuffer.drainTo(pendingCommands);

        
        Map<String, String> commandMap = new HashMap<>();
        for (Command c : pendingCommands) {
            commandMap.put(c.getTarget(), c.getAction());
        }

        
            
        while(iterator.hasNext()){
            
            GameObject gameObject = iterator.next();
            String action = commandMap.get(gameObject.getObjectName());
            if(action!=null){
                gameObject.update(deltaTime, mainAssets, action);
            } else {
                gameObject.update(deltaTime, mainAssets, null);
            }
                
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
