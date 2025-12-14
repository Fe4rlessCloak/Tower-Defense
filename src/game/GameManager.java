package game;


import game.model.DarkKnight;
import game.model.Enemy;
import game.model.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.model.GameObject;
import game.model.Player;
import game.model.Tower;
import game.model.User;
import game.utils.Assets;
import game.utils.Command;
import game.utils.CommandBuffer;

public class GameManager {
    public Assets mainAssets;
    private List<GameObject> objectList;
    private List<GameObject> pendingObjects;
    private CommandBuffer commandBuffer;
private boolean reevaluateClosestTarget = false;
    private User user; // The shared player object
    private boolean buildMode = false;
    private static final int TOWER_COST = 100; // adjust as needed

    GameManager(Assets mainAssets, CommandBuffer commandBuffer) {
        this.objectList = new ArrayList<GameObject>();
        this.commandBuffer = commandBuffer;
        this.mainAssets = mainAssets;
        this.pendingObjects = new ArrayList<>();
    }

    // Player registration
    public void setUser(User p) {
        this.user = p;
    }

    public User getUser() {
        return this.user;
    }

    public void setBuildMode(boolean value) {
        this.buildMode = value;
    }

    public boolean isBuildMode() {
        return this.buildMode;
    }

    
    public void update(float deltaTime){
        
        java.util.Iterator<GameObject> iterator = objectList.iterator(); 

        List<Command> pendingCommands = new ArrayList<>(); // A command is represented as: Target, Action, Attributes
        commandBuffer.drainTo(pendingCommands); // Drains the commandBuffer (containing commands) to an Array-List each tick

        Map<String, Command> commandMap = new HashMap<>();
        for (Command c : pendingCommands) {
            commandMap.put(c.getTarget(), c); // The hashmap maps Target -> Command (Target, Action, Attributes)
        }

        Command systemAction = commandMap.get("GameManager");

        if (systemAction != null) {
            this.handleSystemAction(systemAction); // Assuming we have a command for the GameManager, it forwards it
            commandMap.remove("GameManager"); // Remove the GameManager/System-intended command, so it doesn't try to update a GameObject
        }

        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            if(reevaluateClosestTarget){
                if(gameObject instanceof Enemy enemy){
                    setNewPlayerTarget(enemy);
                    checkToResetToTower(enemy);
                }else if(gameObject instanceof Player player){
                    setNewEnemyTarget(player);
                }
            }else{
                if(gameObject instanceof Enemy enemy){ 
                    if(!enemy.hasValidTarget()){
                        setNewPlayerTarget(enemy);
                    }else if(enemy.isTargetingPlayer()){ // Even though the Game Manager is not supposed to micro-manage Enemy's state, here we need a check to see if the enemy should target a Tower or a non-Tower (and get that tower as well)
                        checkToResetToTower(enemy);
                    }
                }
                if(gameObject instanceof Player player){
                    if(!player.hasValidTarget()){
                        setNewEnemyTarget(player);
                    }
                }
            }
            
            Command currentCommand = commandMap.get(gameObject.getObjectName());
            String action = null;

            if (currentCommand != null) {
                action = currentCommand.getAction();
            }
            if (action != null) {
                gameObject.update(deltaTime, mainAssets, action);
            } else {
                gameObject.update(deltaTime, mainAssets, null);
                
            }
        
            if(gameObject instanceof Entity) {
                Entity entity = (Entity) gameObject;
                if (!entity.isAlive() && entity.removalCandidate()) {
                    
                    iterator.remove(); 
                }
            }         
        }
        reevaluateClosestTarget = false;
        if(!pendingObjects.isEmpty()){
            objectList.addAll(pendingObjects);
            reevaluateClosestTarget = true;
            pendingObjects.clear();
        }
    }

    public void spawnObject(GameObject toAdd) {
        pendingObjects.add(toAdd); // We add new objects to a pending list, which is dumped into the actual game object list in the next tick.
    }
    

    public List<GameObject> getGameObjects() {
        return List.copyOf(this.objectList);
    }

    private void handleSystemAction(Command systemAction) {
        switch (systemAction.getAction()) {
            case "SpawnBarbarian" -> {
                float xValue = Float.parseFloat(systemAction.getAttribute("x"));
                float yValue = Float.parseFloat(systemAction.getAttribute("y"));
                spawnObject(new DarkKnight(xValue,yValue));
                System.out.println("Barbarian Created");
               
            }case "SpawnTower" -> {
                // Attempt to spawn a tower if player has enough gold
                float xValue = Float.parseFloat(systemAction.getAttribute("x"));
                float yValue = Float.parseFloat(systemAction.getAttribute("y"));
                if (this.user == null) {
                    System.err.println("SpawnTower failed: No player registered.");
                    return;
                }
                synchronized (this.user) {
                    if (this.user.getGold() >= TOWER_COST) {
                        boolean success = this.user.spendGold(TOWER_COST);
                        if (success) {
                            spawnObject(new Tower(xValue, yValue));
                            System.out.println("Tower spawned at (" + xValue + "," + yValue + ")");
                        } else {
                            System.out.println("Tower spawn failed: spendGold returned false.");
                        }
                    } else {
                        System.out.println("Not enough gold to spawn tower. Required: " + TOWER_COST + ", have: " + this.user.getGold());
                    }
                }
            }
            case "AddGold" -> {
                int amount = Integer.parseInt(systemAction.getAttribute("amount"));
                if (this.user != null) this.user.addGold(amount);
            }
        }

    }

     // Check to see if the current target (non-tower) has gone out of range
     // Target out of range -> switch back to default pathing/tower target.
    public void checkToResetToTower(Enemy enemy){ 
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
    public void setNewEnemyTarget(Player player){
        Enemy newEnemyTarget = null;
        newEnemyTarget = findClosestEnemy(player);
        if(newEnemyTarget!=null){
            player.setFinalTarget(newEnemyTarget);
        }
    }
    public void setNewPlayerTarget(Enemy enemy){
        Player newTarget = null;
        newTarget = findClosestPlayer(enemy);
        if(newTarget!=null){
            if(newTarget instanceof Tower){
                enemy.setIsTargetingPlayer(false);
            }else{
                enemy.setIsTargetingPlayer(true);
            }
            enemy.setFinalTarget(newTarget);  
        }
    }

    
    public Enemy findClosestEnemy(Player player){
        java.util.Iterator<GameObject> iterator = objectList.iterator();
        Enemy minimumEnemy = null;
        float minimumDistance = Float.MAX_VALUE;
        while(iterator.hasNext()){
            GameObject gameObject = iterator.next();
            if(gameObject instanceof Enemy){
                float dx = gameObject.getX() - player.getX();
                float dy = gameObject.getY() - player.getY();
                float dist = (float)Math.hypot(dx, dy);
                if(dist<minimumDistance){
                    minimumDistance = dist;
                    minimumEnemy = (Enemy) gameObject;
                }
            }
        }
        return minimumEnemy;
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
