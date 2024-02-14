package duke;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * Controller for MainWindow. Provides the layout for the other controls.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Parser parser = new Parser();

    private Stage stage;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/wojak.jpeg"));
    private final Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/chad.jpeg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        greet();
    }

    public void setStage(Stage s) {
        stage = s;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.equals("bye")) {
            stage.close();
        }
        String response = parser.interpret(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        userInput.clear();


    }
    private void greet() {
        String greetings ="Hello! I'm Dukey." + "\n" + "What can I do for you?";
        dialogContainer.getChildren().addAll(
                DialogBox.getDukeDialog(greetings, dukeImage)
        );
        userInput.clear();

    }

}
