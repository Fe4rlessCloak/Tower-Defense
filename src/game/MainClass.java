package game;

import game.model.Barbarian;
import game.model.GameObject;
import game.ui.GameFrame;
import game.ui.GamePanel;
import javax.swing.SwingUtilities;

public class MainClass {
    public static void main(String[] args) {

        // 1. Create Logic Manager
        GameManager mainGame = new GameManager();

        // 2. Spawn a Test Object (Barbarian at 50,200 with 100HP and speed 100)
        GameObject barbarian = new Barbarian(50f, 200f, 100, 50f);
        mainGame.spawnObject(barbarian);

        // 3. Start GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create Window
            GameFrame frame = new GameFrame(mainGame);
            
            // Get Panel to pass to Thread
            GamePanel panel = frame.getGamePanel();

            // 4. Start Game Loop Thread
            MainThread gameLoop = new MainThread(mainGame, panel);
            Thread t1 = new Thread(gameLoop);
            t1.start();
        });
    }
}