package Interfaz;

import Controller.ApiClient;
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

public class CambiarContrasenya extends BorderPane {

	Image imagen;
	ImageView logo;
	Label lblUsuario, lblViejaContrasenya, lblNuevaContrasenya, lblMensaje;
	TextField txtUsuario;
	PasswordField txtViejaContrasenya, txtNuevaContrasenya;
	Button btnCambiarContrasenya;
	VBox contenedor;

	public CambiarContrasenya() {

		// Logo
		imagen = new Image("file:src/resources/img/Logo_RentHub.png");
		logo = new ImageView(imagen);

		logo.setFitWidth(200);
		logo.setFitHeight(200);
		logo.setPreserveRatio(true);
		logo.setSmooth(true);

		// Label
		lblUsuario = new Label("Usuario");
		lblUsuario.setMaxWidth(300);
		lblViejaContrasenya = new Label("Vieja contraseña");
		lblViejaContrasenya.setMaxWidth(300);
		lblNuevaContrasenya = new Label("Nueva contraseña");
		lblNuevaContrasenya.setMaxWidth(300);
		lblMensaje = new Label("");
		lblMensaje.setMaxWidth(300);

		// TextField
		txtUsuario = new TextField();
		txtUsuario.setLayoutX(50);
		txtUsuario.setLayoutY(50);
		txtUsuario.setMaxWidth(300);

		txtViejaContrasenya = new PasswordField();
		txtViejaContrasenya.setLayoutX(50);
		txtViejaContrasenya.setLayoutY(50);
		txtViejaContrasenya.setMaxWidth(300);

		txtNuevaContrasenya = new PasswordField();
		txtNuevaContrasenya.setLayoutX(50);
		txtNuevaContrasenya.setLayoutY(50);
		txtNuevaContrasenya.setMaxWidth(300);

		// Button
		btnCambiarContrasenya = new Button("Cambiar Contraseña");
		btnCambiarContrasenya.setMaxWidth(300);
		btnCambiarContrasenya.setOnAction(e -> cambiarContrasenya());
		btnCambiarContrasenya.setStyle(
			    "-fx-background-color: #31c533;" +
			    "-fx-text-fill: white;"
			);

		// Contenedor
		contenedor = new VBox(10);
		contenedor.setAlignment(Pos.CENTER);
		contenedor.setPadding(new Insets(20));
		contenedor.getChildren().addAll(logo, lblUsuario, txtUsuario, lblViejaContrasenya, txtViejaContrasenya,
				lblNuevaContrasenya, txtNuevaContrasenya, btnCambiarContrasenya, lblMensaje);

		setCenter(contenedor);
	}

	private void cambiarContrasenya() {

		String usuario = txtUsuario.getText();
		String viejaContrasenya = txtViejaContrasenya.getText();
		String nuevaContrasenya = txtNuevaContrasenya.getText();

		boolean exito = ApiClient.changePasswordAdmin(usuario, viejaContrasenya, nuevaContrasenya);

		if (exito) {
			lblMensaje.setText("Contraseña cambiada con éxito.");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {

			}
			SceneManager.mostrarInicioSesion();

		} else {
			lblMensaje.setText("Error al cambiar la contraseña. Verifica tus datos.");
		}
	}
}
