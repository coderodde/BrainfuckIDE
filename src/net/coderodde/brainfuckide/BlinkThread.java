package net.coderodde.brainfuckide;

import javafx.scene.control.Label;

final class BlinkThread extends Thread {

    // 500 milliseconds:
    private static final int SLEEP_DURATION = 500;
    
    private final Label label;
    private volatile boolean exitRequested;
    
    BlinkThread(Label label) {
        this.label = label;
    }
    
    void requestTermination() {
        exitRequested = true;
    }
    
    public void run() {
        while (!exitRequested) {
            label.setVisible(true);
            sleep(SLEEP_DURATION);
            label.setVisible(false);
            sleep(SLEEP_DURATION);
        }
    }
    
    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            
        }
    }
}
