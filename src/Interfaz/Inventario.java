package Interfaz;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Inventario extends BorderPane {
	
	Header header;

	Button btnNuevoProducto;
	VBox contenedor;
	
	public Inventario() {
		
		//Header
		header = new Header();
		header.btnInventario.setOnAction(null);
		setTop(header);
		
		//Body
		//Button
		btnNuevoProducto = new Button("Nuevo producto");
		btnNuevoProducto.maxWidth(300);
		btnNuevoProducto.setOnAction(e -> SceneManager.mostrarNuevoProducto());
		
		//Contenedor
		contenedor = new VBox(10);
		contenedor.setAlignment(Pos.CENTER);
		contenedor.setPadding(new Insets(20));
		contenedor.getChildren().addAll(btnNuevoProducto);
		
		setCenter(contenedor);
		
	}
}