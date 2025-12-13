package game.ui;

import game.GameManager;
import game.model.GameObject;
import game.model.Player;
import game.model.Enemy;
import game.utils.Assets;
import game.utils.Command;
import game.utils.CommandBuffer;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;

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
        
        for (GameObject obj : objects) {
            int frameW = obj.getObjectSize();
            int frameH = obj.getObjectSize();

            int drawX = (int)(obj.getX() - frameW / 2);
            int drawY = (int)(obj.getY() - frameH / 2);
            
            
            // Check if this object is a Barbarian to apply animation
            if (obj instanceof Enemy) {
                Enemy enemy = (Enemy) obj;
                
                // Get the specific frame for the current action (e.g., "runForward", frame 2)
                BufferedImage frame = assets.getFrame(enemy.getClassName(),
                    enemy.getCurrentAnimation(), 
                    enemy.getCurrentFrame()
                );

                if (frame != null) {
                    // Draw the sprite (Scaled to 64x64 or whatever size you prefer)
                    g.drawImage(frame, drawX, drawY, frameW, frameH, null); 
                } else {
                    // Fallback Red Box if sprite isn't found
                    g.setColor(Color.RED);
                    g.fillRect(drawX, drawY, 64, 64);
                }
            } 
            else if(obj instanceof Player){
                Player player = (Player) obj;
                // Default drawing for non-animated objects (e.g., Towers later)
                BufferedImage frame = assets.getFrame(player.getClassName(),
                    player.getCurrentAnimation(), 
                    player.getCurrentFrame()
                );
                if (frame != null) {
                    // Draw the sprite (Scaled to 64x64 or whatever size you prefer)
                    g.drawImage(frame, drawX, drawY, frameW, frameH, null); 
                } else {
                    // Fallback Red Box if sprite isn't found
                    g.setColor(Color.RED);
                    g.fillRect(drawX, drawY, 64, 64);
                }
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