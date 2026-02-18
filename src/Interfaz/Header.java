package Interfaz;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Header extends HBox{
	
	Image imagen;
	ImageView logo, avatar;
	Button btnInventario, btnReservas, btnEstadisticas, btnPerfil;
	HBox menu, header;
	VBox lm;
	
	public Header() {
		imagen = new Image("file:src/resources/img/Logo_RentHub.png");
		logo = new ImageView(imagen);
		
		logo.setFitHeight(100);
		logo.setPreserveRatio(true);
		
		btnInventario = new Button("Inventario");
		btnInventario.setPrefWidth(77);
		btnInventario.setOnAction(e -> SceneManager.mostrarInventario());
		
		btnReservas = new Button("Reservas");
		btnReservas.setPrefWidth(77);
		btnReservas.setOnAction(e -> SceneManager.mostrarReservas());
		
		btnEstadisticas = new Button("Estadisticas");
		btnEstadisticas.setPrefWidth(77);
		btnEstadisticas.setOnAction(e -> SceneManager.mostrarEstadisticas());
		
		btnPerfil = new Button("Perfil");
		btnPerfil.setPrefWidth(77);
		btnPerfil.setOnAction(e -> SceneManager.mostrarPerfil());
		
		menu = new HBox(1, btnInventario, btnReservas, btnEstadisticas, btnPerfil);
		menu.setAlignment(Pos.CENTER);
		
		lm = new VBox(10, logo, menu);
		lm.setAlignment(Pos.CENTER);
		
		setSpacing(20);
		setPadding(new Insets(15));
		setAlignment(Pos.CENTER);
		
		getChildren().addAll(lm);
	}

}
