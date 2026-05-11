package Interfaz;

import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class AdminInterface extends BorderPane{
	
	Image imagen;
	ImageView logo;
	Button btnInventario, btnReservas, btnEstadisticas, btnPerfil;
	VBox contenedor;
	
	public AdminInterface() {
		
		//Logo
		imagen = new Image("file:src/resources/img/Logo_RentHub.png");
		logo = new ImageView(imagen);

		logo.setFitWidth(200);
		logo.setFitHeight(200); 
		logo.setPreserveRatio(true);
		logo.setSmooth(true);
		
		//Buttons
		btnInventario = new Button("Inventario");
		btnInventario.setMaxWidth(200);
		btnInventario.setOnAction(e -> SceneManager.mostrarInventario());
		
		btnReservas = new Button("Reservas");
		btnReservas.setMaxWidth(200);
		btnReservas.setOnAction(e -> SceneManager.mostrarReservas());
		
		btnEstadisticas = new Button("Estadísticas");
		btnEstadisticas.setMaxWidth(200);
		btnEstadisticas.setOnAction(e -> SceneManager.mostrarEstadisticas());
		
		btnPerfil = new Button("Perfil");
		btnPerfil.setMaxWidth(200);
		btnPerfil.setOnAction(e -> SceneManager.mostrarPerfil());
		
		//Layout
		contenedor = new VBox(20);
		contenedor.setAlignment(Pos.CENTER);
		contenedor.setPadding(new Insets(30));
		
		contenedor.getChildren().addAll(logo, btnInventario, btnReservas, btnEstadisticas, btnPerfil);
		setCenter(contenedor);
	}
}
