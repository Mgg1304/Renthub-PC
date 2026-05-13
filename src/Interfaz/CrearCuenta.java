package Interfaz;

import Controller.ApiClient;
import Controller.ApiResult;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

public class CrearCuenta extends BorderPane {

	Image imagen;
	ImageView logo;
	Label lblNombre, lblUsuario, lblContrasenya, lblMensaje;
	TextField txtNombre, txtUsuario;
	PasswordField txtContrasenya;
	Button btnCrearCuenta;
	VBox contenedor;

	public CrearCuenta() {

		// Logo
		imagen = new Image("file:src/resources/img/Logo_RentHub.png");
		logo = new ImageView(imagen);

		logo.setFitWidth(200);
		logo.setFitHeight(200);
		logo.setPreserveRatio(true);
		logo.setSmooth(true);

		// Label
		lblNombre = new Label("Nombre");
		lblNombre.setMaxWidth(300);
		lblUsuario = new Label("Usuario");
		lblUsuario.setMaxWidth(300);
		lblContrasenya = new Label("Contraseña");
		lblContrasenya.setMaxWidth(300);
		lblMensaje = new Label("");
		lblMensaje.setMaxWidth(300);

		// TextField
		txtNombre = new TextField();
		txtNombre.setPromptText("Nombre");
		txtNombre.setLayoutX(50);
		txtNombre.setLayoutY(50);
		txtNombre.setMaxWidth(300);

		txtUsuario = new TextField();
		txtUsuario.setPromptText("Usuario");
		txtUsuario.setLayoutX(50);
		txtUsuario.setLayoutY(50);
		txtUsuario.setMaxWidth(300);

		txtContrasenya = new PasswordField();
		txtContrasenya.setPromptText("Contraseña");
		txtContrasenya.setLayoutX(50);
		txtContrasenya.setLayoutY(50);
		txtContrasenya.setMaxWidth(300);

		// Button
		btnCrearCuenta = new Button();
		btnCrearCuenta.setText("Crear la cuenta");
		btnCrearCuenta.setLayoutX(50);
		btnCrearCuenta.setLayoutY(50);
		btnCrearCuenta.setOnAction(e -> crearCuenta());
		btnCrearCuenta.styleProperty().set("-fx-background-color: #31c533; -fx-text-fill: white;");

		// Layout
		contenedor = new VBox(12);
		contenedor.setAlignment(Pos.CENTER);
		contenedor.setPadding(new Insets(30));

		contenedor.getChildren().addAll(logo, lblNombre, txtNombre, lblUsuario, txtUsuario, lblContrasenya,
				txtContrasenya, lblMensaje, btnCrearCuenta);
		setCenter(contenedor);
	}

	private void crearCuenta() {

		String usuario = txtUsuario.getText();
		String nombre = txtNombre.getText();
		String password = txtContrasenya.getText();

		ApiResult<Void> resultado = ApiClient.registerAdmin(usuario, nombre, password);

		if (resultado.isOk()) {
			lblMensaje.setText("Cuenta creada correctamente");
			PauseTransition pausa = new PauseTransition(Duration.seconds(2));
			pausa.setOnFinished(evento -> SceneManager.mostrarInicioSesion());
			pausa.play();
		} else {
			lblMensaje.setText(
					resultado.getUserMessage() != null ? resultado.getUserMessage() : "Error al crear la cuenta");
		}

	}

}
