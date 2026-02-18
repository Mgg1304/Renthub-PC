package Interfaz;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Reservas extends BorderPane{

	VBox contenedor;
	Header header;
	
	public Reservas() {
		
		header = new Header();
		header.btnReservas.setOnAction(null);
		setTop(header);
	}
}
