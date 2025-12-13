package game.utils;

import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class Assets {
    private Map<String, Map<String, List<BufferedImage>>> gameAssets;
    // Map: AnimationName (String) -> List of Frames (BufferedImage)
    // private Map<String, List<BufferedImage>> barbarianAnimations;
    // private Map<String, List<BufferedImage>> skeletonAnimations;
    // Constants matching your folder structure
    public static final String ANIM_RUN_DOWNWARD = "runDown";
    public static final String ANIM_RUN_LEFT = "runLeft";
    public static final String ANIM_RUN_RIGHT = "runRight";
    public static final String ANIM_RUN_DOWN_LEFT = "runDownLeft";
    public static final String ANIM_RUN_DOWN_RIGHT = "runDownRight";
    public static final String ANIM_RUN_TOP = "runTop";
    public static final String ANIM_RUN_TOP_RIGHT = "runTopRight";
    public static final String ANIM_RUN_TOP_LEFT = "runTopLeft";
    

    public static final String ANIM_ATTACK_DOWNWARD = "attackDown";
    public static final String ANIM_ATTACK_LEFT = "attackLeft";
    public static final String ANIM_ATTACK_RIGHT = "attackRight";
    public static final String ANIM_ATTACK_DOWN_LEFT = "attackDownLeft";
    public static final String ANIM_ATTACK_DOWN_RIGHT = "attackDownRight";
    public static final String ANIM_ATTACK_TOP = "attackTop";
    public static final String ANIM_ATTACK_TOP_RIGHT = "attackTopRight";
    public static final String ANIM_ATTACK_TOP_LEFT = "attackTopLeft";


    public static final String ANIM_IDLE_DOWNWARD = "idleDown";
    public static final String ANIM_IDLE_LEFT = "idleLeft";
    public static final String ANIM_IDLE_RIGHT = "idleRight";
    public static final String ANIM_IDLE_DOWN_LEFT = "idleDownLeft";
    public static final String ANIM_IDLE_DOWN_RIGHT = "idleDownRight";
    public static final String ANIM_IDLE_TOP = "idleTop";
    public static final String ANIM_IDLE_TOP_RIGHT = "idleTopRight";
    public static final String ANIM_IDLE_TOP_LEFT = "idleTopLeft";



    public static final String ANIM_IDLE = "Idle";
    // Add others later: "attackForward", "runLeft", etc.
    
    public Assets() {
        this.gameAssets = new HashMap<>();
        loadCharacterAssets("DarkKnight");
        loadCharacterAssets("Tower");
        loadCharacterAssets("Tower");
    }
    private void loadCharacterAssets(String character){
        switch(character){
            case "DarkKnight" -> {
                loadAnimation(character, ANIM_RUN_DOWNWARD, 10);
                loadAnimation(character, ANIM_RUN_LEFT, 8);
                loadAnimation(character, ANIM_RUN_RIGHT, 8);
                loadAnimation(character, ANIM_RUN_DOWN_LEFT, 8);
                loadAnimation(character, ANIM_RUN_DOWN_RIGHT, 8);
                loadAnimation(character, ANIM_RUN_TOP, 8);
                loadAnimation(character, ANIM_RUN_TOP_LEFT, 8);
                loadAnimation(character, ANIM_RUN_TOP_RIGHT, 8);

                loadAnimation(character, ANIM_ATTACK_DOWNWARD, 10);
                loadAnimation(character, ANIM_ATTACK_LEFT, 9);
                loadAnimation(character, ANIM_ATTACK_RIGHT, 9);
                loadAnimation(character, ANIM_ATTACK_DOWN_LEFT, 10);
                loadAnimation(character, ANIM_ATTACK_DOWN_RIGHT, 10);
                loadAnimation(character, ANIM_ATTACK_TOP, 9);
                loadAnimation(character, ANIM_ATTACK_TOP_LEFT, 10);
                loadAnimation(character, ANIM_ATTACK_TOP_RIGHT, 10);

                loadAnimation(character, ANIM_IDLE_DOWNWARD, 1);
                loadAnimation(character, ANIM_IDLE_LEFT, 1);
                loadAnimation(character, ANIM_IDLE_RIGHT, 1);
                loadAnimation(character, ANIM_IDLE_DOWN_LEFT, 1);
                loadAnimation(character, ANIM_IDLE_DOWN_RIGHT, 1);
                loadAnimation(character, ANIM_IDLE_TOP, 1);
                loadAnimation(character, ANIM_IDLE_TOP_LEFT, 1);
                loadAnimation(character, ANIM_IDLE_TOP_RIGHT, 1);
            }
            case "Tower" -> {
                loadAnimation(character, ANIM_ATTACK_LEFT, 5);
                loadAnimation(character, ANIM_ATTACK_RIGHT, 5);
                loadAnimation(character, ANIM_ATTACK_TOP, 5);
                loadAnimation(character, ANIM_ATTACK_TOP_LEFT, 5);
                loadAnimation(character, ANIM_ATTACK_TOP_RIGHT, 5);
                loadAnimation(character, ANIM_IDLE_LEFT, 1);
                loadAnimation(character, ANIM_IDLE_RIGHT, 1);
                loadAnimation(character, ANIM_IDLE_TOP, 1);
                loadAnimation(character, ANIM_IDLE_TOP_LEFT, 1);
                loadAnimation(character, ANIM_IDLE_TOP_RIGHT, 1);
            }
            default -> System.err.println("Warning: Attempted to load unknown character type: " + character);
        }
        
        
    }
  
    private void loadAnimation(String character, String animationName, int frameCount){
        gameAssets.putIfAbsent(character, new HashMap<>());
        Map<String, List<BufferedImage>> characterAnimations = gameAssets.get(character); // Fetches the inner map
        List<BufferedImage> frames = new ArrayList<>();
        
        for (int i = 1; i <= frameCount; i++) {
            // Path: /resources/Barbarian/runForward/1.png
            String path = "/resources/" + "sprites/" +character + "/" + animationName + "/" + i + ".png";
            try {
                BufferedImage frame = ImageIO.read(getClass().getResourceAsStream(path));
                if (frame != null) {
                    frames.add(frame);
                } else {
                    System.err.println("Frame not found: " + path);
                }
            } catch (Exception e) {
                System.err.println("Failed to load frame: " + path);
                // e.printStackTrace(); // Uncomment to see exact error
            }
        }
        characterAnimations.put(animationName, frames);
    }
    /*private void loadAnimation(String character, String animationName, int frameCount) {
        List<BufferedImage> frames = new ArrayList<>();
        
        for (int i = 1; i <= frameCount; i++) {
            // Path: /resources/Barbarian/runForward/1.png
            String path = "/resources/" + "sprites/" +character + "/" + animationName + "/" + i + ".png";
            try {
                BufferedImage frame = ImageIO.read(getClass().getResourceAsStream(path));
                if (frame != null) {
                    frames.add(frame);
                } else {
                    System.err.println("Frame not found: " + path);
                }
            } catch (Exception e) {
                System.err.println("Failed to load frame: " + path);
                // e.printStackTrace(); // Uncomment to see exact error
            }
        }
        barbarianAnimations.put(animationName, frames);
    }*/
    
    public BufferedImage getFrame(String characterName, String animationName, int frameIndex) {
        Map<String, List<BufferedImage>> characterMap = gameAssets.get(characterName);

        List<BufferedImage> frames = characterMap.get(animationName);
        if (frames != null && !frames.isEmpty()) {
            // Safety check: ensure index is within bounds
            if (frameIndex >= frames.size()) {
                 frameIndex = 0;
            }
            return frames.get(frameIndex);
        }
        return null;
    }
    public int getFrameCount(String characterName, String animationName) {
        Map<String, List<BufferedImage>> characterMap = gameAssets.get(characterName);
        
        // Check if character exists
        if (characterMap == null) return 1; // Default minimum frame count

        List<BufferedImage> frames = characterMap.get(animationName);
        
        // Check if animation exists
        if (frames == null) return 1; // Default minimum frame count
        
        return frames.size();
    }
}