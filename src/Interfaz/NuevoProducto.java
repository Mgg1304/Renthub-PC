package Interfaz;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import Controller.ApiClient;
import Modelo.SesionAdmin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NuevoProducto extends BorderPane {
	
	private static final Logger log = Logger.getLogger(NuevoProducto.class.getName());

	Label lblNombre, lblCategoria, lblPrecio, lblStock, lblDescripcion;
	TextField txtNombre, txtCategoria, txtPrecio, txtStock;
	TextArea txtDescripcion;
	Button btnNuevoProducto, btnAñadirFotos;
	FlowPane previewPane;
	List<File> archivosSeleccionados = new ArrayList<>();
	VBox contenedor;
	HBox botones;

	public NuevoProducto() {
		
		log.info("Mostrando interfaz de nuevo producto.");

		// Label
		lblNombre = new Label("Nombre del producto");
		lblNombre.setMaxWidth(300);

		lblCategoria = new Label("Categoria del producto");
		lblCategoria.setMaxWidth(300);

		lblPrecio = new Label("Precio por dia del producto");
		lblPrecio.setMaxWidth(300);

		lblStock = new Label("Stock del producto");
		lblStock.setMaxWidth(300);

		lblDescripcion = new Label("Descripcion del producto");
		lblDescripcion.setMaxWidth(300);

		// TextField
		txtNombre = new TextField();
		txtNombre.setMaxWidth(300);

		txtCategoria = new TextField();
		txtCategoria.setMaxWidth(300);

		txtPrecio = new TextField();
		txtPrecio.setMaxWidth(300);

		txtStock = new TextField();
		txtStock.setMaxWidth(300);

		txtDescripcion = new TextArea();
		txtDescripcion.setPrefHeight(300);
		txtDescripcion.setPrefWidth(150);
		txtDescripcion.setWrapText(true);
		txtDescripcion.setMaxWidth(300);

		// Button
		btnAñadirFotos = new Button("Añadir fotos");
		btnAñadirFotos.setMaxWidth(300);
		btnAñadirFotos.setOnAction(e -> seleccionarArchivos());

		btnNuevoProducto = new Button("Publicar producto");
		btnNuevoProducto.setMaxWidth(300);
		btnNuevoProducto.setOnAction(e -> nuevoProducto());

		// Flowpane
		previewPane = new FlowPane();
		previewPane.setHgap(10);
		previewPane.setVgap(10);
		previewPane.setPrefWrapLength(300);

		// Contenedor
		contenedor = new VBox();
		contenedor.setAlignment(Pos.CENTER);
		contenedor.setPadding(new Insets(20));
		contenedor.getChildren().addAll(lblNombre, txtNombre, lblCategoria, txtCategoria, lblPrecio, txtPrecio,
				lblStock, txtStock, lblDescripcion, txtDescripcion, previewPane, btnAñadirFotos, btnNuevoProducto);

		setCenter(contenedor);
	}

	private void seleccionarArchivos() {

		log.info("Abriendo selector de archivos multimedia.");
		
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Seleccionar archivos multimedia");

	    fileChooser.getExtensionFilters().add(
	        new FileChooser.ExtensionFilter(
	            "Imágenes y vídeos",
	            "*.png", "*.jpg", "*.jpeg", "*.mp4", "*.mov", "*.webp"
	        )
	    );

	    Stage stage = (Stage) this.getScene().getWindow();
	    List<File> seleccion = fileChooser.showOpenMultipleDialog(stage);

	    if (seleccion == null || seleccion.isEmpty()) {
	    	log.info("No se seleccionaron archivos multimedia.");
	        return;
	    }

	    // Limpiar estado anterior
	    archivosSeleccionados.clear();
	    previewPane.getChildren().clear();

	    archivosSeleccionados.addAll(seleccion);

	    for (File archivo : archivosSeleccionados) {

	        String mime;
	        try {
	            mime = Files.probeContentType(archivo.toPath());
	        } catch (Exception e) {
	            continue;
	        }

	        if (mime != null && mime.startsWith("image")) {
	            previewPane.getChildren().add(crearPreviewImagen(archivo));
	        } 
	        else if (mime != null && mime.startsWith("video")) {
	            previewPane.getChildren().add(crearPreviewVideo(archivo));
	        }
	    }
	}

	private static ImageView crearPreviewImagen(File archivo) {
		
		log.info("Creando vista previa para imagen: " + archivo.getName());

	    Image image = new Image(archivo.toURI().toString(), 120, 120, true, true);
	    ImageView imageView = new ImageView(image);

	    imageView.setFitWidth(120);
	    imageView.setFitHeight(120);
	    imageView.setPreserveRatio(true);

	    imageView.setStyle(
	        "-fx-border-color: #ccc;" +
	        "-fx-border-radius: 4;" +
	        "-fx-padding: 4;"
	    );

	    return imageView;
	}
	
	private static MediaView crearPreviewVideo(File archivo) {
		
		log.info("Creando vista previa para video: " + archivo.getName());

	    Media media = new Media(archivo.toURI().toString());
	    MediaPlayer mediaPlayer = new MediaPlayer(media);

	    mediaPlayer.setAutoPlay(false);

	    MediaView mediaView = new MediaView(mediaPlayer);
	    mediaView.setFitWidth(160);
	    mediaView.setFitHeight(120);
	    mediaView.setPreserveRatio(true);

	    mediaView.setStyle(
	        "-fx-border-color: #ccc;" +
	        "-fx-border-radius: 4;" +
	        "-fx-padding: 4;"
	    );

	    return mediaView;
	}



	private void nuevoProducto() {

	    log.info("Publicando nuevo producto.");

	    try {
	        if (archivosSeleccionados == null || archivosSeleccionados.isEmpty()) {
	            System.out.println("Debes seleccionar al menos un archivo multimedia");
	            return;
	        }

	        // Datos del formulario
	        String nombre = txtNombre.getText();
	        String categoria = txtCategoria.getText();
	        String descripcion = txtDescripcion.getText();
	        double precioDia = Double.parseDouble(txtPrecio.getText());
	        int stock = Integer.parseInt(txtStock.getText());
	        long adminId = SesionAdmin.getIdActual();

	        log.info("Datos del nuevo producto - Nombre: " + nombre +
	                ", Categoria: " + categoria +
	                ", Precio: " + precioDia +
	                ", Stock: " + stock);

	        
	        boolean ok = ApiClient.crearProducto(
	                nombre,
	                descripcion,
	                categoria,
	                precioDia,
	                stock,
	                adminId,
	                archivosSeleccionados
	        );

	        if (ok) {
	            System.out.println("Producto publicado correctamente");
	            SceneManager.mostrarInventario();
	        } else {
	            System.out.println("Error al publicar producto");
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}

}
