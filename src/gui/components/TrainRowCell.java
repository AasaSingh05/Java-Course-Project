package gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import models.Train;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;

/**
 * Card-like cell for trains with logo, title, subtitle, and bold INR price.
 */
class TrainRowCell extends ListCell<Train> {
    private final HBox root = new HBox(14);
    private final ImageView logoView = new ImageView();
    private final VBox centerBox = new VBox(2);
    private final Label title = new Label();
    private final Label subtitle = new Label();
    private final Label price = new Label();
    private final Region spacer = new Region();

    private final NumberFormat currencyFmt;

    TrainRowCell(NumberFormat currencyFmt) {
        this.currencyFmt = currencyFmt;

        logoView.setFitWidth(42);
        logoView.setFitHeight(42);
        logoView.setPreserveRatio(true);
        logoView.setSmooth(true);

        title.getStyleClass().add("train-title");
        subtitle.getStyleClass().add("train-subtitle");
        centerBox.getChildren().addAll(title, subtitle);

        price.getStyleClass().add("train-price");
        HBox.setHgrow(spacer, Priority.ALWAYS);

        root.getChildren().addAll(logoView, centerBox, spacer, price);
        root.setAlignment(Pos.CENTER_LEFT);
        root.getStyleClass().add("train-row");
        root.setPadding(new Insets(10));
    }

    @Override
    protected void updateItem(Train train, boolean empty) {
        super.updateItem(train, empty);
        if (empty || train == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        logoView.setImage(loadLogo(train.getTrainId()));
        title.setText(train.getTrainName());
        subtitle.setText("Remaining: " + train.getAvailableSeats());
        price.setText(currencyFmt.format(train.getPricePerSeat()));

        setGraphic(root);
        setText(null);
    }

    private Image loadLogo(int trainId) {
        String specific = "resources/logos/" + trainId + ".png";
        String fallback = "resources/logos/train.png";
        try {
            return new Image(new FileInputStream(specific));
        } catch (FileNotFoundException e1) {
            try {
                return new Image(new FileInputStream(fallback));
            } catch (FileNotFoundException e2) {
                return null;
            }
        }
    }
}
