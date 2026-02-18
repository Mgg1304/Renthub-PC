package Interfaz;

import javafx.scene.Scene;

public class SceneManager {

	private static Scene scene;

	public static void setScene(Scene s) {
		scene = s;
	}

	public static void mostrarInicioSesion() {
		scene.setRoot(new InicioSesion());
	}

	public static void mostrarCrearCuenta() {
		scene.setRoot(new CrearCuenta());
	}

	public static void mostrarCambiarContrasenya() {
		scene.setRoot(new CambiarContrasenya());
	}

	public static void mostrarInterfazAdministrador() {
		scene.setRoot(new AdminInterface());
	}

	public static void mostrarInventario() {
		scene.setRoot(new Inventario());
	}

	public static void mostrarPerfil() {
		scene.setRoot(new Perfil());
	}

	public static void mostrarEstadisticas() {
		scene.setRoot(new Estadisticas());
	}

	public static void mostrarReservas() {
		scene.setRoot(new Reservas());
	}
	
	public static void mostrarNuevoProducto() {
		scene.setRoot(new NuevoProducto());
	}
}
