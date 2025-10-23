package gui.components;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Booking form that collects name and ticket count, and displays a computed
 * total price (read-only) based on the selected train's unit price.
 */
public class BookingForm {

    @FunctionalInterface
    public interface SubmitHandler {
        void handle(String name, int seats);
    }

    private final GridPane grid = new GridPane();
    private final TextField nameField = new TextField();
    private final TextField seatsField = new TextField();
    private final Label totalLabel = new Label("Total Price");
    private final Label totalValue = new Label("₹0.00");
    private final Button bookButton = new Button("Book Ticket");

    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final IntegerProperty seatsProperty = new SimpleIntegerProperty(0);
    private double unitPrice = 0.0;

    public BookingForm() {
        grid.getStyleClass().add("form-grid");
        grid.setHgap(12);
        grid.setVgap(10);

        nameField.setPromptText("Passenger name");
        seatsField.setPromptText("Seats to Book");

        // Bold total price
        totalValue.setStyle("-fx-font-weight: 800;");

        bookButton.getStyleClass().add("accent-button");
        bookButton.setDefaultButton(true);

        // Parse seats live into seatsProperty (empty/invalid -> 0)
        seatsProperty.bind(Bindings.createIntegerBinding(() -> {
            String s = seatsField.getText();
            if (s == null || s.isBlank()) return 0;
            try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return 0; }
        }, seatsField.textProperty()));

        // Initial total
        updateTotal();

        // Layout: Name, Tickets, Total (read-only), Action
        grid.addRow(0, new Label("Name"), nameField);
        grid.addRow(1, new Label("Tickets"), seatsField);
        grid.addRow(2, totalLabel, totalValue);

        HBox actions = new HBox(bookButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(0, 0, 0, 0));
        grid.add(actions, 1, 3);
    }

    public GridPane getGrid() {
        return grid;
    }

    // Wire the submit callback
    public void onSubmit(SubmitHandler handler) {
        bookButton.setOnAction(e -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            int seats = Math.max(0, seatsProperty.get());
            handler.handle(name, seats);
        });
    }

    // Called by parent when selected train changes to reflect its price per seat
    public void setUnitPrice(double pricePerSeat) {
        this.unitPrice = Math.max(0.0, pricePerSeat);
        updateTotal();
    }

    // Force recompute (e.g., after clearing fields or reselecting train)
    public void refreshTotal() {
        updateTotal();
    }

    public void clear() {
        nameField.clear();
        seatsField.clear();
        updateTotal();
    }

    // Expose seats text for external listeners (so parent can update totals in real time)
    public StringProperty seatsTextProperty() {
        return seatsField.textProperty();
    }

    // Utility to refresh the total label
    private void updateTotal() {
        int seats = Math.max(0, seatsProperty.get());
        double total = seats * unitPrice;
        totalValue.setText(currencyFmt.format(total));
    }
}
