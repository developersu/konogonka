package konogonka.ModelControllers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LogPrinter {
    private MessagesConsumer msgConsumer;
    private BlockingQueue<String> msgQueue;
    private BlockingQueue<Double> progressQueue;

    public LogPrinter(){
        this.msgQueue = new LinkedBlockingQueue<>();
        this.progressQueue = new LinkedBlockingQueue<>();
        this.msgConsumer = new MessagesConsumer(this.msgQueue, this.progressQueue);
        this.msgConsumer.start();
    }
    /**
     * This is what will print to textArea of the application.
     * */
    public void print(String message, EMsgType type){
        try {
            switch (type){
                case PASS:
                    msgQueue.put("[ PASS ] "+message+"\n");
                    break;
                case FAIL:
                    msgQueue.put("[ FAIL ] "+message+"\n");
                    break;
                case INFO:
                    msgQueue.put("[ INFO ] "+message+"\n");
                    break;
                case WARNING:
                    msgQueue.put("[ WARN ] "+message+"\n");
                    break;
                default:
                    msgQueue.put(message);
            }
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
    }
    /**
     * Update progress for progress bar
     * */
    public void updateProgress(Double value) throws InterruptedException{
        progressQueue.put(value);
    }
    /**
     * When we're done - close it
     * */
    public void close(){
        msgConsumer.interrupt();
    }
}
