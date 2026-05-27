public class SimpleThreads {

    // Display a message, preceded by the name of the current thread
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

	private static class CPUIntensive implements Runnable {
        public void run() {
            threadMessage("Calculando seno e consseno");
            double senTimesCosseno = 0.0;
            try {
                for (long i = 0; i < Long.MAX_VALUE; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    senTimesCosseno += Math.sin(i) * Math.cos(i);
                }
                threadMessage("Resultado: " + senTimesCosseno);
                
            } catch (InterruptedException e) {
                threadMessage("Interupção");
            }
        }
    }
	
	
    private static class MessageLoop
        implements Runnable {
        public void run() {
            String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
            };
            try {
                for (int i = 0; i < importantInfo.length; i++) {
                    // Pause for 4 seconds
                    Thread.sleep(4000);
                    // Print a message
                    threadMessage(importantInfo[i]);
                }
            } catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

	

    public static void main(String args[]) throws InterruptedException {

        // Delay, em milissegundos, antes de interromper as threads.
        long patience = 1000 * 6; 

        // If command line argument present, gives patience in seconds
        if (args.length > 0) {
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }

        threadMessage("Starting MessageLoop thread");
        long startTime = System.currentTimeMillis();
        
        Thread t = new Thread(new MessageLoop(), "Thread-Mensagem");
        Thread cpuThread = new Thread(new CPUIntensive(), "Thread-CPU-Intensa");

        t.start();
        cpuThread.start();

        threadMessage("Waiting for threads to finish");
        
        while (t.isAlive() || cpuThread.isAlive()) {
            threadMessage("Still waiting...");
            
            if (t.isAlive()) t.join(1000);
            if (cpuThread.isAlive()) cpuThread.join(1000);
            
            if ((System.currentTimeMillis() - startTime) > patience) {
                
                if (t.isAlive() || cpuThread.isAlive()) {
                    threadMessage("Tired of waiting!");
                }
                
                if (t.isAlive()) {
                    t.interrupt();
                    t.join();
                }
            
                if (cpuThread.isAlive()) {
                    cpuThread.interrupt();
                    cpuThread.join();
                }
            }
        }
        threadMessage("Finally!");
    }
}

