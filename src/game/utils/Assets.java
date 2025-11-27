package game.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
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
    public static final String ANIM_RUN_FORWARD = "runForward";
    public static final String ANIM_ATTACK_FORWARD = "attackForward";
    // Add others later: "attackForward", "runLeft", etc.
    
    public Assets() {
        this.gameAssets = new HashMap<>();
        loadCharacterAssets("Barbarian");
        loadCharacterAssets("Skeleton");
    }
    private void loadCharacterAssets(String character){
        loadAnimation(character, ANIM_RUN_FORWARD, 6);
        loadAnimation(character, ANIM_ATTACK_FORWARD, 7);
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