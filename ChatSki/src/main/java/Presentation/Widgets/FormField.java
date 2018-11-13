package Presentation.Widgets;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.function.Predicate;

public class FormField extends StackPane {

    private static final String STYLE_CLASS_FOCUSED = "focused";
    private static final String STYLE_CLASS_ERROR = "error";

    @FXML
    private TextField textField;

    @FXML
    private Label label;

    @FXML
    private Label error;

    private Predicate<String> checkMethod;
    private Runnable enterAction;
    private Runnable escAction;

    public FormField(Predicate<String> checkMethod) {
        this.checkMethod = checkMethod;
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getClassLoader().getResource("form_field.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                textField.requestFocus();
            }
        });

        labelProperty().addListener((observable, oldValue, newValue)
                -> textField.setPromptText(newValue));

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                getStyleClass().add(STYLE_CLASS_FOCUSED);
            } else {
                getStyleClass().remove(STYLE_CLASS_FOCUSED);
            }
        });

        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE && escAction != null) {
                escAction.run();
            } else if (event.getCode() == KeyCode.ENTER && enterAction != null) {
                enterAction.run();
            }
        });
    }

    public boolean check() {
        if (!checkMethod.test(getText())) {
            getStyleClass().add(STYLE_CLASS_ERROR);
            return false;
        }
        return true;
    }

    public void setEnterAction(Runnable action) {
        enterAction = action;
    }

    public void setEscAction(Runnable action) {
        escAction = action;
    }

    public String getText() {
        return textProperty().getValue();
    }

    public void setText(String value) {
        textProperty().setValue(value);
    }

    public StringProperty textProperty() {
        return textField.textProperty();
    }

    public String getLabel() {
        return labelProperty().getValue();
    }

    public void setLabel(String value) {
        labelProperty().set(value);
    }

    public StringProperty labelProperty() {
        return label.textProperty();
    }

    public String getError() {
        return errorProperty().getValue();
    }

    public void setError(String value) {
        errorProperty().set(value);
    }

    public StringProperty errorProperty() {
        return error.textProperty();
    }
}
