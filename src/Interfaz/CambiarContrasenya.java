package Interfaz;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CambiarContrasenya extends BorderPane{

	Image imagen;
	ImageView logo;
	Label lblUsuario, lblContrasenya;
	TextField txtUsuario;
	PasswordField txtContrasenya;
	Button btnCambiarContrasenya;
	VBox contenedor;
	
	public CambiarContrasenya() {
		
		//Logo
		imagen = new Image("file:src/resources/img/Logo_RentHub.png");
		logo = new ImageView(imagen);
		
		logo.setFitWidth(200);
		logo.setFitHeight(200);
		logo.setPreserveRatio(true);
		logo.setSmooth(true);
		
		//Label
		lblUsuario = new Label("Usuario");
		lblUsuario.setMaxWidth(300);
		lblContrasenya = new Label("Nueva Contraseña");
		lblContrasenya.setMaxWidth(300);
		
		// TextField
		txtUsuario = new TextField();
		txtUsuario.setLayoutX(50);
		txtUsuario.setLayoutY(50);
		txtUsuario.setMaxWidth(300);
		
		txtContrasenya = new PasswordField();
		txtContrasenya.setLayoutX(50);
		txtContrasenya.setLayoutY(50);
		txtContrasenya.setMaxWidth(300);
		
		// Button
		btnCambiarContrasenya = new Button("Cambiar Contraseña");
		btnCambiarContrasenya.setMaxWidth(300);
		btnCambiarContrasenya.setOnAction(e -> cambiarContrasenya());
		
		// Contenedor
		contenedor = new VBox(10);
		contenedor.setAlignment(Pos.CENTER);
		contenedor.setPadding(new Insets(20));
		contenedor.getChildren().addAll(logo, lblUsuario, txtUsuario, lblContrasenya, txtContrasenya, btnCambiarContrasenya);
		
		setCenter(contenedor);
	}

	private void cambiarContrasenya() {
		
		
		
		
		SceneManager.mostrarInicioSesion(); 
	}
}
