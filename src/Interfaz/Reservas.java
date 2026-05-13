package Interfaz;

import java.util.List;
import java.util.logging.Logger;

import Controller.ApiClient;
import Controller.ApiResult;
import Modelo.Reserva;
import Modelo.SesionAdmin;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Reservas extends BorderPane {

	private static final Logger log = Logger.getLogger(Reservas.class.getName());

	Header header;
	private javafx.scene.control.Label lblMensaje;
	VBox contenedor;

	private ReservasView reservasView;

	public Reservas() {

		// Header
		header = new Header();
		header.btnReservas.setOnAction(null);
		header.btnReservas.setStyle(
			    "-fx-background-color: #31c533;" +
			    "-fx-text-fill: white;"
			);
		setTop(header);

		// Vista reservas
		reservasView = new ReservasView();
		lblMensaje = new javafx.scene.control.Label("");
		lblMensaje.setStyle("-fx-text-fill: #b00020;");

		// Contenedor
		contenedor = new VBox(10);
		contenedor.setAlignment(Pos.TOP_RIGHT);
		contenedor.setPadding(new Insets(20));
		contenedor.getChildren().addAll(lblMensaje, reservasView);

		setCenter(contenedor);

		log.info("Mostrando reservas del administrador con ID: " + SesionAdmin.getIdActual());

		// Cargar datos del backend
		cargarReservas();
	}

	private void cargarReservas() {

		log.info("Iniciando carga de reservas para el administrador con ID: " + SesionAdmin.getIdActual());
		
		AsyncExecutor.io().submit(() -> {
			ApiResult<List<Reserva>> resultado = ApiClient.obtenerReservasPorAdmin(SesionAdmin.getIdActual());
			List<Reserva> reservas = resultado.isOk() && resultado.getData() != null ? resultado.getData() : List.of();
			if (!resultado.isOk()) {
				log.warning("Error cargando reservas: " + resultado.getTechnicalMessage());
			}
		    log.info("Reservas recibidas del backend para el administrador con ID: " + SesionAdmin.getIdActual() + ". Cantidad: " + reservas.size());
		    Platform.runLater(() -> {
		        log.info("Entrando en runLater");
		        reservasView.cargarReservas(reservas);
		        lblMensaje.setText(resultado.isOk() ? "" : "No se pudieron cargar todas las reservas.");
		        log.info("Reservas cargadas en la vista para el administrador con ID: " + SesionAdmin.getIdActual());
		    });
		});
	}
}
