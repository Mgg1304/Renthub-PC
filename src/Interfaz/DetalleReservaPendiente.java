package Interfaz;

import java.util.logging.Logger;

import Controller.ApiClient;
import Controller.ApiResult;
import Modelo.Reserva;
import javafx.scene.control.Button;

public class DetalleReservaPendiente extends DetalleReserva {
	
	private static Logger log = Logger.getLogger(DetalleReserva.class.getName());

	public DetalleReservaPendiente(Reserva reserva) {
		super(reserva);
		
		Button btnConfirmar = new Button("Confirmar Reserva");
		btnConfirmar.setStyle(
			    "-fx-background-color: #31c533;" +
			    "-fx-text-fill: white;"
			);
		btnConfirmar.setOnAction(e -> {
			log.info("Confirmando reserva con ID: " + reserva.getId());
			ApiResult<Void> resultado = ApiClient.confirmarReserva((long) reserva.getId());
			if (resultado.isOk()) {
				log.info("Reserva confirmada con ID: " + reserva.getId());
				SceneManager.mostrarReservas();
			} else {
				log.warning("No se pudo confirmar la reserva " + reserva.getId() + ": " + resultado.getTechnicalMessage());
			}
		});
		
		botonesBox.getChildren().add(btnConfirmar);
	}

}
