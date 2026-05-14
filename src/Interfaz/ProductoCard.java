package Interfaz;

import Modelo.Producto;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Objects;
import java.util.logging.Logger;

public class ProductoCard extends VBox {

	Logger log = Logger.getLogger(ProductoCard.class.getName());

	public ProductoCard(Producto producto) {

		setSpacing(5);
		setAlignment(Pos.CENTER);
		setPrefWidth(120);

		setStyle("""
				    -fx-background-color: white;
				    -fx-padding: 10;
				    -fx-background-radius: 10;
				    -fx-border-radius: 10;
				    -fx-border-color: #ddd;
				""");

		// Imagen placeholder inicial
		ImageView imagen = new ImageView();
		imagen.setFitWidth(100);
		imagen.setFitHeight(80);
		imagen.setPreserveRatio(true);

		log.info("Cargando imagen para producto ID: " + producto.getId());
		
		Image carga = new Image(Objects.requireNonNull(getClass().getResource("/img/carga.png")).toExternalForm());
		imagen.setImage(carga);

		ImageLoader.loadProductImage(producto.getId(), imagen::setImage);

		// Rating
		Label rating = new Label(
				producto.getValoracionMedia() != null ? String.valueOf(producto.getValoracionMedia()) : "0.0");

		Label estrella = new Label("★");
		estrella.setStyle("-fx-text-fill: gold;");

		HBox ratingBox = new HBox(2, rating, estrella);
		ratingBox.setStyle("""
				    -fx-padding: 2 5;
				    -fx-background-radius: 5;
				""");

		StackPane stack = new StackPane(imagen);
		StackPane.setAlignment(ratingBox, Pos.TOP_RIGHT);

		// Nombre
		Label nombre = new Label(producto.getNombre());
		nombre.setWrapText(true);
		nombre.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");

		// Precio
		Label precio = new Label(producto.getPrecioPorDia() + "€");

		HBox precioValoracionBox = new HBox(10, ratingBox, precio);
		precioValoracionBox.setAlignment(Pos.CENTER);
		
		getChildren().addAll(stack, nombre, precioValoracionBox);
		
		this.setOnMouseClicked(e -> {
		    SceneManager.mostrarDetalleProducto(producto);
		});
	}
}
