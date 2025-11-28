package game;

import game.model.Barbarian;
import game.model.GameObject;
import game.ui.GameFrame;
import game.ui.GamePanel;
import game.utils.Assets;
import game.utils.CommandBuffer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

public class MainClass {
    public static void main(String[] args) {
        CommandBuffer commandBuffer = new CommandBuffer();


        // Make the executor services (thread managers) for producers and consumers
        ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
        



        // 1. Create Logic Manager
        Assets mainAssets = new Assets();
        GameManager mainGame = new GameManager(mainAssets,commandBuffer);
        
        // 2. Spawn a Test Object (Barbarian at 50,200 with 100HP and speed 100)
        GameObject barbarian = new Barbarian(50f, 200f, 100, 50f, null);
        GameObject barbarian2 = new Barbarian(100, 230f, 100, 50f, null);
        mainGame.spawnObject(barbarian);
        mainGame.spawnObject(barbarian2);
        
        // 3. Start GUI on Event Dispatch Thread
         
        SwingUtilities.invokeLater(() -> {
            // Create Window
            GameFrame frame = new GameFrame(mainGame,producerExecutor,commandBuffer);
            
            // Get Panel to pass to Thread
            GamePanel panel = frame.getGamePanel();

            

            // 4. Start Game Loop Thread
            MainThread gameLoop = new MainThread(mainGame, panel);
            Thread t1 = new Thread(gameLoop);
            t1.start();
        });
    }
}