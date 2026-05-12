package Interfaz;

import java.util.List;
import java.util.logging.Logger;

import Controller.ApiClient;
import Modelo.Reserva;
import Modelo.SesionAdmin;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Reservas extends BorderPane {

	private static final Logger log = Logger.getLogger(Reservas.class.getName());

	Header header;
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

		// Contenedor
		contenedor = new VBox(10);
		contenedor.setAlignment(Pos.TOP_RIGHT);
		contenedor.setPadding(new Insets(20));
		contenedor.getChildren().addAll(reservasView);

		setCenter(contenedor);

		log.info("Mostrando reservas del administrador con ID: " + SesionAdmin.getIdActual());

		// Cargar datos del backend
		cargarReservas();
	}

	private void cargarReservas() {

		log.info("Iniciando carga de reservas para el administrador con ID: " + SesionAdmin.getIdActual());
		
		new Thread(() -> {
		    List<Reserva> reservas = ApiClient.obtenerReservasPorAdmin(SesionAdmin.getIdActual());
		    log.info("Reservas recibidas del backend para el administrador con ID: " + SesionAdmin.getIdActual() + ". Cantidad: " + (reservas != null ? reservas.size() : "null"));
		    Platform.runLater(() -> {
		        log.info("Entrando en runLater");
		        reservasView.cargarReservas(reservas);
		        log.info("Reservas cargadas en la vista para el administrador con ID: " + SesionAdmin.getIdActual());
		    });
		}).start();
	}
}