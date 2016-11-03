package net.coderodde.brainfuckide;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
public class App 
        extends Application 
        implements CharListener, CharacterInputRequestListener {
   
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
    private EventHandler<ActionEvent> inputFieldHandler;
    private BrainfuckVM vm;
    private BlinkThread blinkThread;
    
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
        setRunButtonListener();
        setInputFieldHandler();
        
        inputField.setEditable(false);
        
        StackPane root = new StackPane();
        root.getChildren().add(mainBox);        
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    @Override
    public void acceptChar(char c) {
        outputArea.setText(outputArea.getText() + c);
    }
    
    /**
     * Launches the application.
     * 
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
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
    
    private void setRunButtonListener() {
        runButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    vm = new BrainfuckVM(codeArea.getText(),
                                         App.this, 
                                         App.this);
                    vm.execute();
                } catch (Exception ex) {
                    showExceptionDialog(ex);
                }
            }
        });
    }
    
    private void showExceptionDialog(Exception ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }
    
    private void setInputFieldHandler() {
        inputField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Consume the first character in the input field
                String text = inputField.getText();
                
                if (text.isEmpty()) {
                    return;
                }
                
                // Remove the first character of the text:
                char c = text.charAt(0);
                text = text.substring(1);
                inputField.setText(text);
                stopWaitingForCharacterInput();
            }
        });
    }

    @Override
    public void startWaitingForCharacterInput() {
        startInputPromptBlinking();
        inputField.setEditable(true);
    }
    
    @Override
    public void stopWaitingForCharacterInput() {
        inputField.setEditable(false);
        stopInputPromptBlinking();
    }
}
