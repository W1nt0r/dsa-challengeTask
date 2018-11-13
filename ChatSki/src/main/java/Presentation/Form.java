package Presentation;

import Presentation.Enums.FormType;
import Presentation.Widgets.FormField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Form {
    private Stage form;
    private FormController controller;
    private Map<String, FormField> fieldMap;
    private boolean canceled;

    public Form(String title, String message, FormType type) {
        form = new Stage();
        form.setTitle(title);
        form.setResizable(false);
        loadForm();
        addMessage(message);
        setButtonActions();
        controller.showCorrectButtons(type);
        fieldMap = new HashMap<>();
    }

    private void setButtonActions() {
        controller.setOkButtonAction(this::sendForm);
        controller.setSendButtonAction(this::sendForm);
        controller.setAcceptButtonAction(this::sendForm);
        controller.setRejectButtonAction(form::close);
    }

    private void loadForm() {
        URL resource = getClass().getClassLoader().getResource("form_window.fxml");
        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            controller = loader.getController();
            Scene scene = new Scene(root);
            form.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("title");
        addChild(messageLabel);
    }

    public void setOwner(Scene owner) {
        form.initModality(Modality.WINDOW_MODAL);
        form.initOwner(owner.getWindow());
    }

    protected void addChild(Node child) {
        controller.getMainPane().getChildren().add(child);
    }

    public void addField(String key, String label) {
        addField(key, label, "");
    }

    public void addField(String key, String label, String text) {
        addField(key, label, text, v -> true, "");
    }

    public void addField(String key, String label,
                         Predicate<String> valueCheck,
                         String errorMessage) {
        addField(key, label, "", valueCheck, errorMessage);
    }

    public void addField(String key, String label, String text,
                         Predicate<String> valueCheck,
                         String errorMessage) {
        FormField field = new FormField(valueCheck);
        field.setLabel(label);
        field.setText(text);
        field.setError(errorMessage);
        field.setEnterAction(this::enterKeyPress);
        field.setEscAction(this::escKeyPress);
        fieldMap.put(key, field);
        addChild(field);
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
        form.showAndWait();
        return !canceled;
    }

    protected boolean checkForm() {
        for (FormField field : fieldMap.values()) {
            if (!field.check()) {
                return false;
            }
        }
        return true;
    }

    protected FormController getController() {
        return controller;
    }

    private void sendForm() {
        if (checkForm()) {
            canceled = false;
            form.close();
        }
    }

    protected void enterKeyPress() {
        sendForm();
    }

    protected void escKeyPress() {
        form.close();
    }
}
