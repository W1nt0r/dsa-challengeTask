package Presentation;

import DomainObjects.Group;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

public class GroupCell extends VBox {

    @FXML
    private Label groupName;

    public GroupCell() {
        try {
            URL resource = getClass().getClassLoader().getResource("group_listitem.fxml");

            FXMLLoader loader = new FXMLLoader(resource);
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setGroup(Group group) {
        groupName.setText(group.getName());
    }
}
