package game;

import game.ui.GamePanel;

public class MainThread implements Runnable {
    
    private GameManager mainGame;
    private GamePanel gamePanel; // Added reference to GUI

    // Constructor now accepts GamePanel
    public MainThread(GameManager mainGame, GamePanel gamePanel){
        this.mainGame = mainGame;
        this.gamePanel = gamePanel;
    }
    
    private static final int TARGET_UPS = 120;
    private static final long TIME_PER_UPDATE = 1000000000 / TARGET_UPS;
    private static final float TIME_PER_UPDATE_IN_NANOSECONDS = (float)TIME_PER_UPDATE / 1_000_000_000f;

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        float lag = 0;
        int updateCounter = 0;

        while(true) {
            long currentTime = System.nanoTime();
            long deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            lag += deltaTime;

            // 1. Update Game Logic (Fixed Time Step)
            while(lag >= TIME_PER_UPDATE) {
                mainGame.update(TIME_PER_UPDATE_IN_NANOSECONDS);
                lag -= TIME_PER_UPDATE;
                updateCounter++;
            }

            // 2. Render GUI (The Integration Point)
            if (gamePanel != null) {
                gamePanel.repaintGame();
            }

            // FPS Counter
            if(System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + updateCounter);
                updateCounter = 0;
                timer = System.currentTimeMillis();
            }
        }
    }
}