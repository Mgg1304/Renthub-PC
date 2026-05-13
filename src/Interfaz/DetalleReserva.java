package Interfaz;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import Controller.ApiClient;
import Controller.ApiResult;
import Modelo.Producto;
import Modelo.Reserva;

public class DetalleReserva extends BorderPane {

	private static Logger log = Logger.getLogger(DetalleReserva.class.getName());

	private int indiceImagen = 0;
	private List<String> imagenes;
	
	protected HBox botonesBox;

	private ImageView imageView;
	
	public DetalleReserva() {
		
	}

	public DetalleReserva(Reserva reserva) {

		if (reserva == null) {
			log.warning("No se puede mostrar detalle de una reserva nula.");
			setCenter(new Label("Reserva no disponible"));
			return;
		}

		log.info("Mostrando detalle de la reserva con ID: " + reserva.getId());

		Producto producto = reserva.getProducto();
		if (producto == null) {
			imagenes = List.of();
		} else {
			ApiResult<List<String>> imagenesResult = ApiClient.obtenerUrlsImagenesPorProducto(producto.getId());
			if (imagenesResult.isOk() && imagenesResult.getData() != null) {
				imagenes = imagenesResult.getData();
			} else {
				imagenes = List.of();
				log.warning("No se pudieron cargar imagenes de la reserva " + reserva.getId() + ": "
						+ imagenesResult.getTechnicalMessage());
			}
		}

		imageView = new ImageView();
		imageView.setFitWidth(400);
		imageView.setFitHeight(300);
		imageView.setPreserveRatio(true);

		if (!imagenes.isEmpty()) {
			mostrarImagen();
		}

		Button btnPrev = new Button("<");
		btnPrev.setStyle(
			    "-fx-background-color: #1179ff;" +
			    "-fx-text-fill: white;"
			);
		Button btnNext = new Button(">");
		btnNext.setStyle(
			    "-fx-background-color: #31c533;" +
			    "-fx-text-fill: white;"
			);

		btnPrev.setOnAction(e -> {
			if (indiceImagen > 0) {
				indiceImagen--;
				mostrarImagen();
			}
		});

		btnNext.setOnAction(e -> {
			if (indiceImagen < imagenes.size() - 1) {
				indiceImagen++;
				mostrarImagen();
			}
		});

		StackPane carrusel = new StackPane(imageView, btnPrev, btnNext);
		StackPane.setAlignment(btnPrev, Pos.CENTER_LEFT);
		StackPane.setAlignment(btnNext, Pos.CENTER_RIGHT);

		VBox info = new VBox(10);

		// Nombre producto
		String nombreProducto = producto != null && producto.getNombre() != null ? producto.getNombre() : "Sin producto";
		Label nombre = new Label(nombreProducto);
		nombre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		// Estado reserva
		Label estado = new Label("Estado: " + reserva.getEstado());
		estado.setStyle("""
				-fx-font-size: 14px;
				-fx-font-weight: bold;
				-fx-text-fill: #2196F3;
				""");

		// Precio
		String precioTexto = producto != null ? producto.getPrecioPorDia() + " €/día" : "Precio no disponible";
		Label precioLabel = new Label(precioTexto);
		precioLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: green;");

		// Fechas

		Label fechaInicio = new Label(
				"Inicio: " + (reserva.getFechaInicio() != null ? reserva.getFechaInicio() : "Sin fecha"));

		Label fechaFin = new Label(
				"Fin: " + (reserva.getFechaFin() != null ? reserva.getFechaFin() : "Sin fecha"));

		// Categoría
		String categoria = producto != null && producto.getCategoria() != null ? producto.getCategoria() : "Sin categoria";
		Label categoriaLabel = new Label("Categoría: " + categoria);
		categoriaLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

		// Descripción
		String descripcion = producto != null && producto.getDescripcion() != null ? producto.getDescripcion()
				: "Sin descripcion";
		Label descripcionLabel = new Label(descripcion);
		descripcionLabel.setWrapText(true);

		// Contenedor fechas
		HBox fechasBox = new HBox(20, fechaInicio, fechaFin);

		// Añadir información
		info.getChildren().addAll(
				nombre,
				estado,
				precioLabel,
				fechasBox,
				categoriaLabel,
				descripcionLabel
		);
				
		Button btnVolver = new Button("Volver");
		btnVolver.setOnAction(e -> SceneManager.mostrarReservas());
		btnVolver.setStyle(
			    "-fx-background-color: #1179ff;" +
			    "-fx-text-fill: white;"
			);
		
		botonesBox = new HBox(15, btnVolver);

		VBox contenedor = new VBox(15, carrusel, info, botonesBox);
		contenedor.setStyle("-fx-padding: 20;");

		this.setCenter(contenedor);
	}

	private void mostrarImagen() {
		if (imagenes != null && !imagenes.isEmpty()) {
			Image img = new Image(imagenes.get(indiceImagen), true);
			imageView.setImage(img);
		}
	}
}
