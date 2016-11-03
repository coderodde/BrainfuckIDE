package net.coderodde.brainfuckide;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
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
    
    private final Button runButton       = new Button("Run");
    private final TextArea codeArea      = new TextArea();
    private final TextArea outputArea    = new TextArea();
    private final Label inputPromptLabel = new Label(">>>");
    private final TextField inputField   = new TextField();
    
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
        
        codeArea.setMaxHeight(PREFERRED_TEXTAREA_HEIGHT);
        codeArea.setPrefHeight(PREFERRED_TEXTAREA_HEIGHT);
        
        outputArea.setMaxHeight(PREFERRED_TEXTAREA_HEIGHT);
        outputArea.setPrefHeight(PREFERRED_TEXTAREA_HEIGHT);
        
        // Makes sure the width of the input field occupies all available width:
        HBox.setHgrow(inputField, Priority.ALWAYS);
        
        codeArea.wrapTextProperty().set(true);
        outputArea.wrapTextProperty().set(true);
        inputPromptLabel.setTranslateY(5.0);
        
        Font monospacedFont = Font.font("Monospaced", FontWeight.BOLD, 15.0);
        inputPromptLabel.setFont(monospacedFont);
        inputField.setFont(monospacedFont);
        runButton.setFont(monospacedFont);
        codeArea.setFont(monospacedFont);
        outputArea.setFont(monospacedFont);
        
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        
        StackPane root = new StackPane();
        root.getChildren().add(mainBox);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Brainfuck IDE");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches the application.
     * 
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }    
}
