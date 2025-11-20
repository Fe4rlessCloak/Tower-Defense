package game;

public class MainThread implements Runnable{
    private static final int TARGET_UPS = 120; // Updates Per Second
    private static final long TIME_PER_UPDATE = (1000000000)/TARGET_UPS; // Nanoseconds per update

    private long lastTime = System.nanoTime(); // Time when the last loop started
    private float lag = 0; // Accumulates lag
    
    private long currentTime = 0; // Stores the current time in nanoseconds
    private long deltaTime = 0; // Delta bw currentTime and lastTime

    private long second = 0; // Maintains number of seconds for testing
    private long timer = System.currentTimeMillis(); // Real world timer. Used to determine if a second has passed
    private int updateCounter = 0; // How many times the loop was called in the last second
    
    @Override
    public void run() {

        while(true){
            currentTime = System.nanoTime();
            deltaTime = currentTime - lastTime;
            lastTime = currentTime; 
            lag += deltaTime;
            while(lag>=TIME_PER_UPDATE){
                lag = lag - TIME_PER_UPDATE;
                updateCounter++;

            }
            if(System.currentTimeMillis() - timer >= 1000){
                System.out.println("Second: " + second + "\t" + "FPS: " + updateCounter);
                updateCounter = 0;
                second++;
                timer = System.currentTimeMillis();
            }
        }
        
    }
}
