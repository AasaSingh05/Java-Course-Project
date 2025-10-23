package gui.components;

import javafx.scene.control.Alert;

public class Snackbar {
    public static void show(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStyleClass().add("snackbar");
        alert.showAndWait();
    }
}
