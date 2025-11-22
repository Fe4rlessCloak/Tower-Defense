package game;
import game.model.Barbarian;
import game.model.GameObject;

public class MainClass {
    public static void main(String[] args) {

        GameManager mainGame = new GameManager();
        GameObject Barbarian = new Barbarian(0f,0f,100,2f);
        mainGame.spawnObject(Barbarian);
        MainThread MT = new MainThread(mainGame);
        Thread t1 = new Thread(MT); // Game Logic Thread    
        t1.start();

    }
}
