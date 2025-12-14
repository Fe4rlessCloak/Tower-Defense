package game.ui;

import game.GameManager;
import game.model.GameObject;
import game.model.Player;
import game.model.Enemy;
import game.model.Entity;
import game.model.Entity.FloatingText;
import game.utils.Assets;
import game.utils.Command;
import game.utils.CommandBuffer;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.image.BufferedImage;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;


public class GamePanel extends JPanel implements MouseListener {
    
    // Core Dependencies
    private GameManager gameManager;
    private Assets assets; // Handles all sprite loading
    private BufferedImage backgroundImage;

    // Screen Settings
    private static final int GAME_WIDTH = 500;
    private static final int GAME_HEIGHT = 693;

    private ExecutorService producerExecutor;
    private CommandBuffer commandBuffer;

    public GamePanel(GameManager manager,ExecutorService producerExecutor,CommandBuffer commandBuffer) {
        this.gameManager = manager;
        this.producerExecutor = producerExecutor;
        this.commandBuffer = commandBuffer;
        // 1. Initialize Screen Settings
        this.setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        this.setDoubleBuffered(true); // Smoother rendering
        this.setFocusable(true);
        this.requestFocusInWindow();

        // 2. Initialize Inputs
        this.addMouseListener(this);

        // 3. Load Resources
        this.assets = new Assets(); // Loads the Barbarian sprites
        loadBackgroundImage();
    }
    
    private void loadBackgroundImage() {
        try {
            // Adjust this path if your map is named differently or in a different subfolder
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/resources/images/map.png"));
        } catch (Exception e) {
            System.err.println("Error: Could not load background image.");
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        // --- LAYER 1: Background ---
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            // Fallback Dark Grey
            g.setColor(new Color(40, 40, 40)); 
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        

        // --- LAYER 2: Game Objects ---
        List<GameObject> objects = gameManager.getGameObjects();
        List<GameObject> sortedObjects = new java.util.ArrayList<>(objects);
         sortedObjects.sort((a, b) -> {
            // Compare Y-coordinates for depth sorting
            // a.getY() - b.getY() sorts ascending (smallest Y first)
            // This ensures objects at the bottom (larger Y) are drawn last.
                return Float.compare(a.getY(), b.getY());
            });
        for (GameObject obj : sortedObjects) {

           

            int frameW = obj.getObjectSize();
            int frameH = obj.getObjectSize();

            int drawX = (int)(obj.getX() - frameW / 2);
            int drawY = (int)(obj.getY() - frameH / 2);
            Entity entity = null;
            BufferedImage frame = null;
            if (obj instanceof Enemy enemy) {
                frame = assets.getFrame(enemy.getClassName(), enemy.getCurrentAnimation(), enemy.getCurrentFrame());
                entity = enemy;
            } else if(obj instanceof Player player){
                frame = assets.getFrame(player.getClassName(), player.getCurrentAnimation(), player.getCurrentFrame());
                entity = player; // Towers are entities!
            }
            if (frame == null) {
                g2d.setColor(Color.RED);
                g2d.fillRect(drawX, drawY, frameW, frameH);
                continue; 
            }
            float opacity = 1.0f; 
            Composite originalComposite = g2d.getComposite();
            if (entity != null && entity.isFading()) { // <-- Use the clean isFading() method
                // If dying, calculate the fade opacity.
                opacity = entity.getDeathTimer() / entity.getDeathFadeDuration();
                if (opacity < 0.0f) opacity = 0.0f;
            }

            g2d.setComposite(AlphaComposite.SrcOver.derive(opacity));
            g2d.drawImage(frame, drawX, drawY, frameW, frameH, null);

            if (entity != null && entity.getDamageFlashTimer() > 0 && !entity.isFading()) {
            
                // 1. Reset composite temporarily for the flash overlay
                g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f)); 

                // 2. Create the flash buffer (This is the guaranteed masking technique)
                BufferedImage flashBuffer = new BufferedImage(frameW, frameH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2dBuffer = flashBuffer.createGraphics();
                
                g2dBuffer.drawImage(frame, 0, 0, frameW, frameH, null);
                
                // Apply red tint to buffer
                g2dBuffer.setComposite(AlphaComposite.SrcAtop.derive(0.8f));
                g2dBuffer.setColor(Color.RED); 
                g2dBuffer.fillRect(0, 0, frameW, frameH); 
                g2dBuffer.dispose();
                
                // 3. Draw the flashing buffer on top (at full opacity, as fade is 
                // handled by the base draw). We can simplify this draw to full 1.0f
                g2d.drawImage(flashBuffer, drawX, drawY, null); 
            }
            if (entity != null && entity.isAlive()) {
    
                // 1. Define bar properties
                int barWidth = frameW; // Match the width of the entity sprite
                int barHeight = 6;     // Thin bar
                int barY = drawY - 10; // 10 pixels above the sprite
                
                float currentHealthRatio = (float)entity.getHealth() / entity.getMaxHealth();
                
                int healthBarFill = (int)(barWidth * currentHealthRatio);
                
                // 2. Draw the background/empty bar (e.g., black)
                g2d.setColor(Color.BLACK);
                g2d.setComposite(AlphaComposite.SrcAtop.derive(0.5f));
                g2d.fillRect(drawX, barY, barWidth, barHeight);
                
                // 3. Draw the health fill (e.g., green/red gradient)
                g2d.setColor(Color.GREEN);
                
                // Optional: Turn the bar yellow or red when health is low
                if (currentHealthRatio < 0.5f) {
                    g2d.setColor(Color.YELLOW);
                }
                if (currentHealthRatio < 0.3f) {
                    g2d.setColor(Color.RED);
                }
                
                g2d.fillRect(drawX, barY, healthBarFill, barHeight);
                
                // Optional: Draw a white border
                g2d.setColor(Color.WHITE);
                g2d.drawRect(drawX, barY, barWidth, barHeight);
            }
            if (entity != null) {
                ArrayList<FloatingText> textsToRender;
                synchronized(entity.getActiveTexts()){
                    textsToRender = new java.util.ArrayList<>(entity.getActiveTexts());
                }
                for (FloatingText text : textsToRender) { // Access the list
                    opacity = text.getOpacity();
                    g2d.setComposite(AlphaComposite.SrcOver.derive(opacity));
                    int yOffset = (int)text.currentYOffset;
                    if (text.isCritical) {
                            // --- 1. SETUP: FONTS AND COLORS ---
            
                            String displayDamage = String.valueOf(text.damageValue);
                            String displayCrit = "CRITICAL!";

                            // Define fonts for measurement
                            Font damageFont = new Font("Arial", Font.BOLD, 18);
                            Font critFont = new Font("Arial", Font.BOLD, 22);

                            // Get font metrics for accurate width/height
                            // NOTE: You must set the font first to get its metrics
                            g2d.setFont(damageFont); 
                            FontMetrics fmDamage = g2d.getFontMetrics();
                            g2d.setFont(critFont); 
                            FontMetrics fmCrit = g2d.getFontMetrics();

                            // NEW COLOR DEFINITIONS
                            Color MAIN_CRIT_COLOR = new Color(255, 153, 0);  // Bright Orange/Gold
                            Color CRIT_TEXT_COLOR = new Color(255, 230, 0);  // Neon Yellow
                            Color OUTLINE_COLOR = new Color(150, 0, 0);       // Dark Red Outline
                            
                            int outlineOffset = 2; // Pixel offset for the outline shadow
                            
                            // --- 2. ALIGNMENT CALCULATIONS (Creating the necessary variables) ---
                            
                            // Calculate widths
                            int damageWidth = fmDamage.stringWidth(displayDamage);
                            int critWidth = fmCrit.stringWidth(displayCrit);
                            int maxWidth = Math.max(damageWidth, critWidth);

                            // Calculate heights
                            int lineHeightDamage = fmDamage.getHeight();
                            int lineHeightCrit = fmCrit.getHeight();
                            int totalHeight = lineHeightDamage + lineHeightCrit; 

                            // Define the Center Anchor Point
                            int anchorX = drawX + frameW / 2;
                            int baseAnchorY = drawY - yOffset - 40; 

                            // Calculate the TOP-LEFT starting point for the whole block
                            int startDrawY = baseAnchorY - totalHeight / 2;

                            // Calculate the centered Draw X for each line
                            int damageDrawX = anchorX - damageWidth / 2;
                            int critDrawX = anchorX - critWidth / 2;
                            
                            // Calculate the centered Draw Y for each line
                            int damageDrawY = startDrawY + lineHeightDamage;
                            int critDrawY = damageDrawY + lineHeightCrit; 

                            
                            // --- 3. DRAW SHADOW/OUTLINE FIRST ---
                            
                            // CRITICAL TEXT SHADOW
                            g2d.setFont(critFont);
                            g2d.setColor(OUTLINE_COLOR);
                            g2d.drawString(displayCrit, critDrawX + outlineOffset, critDrawY + outlineOffset);
                            
                            // DAMAGE NUMBER SHADOW
                            g2d.setFont(damageFont);
                            g2d.setColor(OUTLINE_COLOR);
                            g2d.drawString(displayDamage, damageDrawX + outlineOffset, damageDrawY + outlineOffset);


                            // --- 4. DRAW MAIN TEXT ON TOP ---
                            
                            // CRITICAL TEXT MAIN
                            g2d.setFont(critFont);
                            g2d.setColor(CRIT_TEXT_COLOR);
                            g2d.drawString(displayCrit, critDrawX, critDrawY);
                            
                            // DAMAGE NUMBER MAIN
                            g2d.setFont(damageFont);
                            g2d.setColor(MAIN_CRIT_COLOR);
                            g2d.drawString(displayDamage, damageDrawX, damageDrawY);
                        
                    } else {
                        String display = String.valueOf(text.damageValue);
                        
                        // Calculate Opacity and Position
                        opacity = text.getOpacity();
                        yOffset = (int)text.currentYOffset;

                        // Set the drawing environment
                        g2d.setColor(text.isCritical ? Color.RED : Color.YELLOW);
                        g2d.setFont(new Font("Arial", Font.BOLD, 18)); 
                        
                        originalComposite = g2d.getComposite();
                        g2d.setComposite(AlphaComposite.SrcOver.derive(opacity));

                        // Draw the text (Position is above the entity center, offset by the float distance)
                        g2d.drawString(display, 
                                    drawX + frameW/2 - g2d.getFontMetrics().stringWidth(display) / 2, 
                                    drawY - yOffset - 25);
                        
                                    
                        g2d.setComposite(originalComposite);
                    }
                
            }
            // --- E. Final Cleanup (CRITICAL) ---
            // Restore the composite back to the original (full opacity 1.0f) 
            g2d.setComposite(originalComposite);
            
        }
        }
    }
    // Called by Member A's Logic Thread (MainThread.java)
    public void repaintGame() {
        this.repaint();
    }

    // --- Mouse Inputs (For Future Tower Placement) ---
    @Override
    public void mouseClicked(MouseEvent e) {

        producerExecutor.submit(() -> {
                Map<String, String> spawnAttrs = new HashMap<>();
                spawnAttrs.put("x", String.valueOf(e.getX())); // Coordinates
                spawnAttrs.put("y", String.valueOf(e.getY()));
                try{
                    commandBuffer.issueCommand(new Command("GameManager","SpawnBarbarian",spawnAttrs));
                }catch (InterruptedException el) {el.printStackTrace();}
        });
        
        // List<GameObject> objects = gameManager.getGameObjects();
        
        // GameObject closest = null;
        // double closestDist = Double.MAX_VALUE;

        // for(GameObject obj : objects){
        //         double dx = e.getX() - obj.getX();
        //         double dy = e.getY() - obj.getY();
        //         double dist = Math.sqrt(dx*dx + dy*dy);

        //         if(dist <= obj.getObjectSize()/2){
        //             if(dist < closestDist){
        //                 closestDist = dist;
        //                 closest = obj;
        //             }
        //         }
        // }
        // if(closest!=null){
        //     // GameObject clicked = closest;
        //     producerExecutor.submit(() -> {
                
        //         // try {
        //         //     commandBuffer.issueCommand(new Command(clicked.getObjectName(),"Attack", null));
        //         //     System.out.println("Command issued by: " + clicked.getObjectName());
        //         // } catch (InterruptedException e1) { e1.printStackTrace(); }
        //     });
        // }else{
        //     producerExecutor.submit(() -> {
        //         Map<String, String> spawnAttrs = new HashMap<>();
        //         spawnAttrs.put("x", String.valueOf(e.getX())); // Coordinates
        //         spawnAttrs.put("y", String.valueOf(e.getY()));
        //         try{
        //             commandBuffer.issueCommand(new Command("GameManager","SpawnBarbarian",spawnAttrs));
        //         }catch (InterruptedException el) {el.printStackTrace();}
        //     });

        // }
           
        System.out.println("Clicked at: " + e.getX() + ", " + e.getY());
        

    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}