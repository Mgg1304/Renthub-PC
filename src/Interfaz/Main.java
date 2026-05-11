package Interfaz;

import java.util.logging.Logger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

public class Main extends Application {
	
	private static final Logger log = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		launch();
	}
	
	@Override
    public void start(Stage stage) {
        Scene scene = new Scene(new StackPane(), 1000, 700);
        stage.setScene(scene);
        stage.setTitle("RentHub - Administración");
        stage.show();

        SceneManager.setScene(scene);
        SceneManager.mostrarInicioSesion();
        
        log.info("Aplicación iniciada correctamente.");
    }
	
}

