package Presentation;

import Presentation.Enums.FormType;
import Presentation.Interfaces.FormFieldListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Form implements FormFieldListener {
    private static final Font TITLE_FONT = Font.font("Segoe UI",
            FontWeight.NORMAL, 18);
    private static final Font TEXT_FONT = Font.font("Segoe UI",
            FontWeight.NORMAL, 16);
    private static final Font ERROR_FONT = Font.font("Segoe UI",
            FontWeight.NORMAL, 14);
    private static final String ERROR_COLOR = "#FF0000";
    private static final int TEXT_FIELD_WIDTH = 300;
    private static final int FIELD_MARGIN = 20;
    private Stage form;
    private String message;
    private Map<String, FormField> fieldMap;
    private List<FormField> fieldList;
    private GridPane grid;
    private boolean canceled;
    private FormType type;

    public Form(String title, String message, FormType type) {
        form = new Stage();
        form.setTitle(title);
        form.setResizable(false);
        fieldMap = new HashMap<>();
        fieldList = new LinkedList<>();
        this.message = message;
        this.type = type;
    }

    public void addField(String key, String label) {
        FormField field = new FormField(label, this);
        addField(key, field);
    }

    public void addField(String key, String label, String text) {
        FormField field = new FormField(label, text, this);
        addField(key, field);
    }

    public void addField(String key, String label, String text,
                         Predicate<String> valueCheck,
                         String errorMessage) {
        FormField field = new FormField(label, text, valueCheck, errorMessage, this);
        addField(key, field);
    }

    public void addField(String key, String label,
                         Predicate<String> valueCheck,
                         String errorMessage) {
        FormField field = new FormField(label, valueCheck, errorMessage, this);
        addField(key, field);
    }

    public String getFieldText(String key) {
        FormField field = fieldMap.get(key);
        if (field == null) {
            return null;
        }
        return field.getText();
    }

    public boolean showAndWait() {
        canceled = true;
        if (grid == null) {
            buildFormGrid();
        }
        form.showAndWait();
        return !canceled;
    }

    private void buildFormGrid() {
        initializeGrid();
        Label title = new Label(message);
        title.setFont(TITLE_FONT);
        title.setPadding(new Insets(0, 0, FIELD_MARGIN, 0));
        grid.add(title, 0, 0, 2, 1);
        addFieldsToGrid();
        int buttonRow = fieldList.size() + 1;
        switch (type) {
            case DECISION:
                initializeDecisionButtons(buttonRow);
                break;
            case SEND:
                initializeSendButton(buttonRow);
                break;
            case OK:
                initializeOkButton(buttonRow);
                break;
        }
    }

    private void addFieldsToGrid() {
        int fieldRow = 1;
        for (FormField field : fieldList) {
            Label label = new Label(field.getLabel());
            label.setFont(TEXT_FONT);
            label.setPadding(new Insets(6, 0, 0, 0));
            BorderPane labelPane = new BorderPane();
            labelPane.setTop(label);
            grid.add(labelPane, 0, fieldRow);
            field.setFont(TEXT_FONT);
            field.setErrorFont(ERROR_FONT);
            field.setErrorColor(ERROR_COLOR);
            field.setWidth(TEXT_FIELD_WIDTH);
            grid.add(field.getField(), 1, fieldRow);
            fieldRow++;
        }
    }

    private void initializeGrid() {
        grid = new GridPane();
        grid.setPadding(new Insets(25, 25, 25, 25));
        grid.setBackground(Background.EMPTY);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        Scene scene = new Scene(grid);
        form.setScene(scene);
    }

    private void initializeSendButton(int gridRow) {
        HBox container = initializeButtonContainer(gridRow);
        Button okButton = createFormButton("Send", e -> sendForm());
        container.getChildren().add(okButton);
    }

    private void initializeOkButton(int gridRow) {
        HBox container = initializeButtonContainer(gridRow);
        Button okButton = createFormButton("OK", e -> sendForm());
        container.getChildren().add(okButton);
    }

    private void initializeDecisionButtons(int gridRow) {
        HBox container = initializeButtonContainer(gridRow);
        Button acceptButton = createFormButton("Accept", e -> sendForm());
        Button rejectButton = createFormButton("Reject", e -> form.close());
        container.getChildren().add(acceptButton);
        container.getChildren().add(rejectButton);
    }

    private Button createFormButton(String text,
                                    EventHandler<ActionEvent> actionHanlder) {
        Button button = new Button(text);
        button.setOnAction(actionHanlder);
        button.setFont(TEXT_FONT);
        return button;
    }

    private HBox initializeButtonContainer(int gridRow) {
        HBox container = new HBox(10);
        if (gridRow > 1) {
            container.setPadding(new Insets(FIELD_MARGIN, 0, 0, 0));
        }
        container.setAlignment(Pos.BOTTOM_RIGHT);
        grid.add(container, 0, gridRow, 2, 1);
        return container;
    }

    private void sendForm() {
        boolean result = true;
        for (FormField field : fieldList) {
            if (!field.performCheck()) {
                result = false;
            }
        }
        if (result) {
            canceled = false;
            form.close();
        }
    }

    private void addField(String key, FormField field) {
        fieldMap.put(key, field);
        fieldList.add(field);
        grid = null;
    }

    @Override
    public void enterKeyPress() {
        sendForm();
    }

    @Override
    public void escKeyPress() {
        form.close();
    }
}
