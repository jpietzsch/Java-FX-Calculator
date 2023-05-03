import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class calculator extends Application {
    private TextField display;
    private Button[] numberButtons;
    private Button[] operatorButtons;
    private Button equalsButton;
    private Button clearButton;
    private Button backspaceButton;
    private static final String[] OPERATORS = { "+", "-", "*", "/" };

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        display = createDisplay();
        numberButtons = createNumberButtons();
        operatorButtons = createOperatorButtons();
        equalsButton = createEqualsButton();
        clearButton = createClearButton();
        Button backspaceButton = createBackspaceButton();


        GridPane grid = createGridPane();
        grid.add(display, 0, 0, 4, 1);

        int row = 1;
        int col = 0;
        for (int i = 1; i < numberButtons.length; i++) {
            grid.add(numberButtons[i], col, row);
            col++;
            if (col % 3 == 0) {
                col = 0;
                row++;
            }
        }

        grid.add(numberButtons[0], 1, 4);
        grid.add(clearButton, 0, 4);
        grid.add(backspaceButton, 0, 0);
        grid.add(equalsButton, 2, 4);

        for (int i = 0; i < operatorButtons.length; i++) {
            grid.add(operatorButtons[i], 3, i + 1);
        }

        Scene scene = new Scene(grid, Color.BLACK);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
        primaryStage.setTitle("JavaFX Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField createDisplay() {
        TextField display = new TextField();
        display.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        display.setEditable(false);
        display.setAlignment(Pos.CENTER_RIGHT);
        display.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); 
        return display;
    }
    

    private Button[] createNumberButtons() {
        Button[] buttons = new Button[10];
        for (int i = 0; i < buttons.length; i++) {
            Button button = new Button(String.valueOf(i));
            button.setStyle("-fx-base: black; -fx-text-fill: white;");
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); 
            int number = i;
            button.setOnAction(event -> display.appendText(String.valueOf(number)));
            buttons[i] = button;
        }
        return buttons;
    }
    
    private Button[] createOperatorButtons() {
        Button[] buttons = new Button[OPERATORS.length];
        for (int i = 0; i < buttons.length; i++) {
            Button button = new Button(OPERATORS[i]);
            button.setStyle("-fx-base: black; -fx-text-fill: white;");
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); 
            String operator = OPERATORS[i]; 
            button.setOnAction(event -> display.appendText(operator));
            buttons[i] = button;
        }
        return buttons;
    }

    private Button createEqualsButton() {
        Button button = new Button("=");
        button.setStyle("-fx-base: black; -fx-text-fill: white;");
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); 
        button.setOnAction(event -> calculateResult());
        return button;
    }
    
    private Button createClearButton() {
        Button button = new Button("C");
        button.setStyle("-fx-base: black; -fx-text-fill: white;");
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); 
        button.setOnAction(event -> display.clear());
        return button;
    }

    private Button createBackspaceButton() {
        Button button = new Button("<");
        button.setStyle("-fx-base: black; -fx-text-fill: white;");
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setOnAction(event -> deleteLastCharacter());
        return button;
    }
    

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);


        grid.setPrefSize(600, 600);

   
        for (int i = 0; i < 4; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(25);
            grid.getColumnConstraints().add(column);
        }

        for (int i = 0; i < 5; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(20);
            grid.getRowConstraints().add(row);
        }

        return grid;
    }

    private void calculateResult() {
        try {
            String input = display.getText();
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            Object result = engine.eval(input);
            String resultString = result.toString();
            display.setText(resultString);
            saveResultToHistoryFile(resultString, input);
        } catch (ScriptException e) {
            display.setText("Error");
        }
    }

    private void deleteLastCharacter() {
        String currentText = display.getText();
        if (!currentText.isEmpty()) {
            display.setText(currentText.substring(0, currentText.length() - 1));
        }
    }    

    private void handleKeyPress(KeyEvent event) {
        Button targetButton = null;
        char key = event.getText().charAt(0);
        if (Character.isDigit(key)) {
            targetButton = numberButtons[Character.getNumericValue(key)];
        } else if (event.getCode().toString().equals("ENTER")) {
            targetButton = equalsButton;
        } else if (event.getCode().toString().equals("SPACE") || event.getCode().toString().equals("DELETE")) {
            targetButton = clearButton;
        } else if (event.getCode().toString().equals("BACK_SPACE")) {
            targetButton = backspaceButton;
        } else {
            for (Button button : operatorButtons) {
                if (button.getText().charAt(0) == key) {
                    targetButton = button;
                    break;
                }
            }
        }
        if (targetButton != null) {
            targetButton.fire();
            event.consume();
        }
    }
    

    private void saveResultToHistoryFile(String result, String input) {
        Path historyFilePath = Paths.get("history.txt");
    
        try {
            if (!Files.exists(historyFilePath)) {
                Files.createFile(historyFilePath);
            }
    
            try (BufferedWriter writer = Files.newBufferedWriter(historyFilePath, StandardOpenOption.APPEND)) {
                writer.write(input + " = ");
                writer.write(result);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing in history file: " + e.getMessage());
        }
    }
    
}
