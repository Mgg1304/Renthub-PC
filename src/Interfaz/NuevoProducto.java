package Interfaz;

import java.io.File;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import Controller.ApiClient;
import Controller.ApiResult;
import Modelo.SesionAdmin;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
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

	Label lblNombre, lblCategoria, lblPrecio, lblStock, lblDescripcion, lblMensaje;
	TextField txtNombre, txtCategoria, txtPrecio, txtStock;
	TextArea txtDescripcion;
	Button btnNuevoProducto, btnAñadirFotos, btnVolverInventario;
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

		lblMensaje = new Label("");
		lblMensaje.setMaxWidth(300);

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
		btnAñadirFotos.setStyle(
			    "-fx-background-color: #1179ff;" +
			    "-fx-text-fill: white;"
			);

		btnNuevoProducto = new Button("Publicar producto");
		btnNuevoProducto.setMaxWidth(300);
		btnNuevoProducto.setOnAction(e -> nuevoProducto());
		btnNuevoProducto.setStyle(
			    "-fx-background-color: #31c533;" +
			    "-fx-text-fill: white;"
			);

		btnVolverInventario = new Button("Volver a inventario");
		btnVolverInventario.setOnAction(e -> SceneManager.mostrarInventario());
		btnVolverInventario.setStyle(
			    "-fx-background-color: #1179ff;" +
			    "-fx-text-fill: white;"
			);

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
				lblStock, txtStock, lblDescripcion, txtDescripcion, previewPane, lblMensaje, btnAñadirFotos,
				btnNuevoProducto);

		HBox barraSuperior = new HBox(btnVolverInventario);
		barraSuperior.setAlignment(Pos.TOP_LEFT);
		barraSuperior.setPadding(new Insets(20, 20, 0, 20));
		setTop(barraSuperior);

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
	        if (esImagen(archivo)) {
	            previewPane.getChildren().add(crearPreviewImagen(archivo));
	        } 
	        else if (esVideo(archivo)) {
	            previewPane.getChildren().add(crearPreviewVideo(archivo));
	        }
	    }

	    previewPane.requestLayout();
	    contenedor.requestLayout();
	}

	private static String obtenerMimeType(File archivo) {
	    try {
	        String mime = Files.probeContentType(archivo.toPath());
	        if (mime != null) {
	            return mime;
	        }
	    } catch (Exception e) {
	        log.warning("No se pudo detectar MIME por sistema para: " + archivo.getName());
	    }

	    String nombre = archivo.getName().toLowerCase();
	    if (nombre.endsWith(".png") || nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || nombre.endsWith(".webp")) {
	        return "image/*";
	    }
	    if (nombre.endsWith(".mp4") || nombre.endsWith(".mov")) {
	        return "video/*";
	    }

	    return null;
	}

	private static boolean esImagen(File archivo) {
	    String nombre = archivo.getName().toLowerCase();
	    if (nombre.endsWith(".png") || nombre.endsWith(".jpg") || nombre.endsWith(".jpeg") || nombre.endsWith(".webp")) {
	        return true;
	    }

	    String mime = obtenerMimeType(archivo);
	    return mime != null && mime.startsWith("image");
	}

	private static boolean esVideo(File archivo) {
	    String nombre = archivo.getName().toLowerCase();
	    if (nombre.endsWith(".mp4") || nombre.endsWith(".mov")) {
	        return true;
	    }

	    String mime = obtenerMimeType(archivo);
	    return mime != null && mime.startsWith("video");
	}

	private static Node crearPreviewImagen(File archivo) {
		
		log.info("Creando vista previa para imagen: " + archivo.getName());

	    Image image = new Image(archivo.toURI().toASCIIString(), 120, 120, true, true, false);
	    if (image.isError()) {
	        log.warning("No se pudo cargar la imagen para preview: " + archivo.getAbsolutePath());
	        if (image.getException() != null) {
	            log.warning("Motivo: " + image.getException().getMessage());
	        }
	        image = cargarImagenConImageIO(archivo);
	    }

	    if (image != null) {
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

	    if (esWebP(archivo)) {
	    	log.warning("El archivo es WEBP y este runtime no tiene decoder WEBP: " + archivo.getAbsolutePath());
	    }

	    Label noCompatible = new Label("Formato no compatible\n(" + archivo.getName() + ")");
	    noCompatible.setMinSize(120, 120);
	    noCompatible.setPrefSize(120, 120);
	    noCompatible.setWrapText(true);
	    noCompatible.setAlignment(Pos.CENTER);
	    noCompatible.setStyle(
	        "-fx-border-color: #ccc;" +
	        "-fx-border-radius: 4;" +
	        "-fx-padding: 6;"
	    );
	    return noCompatible;
	}

	private static Image cargarImagenConImageIO(File archivo) {
	    try {
	        BufferedImage buffered = ImageIO.read(archivo);
	        if (buffered == null) {
	            log.warning("ImageIO no pudo decodificar: " + archivo.getAbsolutePath());
	            return null;
	        }
	        return SwingFXUtils.toFXImage(buffered, null);
	    } catch (Exception e) {
	        log.warning("Fallo cargando con ImageIO: " + e.getMessage());
	        return null;
	    }
	}

	private static boolean esWebP(File archivo) {
		byte[] cabecera = new byte[12];
		try (FileInputStream fis = new FileInputStream(archivo)) {
			if (fis.read(cabecera) < 12) {
				return false;
			}
			return cabecera[0] == 'R' && cabecera[1] == 'I' && cabecera[2] == 'F' && cabecera[3] == 'F'
					&& cabecera[8] == 'W' && cabecera[9] == 'E' && cabecera[10] == 'B' && cabecera[11] == 'P';
		} catch (Exception e) {
			return false;
		}
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
	            lblMensaje.setText("Debes seleccionar al menos un archivo multimedia");
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

	        
	        ApiResult<Void> resultado = ApiClient.crearProducto(
	                nombre,
	                descripcion,
	                categoria,
	                precioDia,
	                stock,
	                adminId,
	                archivosSeleccionados
	        );

	        if (resultado.isOk()) {
	            lblMensaje.setText("Producto publicado correctamente");
	            SceneManager.mostrarInventario();
	        } else {
	            lblMensaje.setText(resultado.getUserMessage() != null ? resultado.getUserMessage()
	                    : "Error al publicar producto");
	            log.warning("Error publicando producto: " + resultado.getTechnicalMessage());
	        }

	    } catch (Exception ex) {
	        log.warning("Error preparando datos del producto: " + ex.getMessage());
	        lblMensaje.setText("Datos inválidos para publicar el producto");
	    }
	}

}
