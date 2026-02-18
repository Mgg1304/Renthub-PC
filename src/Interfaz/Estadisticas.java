package Interfaz;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Estadisticas extends BorderPane{

	VBox contenedor;
	Header header;
	
	public Estadisticas() {
		
		header = new Header();
		header.btnEstadisticas.setOnAction(null);
		setTop(header);
	}
}
