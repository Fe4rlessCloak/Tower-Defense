package game.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

public class Assets {

    // Map: AnimationName (String) -> List of Frames (BufferedImage)
    private Map<String, List<BufferedImage>> barbarianAnimations;
    
    // Constants matching your folder structure
    public static final String ANIM_RUN_FORWARD = "runForward";
    // Add others later: "attackForward", "runLeft", etc.
    
    public Assets() {
        this.barbarianAnimations = new HashMap<>();
        loadBarbarianAssets();
    }

    private void loadBarbarianAssets() {
        // Load the sequence. Assumes files are named 1.png, 2.png, ... 6.png
        // Change '6' to however many frames are in your runForward folder
        loadAnimation("Barbarian", ANIM_RUN_FORWARD, 6); 
    }
    
    private void loadAnimation(String character, String animationName, int frameCount) {
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
    }
    
    public BufferedImage getBarbarianFrame(String animationName, int frameIndex) {
        List<BufferedImage> frames = barbarianAnimations.get(animationName);
        if (frames != null && !frames.isEmpty()) {
            // Safety check: ensure index is within bounds
            if (frameIndex >= frames.size()) {
                 frameIndex = 0;
            }
            return frames.get(frameIndex);
        }
        return null;
    }
}