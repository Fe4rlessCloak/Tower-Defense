package game.ui;

import game.GameManager;
import game.utils.CommandBuffer;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.util.concurrent.ExecutorService;

public class GameFrame extends JFrame {
    
    private GamePanel gamePanel;
    private ControlPanel controlPanel;
        private ExecutorService producerExecutor;
        private CommandBuffer commandBuffer;
    public GameFrame(GameManager manager, ExecutorService producerExecutor,CommandBuffer commandBuffer ) {
        this.producerExecutor = producerExecutor;
        this.commandBuffer = commandBuffer;
        this.setTitle("Tower Defense - Phase 1");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(false);

        // Create Panels
        gamePanel = new GamePanel(manager,producerExecutor,commandBuffer);
        controlPanel = new ControlPanel();

        // Add Panels to Window
        this.add(gamePanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.EAST);

        this.pack(); // Fit window to panel sizes
        this.setLocationRelativeTo(null); // Center on screen
        this.setVisible(true);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}