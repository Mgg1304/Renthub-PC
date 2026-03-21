package Modelo;

public class SesionAdmin {

	private static Long idActual;
	private static String usuarioActual;
	private static String nombreActual;

	public static Long getIdActual() {
		return idActual;
	}

	public static void setIdActual(Long idActual) {
		SesionAdmin.idActual = idActual;
	}

	public static String getUsuarioActual() {
		return usuarioActual;
	}

	public static void setUsuarioActual(String usuarioActual) {
		SesionAdmin.usuarioActual = usuarioActual;
	}

	public static String getNombreActual() {
		return nombreActual;
	}

	public static void setNombreActual(String nombreActual) {
		SesionAdmin.nombreActual = nombreActual;
	}

	public static void cerrarSesion() {
		usuarioActual = null;
		nombreActual = null;
		idActual = null;
	}
}