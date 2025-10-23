package gui.components;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import models.Train;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Wrapper for the trains ListView with a custom row cell and height binding
 * that fits exactly the number of items.
 */
public class TrainListView {
    private final ListView<Train> list = new ListView<>();

    public TrainListView() {
        list.getStyleClass().add("carded-list");
        list.setPrefWidth(560);

        // Fixed row height for consistent measurement and shadow layout
        list.setFixedCellSize(72);

        NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        list.setCellFactory(v -> new TrainRowCell(currencyFmt));

        // Fit height to contents: items * fixedCellSize + small padding
        list.prefHeightProperty().bind(
                Bindings.size(list.getItems())
                        .multiply(list.getFixedCellSize())
                        .add(12)
        );
    }

    public ListView<Train> getView() {
        return list;
    }

    public MultipleSelectionModel<Train> getSelectionModel() {
        return list.getSelectionModel();
    }

    public void setItems(List<Train> trains) {
        list.getItems().setAll(trains);
    }
}
