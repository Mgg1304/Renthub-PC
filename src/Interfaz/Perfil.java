package Interfaz;

import java.util.logging.Logger;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Perfil extends BorderPane {
	
	private static final Logger log = Logger.getLogger(Perfil.class.getName());

	VBox contenedor;
	Header header;
	
	public Perfil() {
		
		log.info("Mostrando perfil del usuario.");
		
		header = new Header();
		header.btnPerfil.setOnAction(null);
		setTop(header);
	}
}
