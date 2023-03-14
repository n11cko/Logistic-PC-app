package utils;

import javafx.scene.Node;
import javafx.scene.control.*;

import java.util.Optional;

public class AlertDialog {
    public static void throwAlert(String pageName, String information) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(pageName);
        alert.setHeaderText(null);
        alert.setContentText(information);
        alert.showAndWait();
    }

    public static <T> void updateItem(ListView<T> listView, T selectedItem, String title, Node content, Runnable onSuccess) {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Dialog<T> dialog = new Dialog<>();
            dialog.setTitle(title);
            dialog.getDialogPane().setContent(content);
            ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);
            Optional<T> result = dialog.showAndWait();
            if (result.isPresent()) {
                onSuccess.run();
                listView.getItems().set(selectedIndex, selectedItem);
            }
        }
    }

}
