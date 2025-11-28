package game.utils;

import java.util.HashMap;
import java.util.Map;

public class Command {
    private final String target;
    private final String action;
    private final Map<String, String> attributes;
    public Command(String target, String action, Map<String, String> attributes){
        this.target = target;
        this.action = action;
        this.attributes = attributes;
    }
    public String getTarget() { return target; }
    public String getAction() { return action; }
    public Map<String,String> getAttributes() { return attributes;}
    public String getAttribute(String key) {
        return attributes.getOrDefault(key, null);
    }
}
