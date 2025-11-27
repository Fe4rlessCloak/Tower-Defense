package game.utils;

import game.model.GameObject;

public class Command {
    private final String target;
    private final String action;
    public Command(String target, String action){
        this.target = target;
        this.action = action;
    }
    public String getTarget() { return target; }
    public String getAction() { return action; }

}
