package game.model;

public class User {
    private int gold = 0;
    private int score =0;
     public synchronized int getGold(){
        return gold;
    }

    public synchronized void addGold(int amount){
        this.gold += amount;
    }

    public synchronized boolean spendGold(int amount){
        if (amount <= 0){
            return false;
        }
        if (this.gold >= amount){
            this.gold -= amount;
            return true;
        }
        return false;
    }

    public synchronized int getScore(){
        return score;
    }

    public synchronized void addScore(int amount){
        this.score += amount;
    }
}
