package game;

import game.model.Barbarian;
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

    public void update(float deltaTime) {
        java.util.Iterator<GameObject> iterator = objectList.iterator();

        List<Command> pendingCommands = new ArrayList<>();
        commandBuffer.drainTo(pendingCommands);

        Map<String, Command> commandMap = new HashMap<>();
        for (Command c : pendingCommands) {
            commandMap.put(c.getTarget(), c);
        }

        Command systemAction = commandMap.get("GameManager");

        if (systemAction != null) {
            this.handleSystemAction(systemAction);
            commandMap.remove("GameManager");
        }

        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
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

            if (gameObject instanceof Enemy enemy) {
                Player newTarget = null;
                if (!enemy.hasValidTarget()) {
                    newTarget = findClosestPlayer(enemy);
                }
                if (newTarget != null) {
                    enemy.setFinalTarget(newTarget);
                }
            }
            if (gameObject instanceof Entity) {
                Entity entity = (Entity) gameObject;
                if (!entity.isAlive()) {
                    iterator.remove();
                }
            }
        }
        if (!pendingObjects.isEmpty()) {
            objectList.addAll(pendingObjects);
            pendingObjects.clear();
        }
    }

    public void spawnObject(GameObject toAdd) {
        pendingObjects.add(toAdd);
    }

    public List<GameObject> getGameObjects() {
        return List.copyOf(this.objectList);
    }

    private void handleSystemAction(Command systemAction) {
        switch (systemAction.getAction()) {
            case "SpawnBarbarian" -> {
                float xValue = Float.parseFloat(systemAction.getAttribute("x"));
                float yValue = Float.parseFloat(systemAction.getAttribute("y"));
                spawnObject(new Barbarian(xValue, yValue, 100, 50f, null));
                System.out.println("Barbarian Created");
            }
            case "SpawnTower" -> {
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
                            spawnObject(new Tower(xValue, yValue, 200, 0f));
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

    public Player findClosestPlayer(Enemy enemy) {
        java.util.Iterator<GameObject> iterator = objectList.iterator();
        Player minimumPlayer = null;
        float minimumDistance = Float.MAX_VALUE;
        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            if (gameObject instanceof Player) {
                float dx = gameObject.getX() - enemy.getX();
                float dy = gameObject.getY() - enemy.getY();
                float dist = (float) Math.hypot(dx, dy);
                if (dist < minimumDistance) {
                    minimumDistance = dist;
                    minimumPlayer = (Player) gameObject;
                }
            }
        }
        return minimumPlayer;
    }
}