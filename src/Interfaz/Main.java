package Interfaz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

public class Main extends Application {

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
//        SceneManager.mostrarInventario();
    }
	
}

