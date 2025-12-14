package game;

import game.model.Barbarian;
import game.model.Entity;
import game.model.GameObject;
import game.model.Player;
import game.model.Tower;
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
        

        // Create Player and register
        Player mainPlayer = new Player(250f, 350f, 500, 0f, "PlayerMain");
        mainPlayer.addGold(300); // starting gold for testing
        mainGame.setPlayer(mainPlayer);
        mainGame.spawnObject(mainPlayer);



        // 2. Spawn a Test Object (Barbarian at 50,200 with 100HP and speed 100)
        GameObject barbarian = new Barbarian(90f, 100f, 100, 50f, null);
        GameObject tower = new Tower(90f, 350f, 200, 50f);
        Tower tower2 = new Tower(245f, 650f, 200, 50f);
        mainGame.spawnObject(barbarian);
        mainGame.spawnObject(tower);
        mainGame.spawnObject(tower2);
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