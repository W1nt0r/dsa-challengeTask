package Presentation;

import DomainObjects.Contact;
import DomainObjects.Group;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GroupCell extends VBox {

    @FXML
    private Label groupName;

    @FXML
    private Label members;

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
        List<String> memberNames = new ArrayList<>();
        group.getMembers().forEach(x -> memberNames.add(x.getName()));
        String memberString = String.join(", ", memberNames);
        groupName.setText(group.getName());
        members.setText(memberString);
    }
}
