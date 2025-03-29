import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PrintTask printTask = new PrintTask();

        
        Thread printThread = new Thread(printTask);
        printThread.start();

     
        while (true) {
            System.out.print("Enter text (or 'end' to exit): ");
            String input = scanner.nextLine();

            printTask.updateMessage(input);

            if (input.equalsIgnoreCase("end")) {
                
                printTask.stop();
                break;
            }
        }

    
        try {
            printThread.join();
        } catch (InterruptedException e) {
            System.out.println("The main thread was interrupted.");
        }

        System.out.println("Program terminated.");
    }
}


class PrintTask implements Runnable {
    private volatile String message = "";
    private final AtomicBoolean running = new AtomicBoolean(true);

    public void updateMessage(String newMessage) {
        synchronized (this) {
            this.message = newMessage;
            this.notify(); 
        }
    }

    public void stop() {
        running.set(false); 
    }

    @Override
    public void run() {
        while (running.get()) {
            synchronized (this) {
                try {
                    while (message.isEmpty()) {
                        
                        wait();
                    }

                    if (!message.equalsIgnoreCase("end")) {
                        System.out.println("Received message: " + message);
                    }
                    message = ""; 

                } catch (InterruptedException e) {
                    System.out.println("The print thread was interrupted.");
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
