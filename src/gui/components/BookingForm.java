package gui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class BookingForm {

    @FunctionalInterface
    public interface SubmitHandler {
        void handle(String name, double balance, int seats);
    }

    private final GridPane grid = new GridPane();
    private final TextField nameField = new TextField();
    private final TextField balanceField = new TextField();
    private final TextField seatsField = new TextField();
    private final Button bookButton = new Button("Book Ticket");

    public BookingForm() {
        grid.getStyleClass().add("form-grid");
        grid.setHgap(12);
        grid.setVgap(10);

        nameField.setPromptText("Passenger name");
        balanceField.setPromptText("Balance (e.g., 500.00)");
        seatsField.setPromptText("Seats to Book");

        bookButton.getStyleClass().add("accent-button");
        bookButton.setDefaultButton(true);

        grid.addRow(0, new Label("Name"), nameField);
        grid.addRow(1, new Label("Balance"), balanceField);
        grid.addRow(2, new Label("Tickets"), seatsField);

        HBox actions = new HBox(bookButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(0, 0, 0, 0));
        grid.add(actions, 1, 3);
    }

    public GridPane getGrid() {
        return grid;
    }

    public void onSubmit(SubmitHandler handler) {
        bookButton.setOnAction(e -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String bal = balanceField.getText() == null ? "" : balanceField.getText().trim();
            String seats = seatsField.getText() == null ? "" : seatsField.getText().trim();

            double balance;
            int seatCount;
            try {
                balance = Double.parseDouble(bal);
            } catch (Exception ex) {
                Snackbar.show("Balance must be a valid number.");
                return;
            }
            try {
                seatCount = Integer.parseInt(seats);
            } catch (Exception ex) {
                Snackbar.show("Tickets must be a whole number.");
                return;
            }
            handler.handle(name, balance, seatCount);
        });
    }

    public void clear() {
        nameField.clear();
        balanceField.clear();
        seatsField.clear();
    }
}
