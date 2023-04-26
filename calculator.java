import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class calculator extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Hello World!");
        StackPane root = new StackPane();
        root.getChildren().add(label);

        Scene scene = new Scene(root, 600, 600);

        primaryStage.setTitle("Hello World Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
