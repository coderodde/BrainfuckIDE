package net.coderodde.brainfuckide;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * This class is the actual Brainfuck IDE application.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Nov 3, 2016)
 */
public class App extends Application {
   
    private static final double PREFERRED_TEXTAREA_HEIGHT = 10000.0;
    private static final Font APP_FONT = Font.font("Monospaced",
                                                   FontWeight.BOLD,
                                                   15.0);
    private static final double INITIAL_WIDTH = 500.0;
    private static final double INITIAL_HEIGHT = 400.0;
    private static final String APP_TITLE = "Brainfuck IDE - 1.6";
    
    private final Button runButton       = new Button("Run");
    private final TextArea codeArea      = new TextArea();
    private final TextArea outputArea    = new TextArea();
    private final Label inputPromptLabel = new Label(">>>");
    private final TextField inputField   = new TextField();

    private BlinkThread blinkThread;
    
    private void setComponentDimensions() {
        codeArea.setMaxHeight(PREFERRED_TEXTAREA_HEIGHT);
        codeArea.setPrefHeight(PREFERRED_TEXTAREA_HEIGHT);
        
        outputArea.setMaxHeight(PREFERRED_TEXTAREA_HEIGHT);
        outputArea.setPrefHeight(PREFERRED_TEXTAREA_HEIGHT);
        
        // Makes sure the width of the input field occupies all available width:
        HBox.setHgrow(inputField, Priority.ALWAYS);
    }
    
    private void setTextWrapping() {
        codeArea.wrapTextProperty().set(true);
        outputArea.wrapTextProperty().set(true);
    }
    
    private void setMiscAttributes() {
        inputPromptLabel.setTranslateY(5.0);
    }
    
    @Override
    public void start(Stage primaryStage) {
        HBox controlLine = new HBox();
        controlLine.getChildren().addAll(inputPromptLabel,
                                         inputField,
                                         runButton);
        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(codeArea,
                                     controlLine,
                                     outputArea);
        
        setComponentDimensions();
        setTextWrapping();
        setMiscAttributes();
        setComponentDimensions();
        setComponentFonts();
        
        StackPane root = new StackPane();
        root.getChildren().add(mainBox);        
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        startInputPromptBlinking();
        
    }

    /**
     * Launches the application.
     * 
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        BrainfuckVM vm = new BrainfuckVM("++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.", System.out);
        System.out.println("YOOOO!!!");
        vm.execute();
        
        launch(args);
    }    
    
    private void setComponentFonts() {
        codeArea.setFont(APP_FONT);
        inputPromptLabel.setFont(APP_FONT);
        inputField.setFont(APP_FONT);
        runButton.setFont(APP_FONT);
        outputArea.setFont(APP_FONT);
    }
    
    private void startInputPromptBlinking() {
        if (blinkThread == null) {
            blinkThread = new BlinkThread(inputPromptLabel);
            blinkThread.start();
        }
    }
    
    private void stopInputPromptBlinking() {
        if (blinkThread != null) {
            blinkThread.requestTermination();
            blinkThread = null;
        }
    }
}
