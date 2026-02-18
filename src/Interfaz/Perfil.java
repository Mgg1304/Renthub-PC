package Interfaz;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Perfil extends BorderPane {

	VBox contenedor;
	Header header;
	
	public Perfil() {
		
		header = new Header();
		header.btnPerfil.setOnAction(null);
		setTop(header);
	}
}
