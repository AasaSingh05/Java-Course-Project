package gui.components;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Passenger;

import java.util.List;

/**
 * Passenger card-style list (no balance shown).
 */
public class PassengerListView {
    private final ListView<Passenger> list = new ListView<>();

    public PassengerListView() {
        list.getStyleClass().add("carded-list");
        list.setFixedCellSize(64);
        list.setCellFactory(v -> new PassengerRowCell());

        list.prefHeightProperty().bind(
                Bindings.size(list.getItems())
                        .multiply(list.getFixedCellSize())
                        .add(12)
        );
    }

    public ListView<Passenger> getView() { return list; }

    public void setItems(List<Passenger> passengers) {
        list.getItems().setAll(passengers);
    }

    private static class PassengerRowCell extends ListCell<Passenger> {
        private final HBox root = new HBox(14);
        private final StackPane avatar = new StackPane();
        private final VBox centerBox = new VBox(2);
        private final Label title = new Label();
        private final Label subtitle = new Label();

        public PassengerRowCell() {
            avatar.setMinSize(36, 36);
            avatar.setMaxSize(36, 36);
            avatar.setStyle("-fx-background-color: #223049; -fx-background-radius: 18;");

            title.getStyleClass().add("passenger-title");
            subtitle.getStyleClass().add("passenger-subtitle");

            centerBox.getChildren().addAll(title, subtitle);

            root.getChildren().addAll(avatar, centerBox);
            root.setAlignment(Pos.CENTER_LEFT);
            root.getStyleClass().add("passenger-row");
            root.setPadding(new Insets(10));
        }

        @Override
        protected void updateItem(Passenger p, boolean empty) {
            super.updateItem(p, empty);
            if (empty || p == null) {
                setGraphic(null);
                setText(null);
                return;
            }
            title.setText(p.getName() + " (ID " + p.getPassengerId() + ")");
            subtitle.setText("Passenger"); // no balance shown
            setGraphic(root);
            setText(null);
        }
    }
}
