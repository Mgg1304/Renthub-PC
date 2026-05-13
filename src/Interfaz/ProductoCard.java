package Interfaz;

import Controller.ApiClient;
import Controller.ApiResult;
import Modelo.Producto;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;
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
		
		Image carga = new Image("file:src/resources/img/carga.png");
		imagen.setImage(carga);

		new Thread(() -> {

			log.info("Obteniendo URLs de imágenes para producto ID: " + (producto.getId()));

			ApiResult<List<String>> urlsResult = ApiClient.obtenerUrlsImagenesPorProducto(producto.getId());
			List<String> urls = urlsResult.isOk() && urlsResult.getData() != null ? urlsResult.getData() : List.of();
			if (!urlsResult.isOk()) {
				log.warning("No se pudieron obtener imagenes del producto " + producto.getId() + ": "
						+ urlsResult.getTechnicalMessage());
			}

			if (!urls.isEmpty()) {

				String primeraUrl = urls.get(0);
				
//				String Url = urls.get(0);
//				String primeraUrl = Url.replace("/upload/", "/upload/f_jpg/");

				Platform.runLater(() -> {
					try {
						log.info("Cargando imagen producto ID: " + producto.getId());
						log.info("URL: " + primeraUrl);

						Image img = new Image(primeraUrl, true);

						// DEBUG errores reales
						img.exceptionProperty().addListener((obs, old, ex) -> {
							if (ex != null) {
								log.severe("Error cargando imagen: " + ex.getMessage());
							}
						});

						imagen.setImage(img);

					} catch (Exception e) {
						log.severe("Error inesperado cargando imagen: " + e.getMessage());
					}
				});
			} else {
				log.warning("Producto sin imágenes: " + producto.getId());
			}

		}).start();

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
