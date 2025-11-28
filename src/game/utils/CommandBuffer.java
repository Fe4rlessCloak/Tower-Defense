package game.utils;



import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

public class CommandBuffer{
    private final ArrayBlockingQueue<Command> Buffer;
    
    public CommandBuffer(){
        this.Buffer = new ArrayBlockingQueue<Command>(1);
    }
    

    public void issueCommand(Command toIssue) throws InterruptedException{
        Buffer.put(toIssue);
    }
    public Command receiveCommand() throws InterruptedException{
        return(Buffer.poll());
    }


    public int drainTo(Collection<Command> list) {
        Buffer.drainTo(list); 
        return list.size();
    }
}
