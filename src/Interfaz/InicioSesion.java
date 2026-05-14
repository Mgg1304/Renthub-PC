package Interfaz;

import Controller.ApiClient;
import Controller.ApiResult;
import Modelo.LoginResponse;
import Modelo.SesionAdmin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class InicioSesion extends BorderPane {

	Image imagen;
	ImageView logo;
	Label lblUsuario, lblContrasenya, lblMensaje;
	TextField txtUsuario;
	PasswordField txtContrasenya;
	Button btnSesion;
	Hyperlink linkContraseña, linkCrearCuenta;
	HBox enlaces;
	VBox contenedor;

	public InicioSesion() {
		// Logo
//		stream = ClassLoader.getSystemResourceAsStream("img/Logo_RentHub.png");
		Image imagen = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/Logo_RentHub.png")));
		logo = new ImageView(imagen);

		logo.setFitWidth(200);
		logo.setFitHeight(200);
		logo.setPreserveRatio(true);
		logo.setSmooth(true);

		// Label
		lblUsuario = new Label("Usuario");
		lblUsuario.setMaxWidth(300);
		lblContrasenya = new Label("Contraseña");
		lblContrasenya.setMaxWidth(300);
		lblMensaje = new Label("");

		// TextField
		txtUsuario = new TextField();
		txtUsuario.setLayoutX(50);
		txtUsuario.setLayoutY(50);
		txtUsuario.setMaxWidth(300);

		txtContrasenya = new PasswordField();
		txtContrasenya.setLayoutX(50);
		txtContrasenya.setLayoutY(50);
		txtContrasenya.setMaxWidth(300);

		// Hyperlink
		linkContraseña = new Hyperlink("Recuperar contraseña");
		linkContraseña.setLayoutX(50);
		linkContraseña.setLayoutX(100);
		linkContraseña.setOnAction(e -> SceneManager.mostrarCambiarContrasenya());

		linkCrearCuenta = new Hyperlink("¿No tienes cuenta? Crear cuenta");
		linkCrearCuenta.setLayoutX(50);
		linkCrearCuenta.setLayoutY(100);
		linkCrearCuenta.setOnAction(e -> SceneManager.mostrarCrearCuenta());

		enlaces = new HBox(15, linkContraseña, linkCrearCuenta);
		enlaces.setAlignment(Pos.CENTER);

		// Button
		btnSesion = new Button();
		btnSesion.setText("Inicio sesion");
		btnSesion.setLayoutX(50);
		btnSesion.setLayoutY(50);
		btnSesion.setOnAction(e -> iniciarSesion());
		btnSesion.setStyle("-fx-background-color: #31c533;" + "-fx-text-fill: white;");

		// Layout
		contenedor = new VBox(12);
		contenedor.setAlignment(Pos.CENTER);
		contenedor.setPadding(new Insets(30));

		contenedor.getChildren().addAll(logo, lblUsuario, txtUsuario, lblContrasenya, txtContrasenya, lblMensaje,
				enlaces, btnSesion);
		setCenter(contenedor);
	}

	// Metodos
	private void iniciarSesion() {

		String usuario = txtUsuario.getText();
		String password = txtContrasenya.getText();

		ApiResult<LoginResponse> loginResult = ApiClient.loginAdmin(usuario, password);

		if (loginResult.isOk() && loginResult.getData() != null) {
			LoginResponse login = loginResult.getData();

			SesionAdmin.setIdActual(login.getId());
			SesionAdmin.setUsuarioActual(usuario);
			SesionAdmin.setNombreActual(login.getNombre());

			SceneManager.mostrarInterfazAdministrador();
		} else {
			lblMensaje.setText(
					loginResult.getUserMessage() != null ? loginResult.getUserMessage() : "No se pudo iniciar sesión.");

		}
	}
}
