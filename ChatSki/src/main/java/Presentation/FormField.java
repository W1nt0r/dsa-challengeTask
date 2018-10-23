package Presentation;

import Presentation.Interfaces.FormFieldListener;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.function.Predicate;

public class FormField {
    private String label;
    private TextField textField;
    private BorderPane pane;
    private Label errorText;
    private Predicate<String> valueCheck;

    public FormField(String label, FormFieldListener listener) {
        this(label, "", listener);
    }

    public FormField(String label, String text, FormFieldListener listener) {
        this(label, text, value -> true, "", listener);
    }

    public FormField(String label, Predicate<String> valueCheck,
                     String errorMessage, FormFieldListener listener) {
        this(label, "", valueCheck, errorMessage, listener);
    }

    public FormField(String label, String text,
                     Predicate<String> valueCheck,
                     String errorMessage, FormFieldListener listener) {
        this.label = label;
        this.valueCheck = valueCheck;
        textField = new TextField(text);
        textField.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                listener.enterKeyPress();
            } else if (e.getCode().equals(KeyCode.ESCAPE)) {
                listener.escKeyPress();
            }
        });
        errorText = new Label(errorMessage);
        errorText.setVisible(false);
        pane = new BorderPane();
        pane.setBottom(errorText);
        pane.setCenter(textField);
    }

    public String getLabel() {
        return label;
    }

    public BorderPane getField() {
        return pane;
    }

    public void setFont(Font font) {
        textField.setFont(font);
    }

    public void setErrorFont(Font font) {
        errorText.setFont(font);
    }

    public void setErrorColor(String color) {
        errorText.setTextFill(Paint.valueOf(color));
    }

    public void setWidth(double width) {
        textField.setPrefWidth(width);
    }

    public String getText() {
        return textField.getText();
    }

    public boolean performCheck() {
        boolean result = valueCheck.test(textField.getText());
        if (!result) {
            errorText.setVisible(true);
        }
        return result;
    }
}
