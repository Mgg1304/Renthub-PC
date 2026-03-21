package Interfaz;

import java.util.logging.Logger;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Reservas extends BorderPane{
	
	private static final Logger log = Logger.getLogger(Reservas.class.getName());

	VBox contenedor;
	Header header;
	
	public Reservas() {
		
		log.info("Mostrando la sección de reservas.");
		
		header = new Header();
		header.btnReservas.setOnAction(null);
		setTop(header);
	}
}
