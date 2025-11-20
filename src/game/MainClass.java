package game;

public class MainClass {
    public static void main(String[] args) {
        MainThread MT = new MainThread();
        Thread t1 = new Thread(MT); // Game Logic Thread
        t1.start();

    }
}
