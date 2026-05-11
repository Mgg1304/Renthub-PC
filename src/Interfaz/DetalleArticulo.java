package Interfaz;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;
import java.util.logging.Logger;

import Controller.ApiClient;
import Modelo.Producto;

public class DetalleArticulo extends BorderPane {

	private static Logger log = Logger.getLogger(DetalleArticulo.class.getName());

	private int indiceImagen = 0;
	private List<String> imagenes;

	private ImageView imageView;

	public DetalleArticulo(Producto producto) {

		log.info("Mostrando detalle del producto con ID: " + producto.getId());

		imagenes = ApiClient.obtenerUrlsImagenesPorProducto(producto.getId());
		
		imageView = new ImageView();
		imageView.setFitWidth(400);
		imageView.setFitHeight(300);
		imageView.setPreserveRatio(true);

		if (!imagenes.isEmpty()) {
			mostrarImagen();
		}

		Button btnPrev = new Button("<");
		Button btnNext = new Button(">");

		btnPrev.setOnAction(e -> {
			if (indiceImagen != 0) {
				indiceImagen = indiceImagen - 1;
				mostrarImagen();
			}
		});

		btnNext.setOnAction(e -> {
			if (indiceImagen != imagenes.size() - 1) {
				indiceImagen = indiceImagen + 1;
				mostrarImagen();
			}
		});

		StackPane carrusel = new StackPane(imageView, btnPrev, btnNext);
		StackPane.setAlignment(btnPrev, Pos.CENTER_LEFT);
		StackPane.setAlignment(btnNext, Pos.CENTER_RIGHT);

		VBox info = new VBox(10);

		// Nombre
		Label nombre = new Label(producto.getNombre());
		nombre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		// Precio
		Label precioLabel = new Label(producto.getPrecioPorDia() + " €/día");
		precioLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: green;");

		// Valoración
		Label rating = new Label(
				producto.getValoracionMedia() != null ? String.valueOf(producto.getValoracionMedia()) : "0.0");

		Label estrella = new Label("★");
		estrella.setStyle("-fx-text-fill: gold;");
		HBox ratingBox = new HBox(2, rating, estrella);
		ratingBox.setStyle("""
				    -fx-padding: 2 5;
				    -fx-background-radius: 5;
				""");

		// Spacer para separar
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		// Fila precio + valoración
		HBox filaPrecioValoracion = new HBox(10, precioLabel, spacer, ratingBox);
		filaPrecioValoracion.setAlignment(Pos.CENTER_LEFT);

		// Categoría
		Label categoriaLabel = new Label("Categoría: " + producto.getCategoria());
		categoriaLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

		//Stock
		Label stockLabel = new Label("Stock: " + producto.getStock());
		
		// Descripción
		Label descripcionLabel = new Label(producto.getDescripcion());
		descripcionLabel.setWrapText(true);

		// Añadir todo
		info.getChildren().addAll(nombre, filaPrecioValoracion, categoriaLabel, stockLabel, descripcionLabel);
		
		Button btnVolver = new Button("Volver");
		btnVolver.setOnAction(e -> SceneManager.mostrarReservas());


		VBox contenedor = new VBox(15, carrusel, info, btnVolver);
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
