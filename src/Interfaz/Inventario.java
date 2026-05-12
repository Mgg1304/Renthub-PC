package Interfaz;

import java.util.logging.Logger;

import Controller.ApiClient;
import Modelo.SesionAdmin;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Inventario extends BorderPane {

	private static final Logger log = Logger.getLogger(Inventario.class.getName());

	Header header;

	Button btnNuevoProducto;
	VBox contenedor;

	private ProductosView productosView;

	public Inventario() {

		// Header
		header = new Header();
		header.btnInventario.setOnAction(null);
		header.btnInventario.setStyle(
			    "-fx-background-color: #1179ff;" +
			    "-fx-text-fill: white;"
			);
		setTop(header);
 
		// Body
		// Button
		btnNuevoProducto = new Button("Nuevo producto");
		btnNuevoProducto.maxWidth(300);
		btnNuevoProducto.setOnAction(e -> SceneManager.mostrarNuevoProducto());
		btnNuevoProducto.setStyle(
			    "-fx-background-color: #31c533;" +
			    "-fx-text-fill: white;"
			);

		// Vista productos
		productosView = new ProductosView();

		// Contenedor
		contenedor = new VBox(10);
		contenedor.setAlignment(Pos.TOP_RIGHT);
		contenedor.setPadding(new Insets(20));
		contenedor.getChildren().addAll(btnNuevoProducto, productosView);

		setCenter(contenedor);

		log.info("Mostrando inventario del administrador con ID: " + SesionAdmin.getIdActual());

		// Cargar datos del backend
		cargarDatos();

	}

	private void cargarDatos() {

		log.info("Iniciando carga de productos para el administrador con ID: " + SesionAdmin.getIdActual());

		new Thread(() -> {

		    var productos = ApiClient.obtenerProductosPorAdmin(SesionAdmin.getIdActual());
		    var reservas = ApiClient.obtenerReservasPorAdmin(SesionAdmin.getIdActual());

		    log.info("Productos recibidos del backend para el administrador con ID: " + SesionAdmin.getIdActual() + ". Cantidad: " + (productos != null ? productos.size() : "null"));

		    Platform.runLater(() -> {
		    	log.info("Entrando en runLater");
		        productosView.cargarArticulos(productos, reservas);
		        log.info("Productos y reservas en curso cargadas en la vista para el administrador con ID: " + SesionAdmin.getIdActual());
		    });

		}).start();
		
	}
}