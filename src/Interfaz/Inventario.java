package Interfaz;

import java.util.logging.Logger;

import Controller.ApiClient;
import Controller.ApiResult;
import Modelo.Producto;
import Modelo.Reserva;
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
	Label lblMensaje;
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

		lblMensaje = new Label("");
		lblMensaje.setStyle("-fx-text-fill: #b00020;");

		// Vista productos
		productosView = new ProductosView();

		// Contenedor
		contenedor = new VBox(10);
		contenedor.setAlignment(Pos.TOP_RIGHT);
		contenedor.setPadding(new Insets(20));
		contenedor.getChildren().addAll(btnNuevoProducto, lblMensaje, productosView);

		setCenter(contenedor);

		log.info("Mostrando inventario del administrador con ID: " + SesionAdmin.getIdActual());

		// Cargar datos del backend
		cargarDatos();

	}

	private void cargarDatos() {

		log.info("Iniciando carga de productos para el administrador con ID: " + SesionAdmin.getIdActual());

		new Thread(() -> {

		    ApiResult<java.util.List<Producto>> productosResult = ApiClient.obtenerProductosPorAdmin(SesionAdmin.getIdActual());
		    ApiResult<java.util.List<Reserva>> reservasResult = ApiClient.obtenerReservasPorAdmin(SesionAdmin.getIdActual());
		    java.util.List<Producto> productos = productosResult.isOk() && productosResult.getData() != null
		    		? productosResult.getData() : java.util.List.of();
		    java.util.List<Reserva> reservas = reservasResult.isOk() && reservasResult.getData() != null
		    		? reservasResult.getData() : java.util.List.of();

		    if (!productosResult.isOk()) {
		    	log.warning("Error cargando productos: " + productosResult.getTechnicalMessage());
		    }
		    if (!reservasResult.isOk()) {
		    	log.warning("Error cargando reservas: " + reservasResult.getTechnicalMessage());
		    }

		    log.info("Productos recibidos del backend para el administrador con ID: " + SesionAdmin.getIdActual() + ". Cantidad: " + productos.size());

		    Platform.runLater(() -> {
		    	log.info("Entrando en runLater");
		        productosView.cargarArticulos(productos, reservas);
		        if (!productosResult.isOk() || !reservasResult.isOk()) {
		        	lblMensaje.setText("No se pudieron cargar todos los datos del inventario.");
		        } else {
		        	lblMensaje.setText("");
		        }
		        log.info("Productos y reservas en curso cargadas en la vista para el administrador con ID: " + SesionAdmin.getIdActual());
		    });

		}).start();
		
	}
}
