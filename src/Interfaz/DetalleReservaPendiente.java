package Interfaz;

import java.util.logging.Logger;

import Controller.ApiClient;
import Modelo.Reserva;
import javafx.scene.control.Button;

public class DetalleReservaPendiente extends DetalleReserva {
	
	private static Logger log = Logger.getLogger(DetalleReserva.class.getName());

	public DetalleReservaPendiente(Reserva reserva) {
		super(reserva);
		
		Button btnConfirmar = new Button("Confirmar Reserva");
		btnConfirmar.setOnAction(e -> {
			// Lógica para confirmar la reserva
			log.info("Confirmando reserva con ID: " + reserva.getId());
			ApiClient.confirmarReserva((long)reserva.getId());
			log.info("Reserva confirmada con ID: " + reserva.getId());
			SceneManager.mostrarReservas();
		});
		
		botonesBox.getChildren().add(btnConfirmar);
	}

}
