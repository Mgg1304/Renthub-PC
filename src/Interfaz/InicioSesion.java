package Interfaz;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import Modelo.Administrador;
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

public class InicioSesion extends BorderPane {

	InputStream stream;
	Image imagen;
	ImageView logo;
	Label lblUsuario, lblContrasenya;
	TextField txtUsuario;
	PasswordField txtContrasenya;
	Button btnSesion;
	Hyperlink linkContraseña, linkCrearCuenta;
	HBox enlaces;
	VBox contenedor;

	public InicioSesion() {
		// Logo
//		stream = ClassLoader.getSystemResourceAsStream("img/Logo_RentHub.png");
		imagen = new Image("file:src/resources/img/Logo_RentHub.png");
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

		// Layout
		contenedor = new VBox(12);
		contenedor.setAlignment(Pos.CENTER);
		contenedor.setPadding(new Insets(30));

		contenedor.getChildren().addAll(logo, lblUsuario, txtUsuario, lblContrasenya, txtContrasenya, enlaces,
				btnSesion);
		setCenter(contenedor);
	}

	// Metodos
	private void iniciarSesion() {

	    try {
	        String usuario = txtUsuario.getText();
	        String password = txtContrasenya.getText();

	        if (usuario.isEmpty() || password.isEmpty()) {
	            System.out.println("Usuario o contraseña vacíos");
	            return;
	        }

	        // Parámetros POST
	        String params =
	                "usuario=" + URLEncoder.encode(usuario, "UTF-8") +
	                "&password=" + URLEncoder.encode(password, "UTF-8");

	        URL url = new URL("http://localhost:8080/api/admin/login");
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

	        conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setRequestProperty(
	                "Content-Type",
	                "application/x-www-form-urlencoded"
	        );

	        // Enviar datos
	        OutputStream os = conn.getOutputStream();
	        os.write(params.getBytes());
	        os.flush();
	        os.close();

	        int responseCode = conn.getResponseCode();

	        if (responseCode == 200) {

	            BufferedReader br = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream())
	            );

	            StringBuilder response = new StringBuilder();
	            String line;
	            while ((line = br.readLine()) != null) {
	                response.append(line);
	            }
	            br.close();

	            // 🔑 AQUÍ SE PODRÍA PARSEAR JSON (ahora lo hacemos simple)
	            System.out.println("Login correcto: " + response);

	            // Guardar sesión (temporalmente)
	            Administrador.nombre = usuario;

	            // Ir a pantalla principal
	            SceneManager.mostrarInterfazAdministrador();

	        } else {
	            System.out.println("Usuario o contraseña incorrectos");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("Error al conectar con el backend");
	    }
	}


}
