package net.coderodde.brainfuckide;

import javafx.scene.control.Label;

final class BlinkThread extends Thread {

    // 500 milliseconds:
    private static final int SLEEP_COUNT = 5;
    private static final int SLEEP_DURATION = 100;

    private final Label label;
    private volatile boolean exitRequested;

    BlinkThread(Label label) {
        this.label = label;
    }

    void requestTermination() {
        exitRequested = true;
    }

    public void run() {
        outerLoop:
        while (true) {
            label.setVisible(true);
            
            for (int i = 0; i < SLEEP_COUNT; ++i) {
                sleep(SLEEP_DURATION);
                
                if(exitRequested) {
                    break outerLoop;
                }
            }
            
            label.setVisible(false);
            
            for (int i = 0; i < SLEEP_COUNT; ++i) {
                sleep(SLEEP_DURATION);
                
                if (exitRequested) {
                    break outerLoop;
                }
            }
        }

        // Keep the label visible:
        label.setVisible(true);
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {

        }
    }
}
