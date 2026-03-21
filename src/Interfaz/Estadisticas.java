package Interfaz;

import java.util.logging.Logger;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Estadisticas extends BorderPane{
	
	private static final Logger log = Logger.getLogger(Estadisticas.class.getName());

	VBox contenedor;
	Header header;
	
	public Estadisticas() {
		
		log.info("Mostrando estadísticas.");
		
		header = new Header();
		header.btnEstadisticas.setOnAction(null);
		setTop(header);
	}
}
