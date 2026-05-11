package Interfaz;

import Modelo.Reserva;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.logging.Logger;

public class ReservasView extends VBox {
	
	private static final Logger log = Logger.getLogger(ReservasView.class.getName());

    private FlowPane flowPendientes = new FlowPane();
    private FlowPane flowConfirmadas = new FlowPane();

    public ReservasView() {

        setSpacing(20);
        setPadding(new Insets(20));

        flowPendientes.setHgap(20);
        flowPendientes.setVgap(20);

        flowConfirmadas.setHgap(20);
        flowConfirmadas.setVgap(20);

        VBox pendientesBox = new VBox(10,
                new Label("RESERVAS PENDIENTES"),
                flowPendientes
        );

        VBox confirmadasBox = new VBox(10,
                new Label("RESERVAS CONFIRMADAS"),
                flowConfirmadas
        );

        getChildren().addAll(pendientesBox, confirmadasBox);
    }

    public void cargarReservas(List<Reserva> reservas) {

        flowPendientes.getChildren().clear();
        flowConfirmadas.getChildren().clear();

        for (Reserva r : reservas) {
        	
        	log.info("Procesando reserva con ID: " + r.getId() + " y estado: " + r.getEstado());

            ReservaCard card = new ReservaCard(r);

            // lógica según estado de la reserva
            if (r.getEstado().equalsIgnoreCase("PENDIENTE")) {
                flowPendientes.getChildren().add(card);
            } else if (r.getEstado().equalsIgnoreCase("CONFIRMADA")) {
                flowConfirmadas.getChildren().add(card);
            } else {
            	
            }
        }
    }
}
