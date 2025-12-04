package game;


import game.model.DarkKnight;
import game.model.Enemy;
import game.model.Entity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import game.model.GameObject;
import game.model.Player;
import game.model.Tower;
import game.utils.Assets;
import game.utils.Command;
import game.utils.CommandBuffer;

public class GameManager {
    public Assets mainAssets;
    private List<GameObject> objectList;
    private List<GameObject> pendingObjects;
    private CommandBuffer commandBuffer;
    
    GameManager(Assets mainAssets, CommandBuffer commandBuffer ){
        this.objectList = new ArrayList<GameObject>();
        this.commandBuffer = commandBuffer;
        this.mainAssets = mainAssets;
        this.pendingObjects = new ArrayList<>();
    }

    public void update(float deltaTime){
        
        java.util.Iterator<GameObject> iterator = objectList.iterator(); 

        List<Command> pendingCommands = new ArrayList<>(); // A command is represented as: Target, Action, Attributes
        commandBuffer.drainTo(pendingCommands); // Drains the commandBuffer (containing commands) to an Array-List each tick

        
        Map<String, Command> commandMap = new HashMap<>(); // Creates a hashmap
        for (Command c : pendingCommands) {
            commandMap.put(c.getTarget(), c); // The hashmap maps Name -> Command (Target, Action, Attributes)
        }

        Command systemAction = commandMap.get("GameManager"); // If any command in the map is intended for the GameManager

        if (systemAction != null) {
            this.handleSystemAction(systemAction);  // Assuming we have a command for the GameManager, it forwards it
            commandMap.remove("GameManager"); // Remove the GameManager/System-intended command, so it doesn't try to update a GameObject
        }
        while(iterator.hasNext()){
            GameObject gameObject = iterator.next();



            if(gameObject instanceof Enemy enemy){ 
                Player newTarget = null;
                if(!enemy.hasValidTarget()){
                    newTarget = findClosestPlayer(enemy);
                    if(newTarget!=null){
                        if(newTarget instanceof Tower){
                            enemy.setIsTargetingPlayer(false);
                        }else{
                            enemy.setIsTargetingPlayer(true);
                        }
                        enemy.setFinalTarget(newTarget);
                        System.out.println(enemy.getFinalTarget().getClassName());
                    }
                    
                }else if(enemy.isTargetingPlayer()){
                    Player currentTarget = enemy.getFinalTarget();
                    // Check to see if the current target (non-tower) has gone out of range
                    if (currentTarget != null) {
                        float dx = currentTarget.getX() - enemy.getX();
                        float dy = currentTarget.getY() - enemy.getY();
                        float dist = (float)Math.hypot(dx, dy);
                        if (dist > enemy.getPlayerDetectionRadius()) {
                            // Target out of range -> switch back to default pathing/tower target.
                            enemy.resetTargetPlayer();
                    }
                }

            }


            Command currentCommand = commandMap.get(gameObject.getObjectName()); // Fetches Entity related commands for the current entity
            String action = null; // Fetches the specific action from the command object (Target, Action, Attributes)

            if(currentCommand!=null){ // Determines if there's a command enqueued for the current entity
                action = currentCommand.getAction(); // Fetches that command action (String)
            }
            if(action!=null){
                gameObject.update(deltaTime, mainAssets, action);
            } else {
                gameObject.update(deltaTime, mainAssets, null);
            }
        }
             if (gameObject instanceof Entity) {
                Entity entity = (Entity) gameObject;
                if (!entity.isAlive()) {
                    iterator.remove(); 
                }
            }         
        }

        if(!pendingObjects.isEmpty()){
            objectList.addAll(pendingObjects);
            pendingObjects.clear();
        }
    }
    public void spawnObject(GameObject toAdd){
        pendingObjects.add(toAdd); // We add new objects to a pending list, which is dumped into the actual game object list in the next tick.
    }
    public List<GameObject> getGameObjects(){
        return List.copyOf(this.objectList);
    }
    private void handleSystemAction(Command systemAction){
        switch (systemAction.getAction()){
            case "SpawnBarbarian" -> {
                float xValue = Float.parseFloat(systemAction.getAttribute("x"));
                float yValue = Float.parseFloat(systemAction.getAttribute("y"));
                spawnObject(new DarkKnight(xValue,yValue, 100, 50f, null));
                System.out.println("Barbarian Created");
               
            }
        }

    }
    public Player findClosestPlayer(Enemy enemy){ // If it finds a Player (not a Tower) within a certain radius, it returns that. Else it defaults to the closest Tower
        java.util.Iterator<GameObject> iterator = objectList.iterator();
        Player minimumPlayer = null;
        float minimumPlayerDistance = enemy.getPlayerDetectionRadius();
        float minimumTowerDistance = Float.MAX_VALUE;
        boolean isPlayer;
        Tower defaultTower = null;
        while(iterator.hasNext()){
            GameObject gameObject = iterator.next(); 
            if(gameObject instanceof Player){
                
                float dx = gameObject.getX() - enemy.getX();
                float dy = gameObject.getY() - enemy.getY();
                float dist = (float)Math.hypot(dx, dy);

                isPlayer = !(gameObject instanceof Tower);
                if(isPlayer){
                    if(dist<minimumPlayerDistance){
                        minimumPlayerDistance = dist;
                        minimumPlayer = (Player) gameObject;
                    }
                }else if(gameObject instanceof Tower){
                    if(dist<minimumTowerDistance){
                        minimumTowerDistance = dist;
                        defaultTower = (Tower) gameObject;
                    }
                }
                
            }

        }

        if (minimumPlayer != null) {
            // Return the closest dynamic Player (Priority 1)
            return minimumPlayer;
        } else {
            // Otherwise, return the closest Tower (Priority 2 fallback)
            return defaultTower;
        }
    }

}
