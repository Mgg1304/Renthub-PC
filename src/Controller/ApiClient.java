package Controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

import Modelo.LoginResponse;

public class ApiClient {

	private static final Logger log = Logger.getLogger(ApiClient.class.getName());

	private static final String BASE_URL = "http://localhost:8080/auth";

	private static final HttpClient client = HttpClient.newHttpClient();

	// ---------------- REGISTRO ----------------
	public static boolean register(String usuario, String nombre, String password) {

		String json = String.format("{\"usuario\":\"%s\",\"nombre\":\"%s\",\"password\":\"%s\"}", usuario, nombre,
				password);
		
		log.info("json registro enviado: " + json);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/register"))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			log.info("Respuesta del servidor: " + response.statusCode() + " - " + response.body());

			return response.statusCode() == 200;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ---------------- LOGIN ----------------
	public static LoginResponse login(String usuario, String password) {

		String json = String.format("{\"usuario\":\"%s\",\"password\":\"%s\"}", usuario, password);
		log.info("json login enviado: " + json);
		
		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/login"))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			log.info("Respuesta del servidor: " + response.statusCode() + " - " + response.body());
			
			if (response.statusCode() == 200) {

				String body = response.body();

				// Parseo manual
				Long id = Long.parseLong(extraerValor(body, "id"));
				String user = extraerValor(body, "usuario");
				String nombre = extraerValor(body, "nombre");

				return new LoginResponse(id, user, nombre);

			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String extraerValor(String json, String clave) {

		String patron = "\"" + clave + "\":";

		int inicio = json.indexOf(patron) + patron.length();

		// Saltar espacios
		while (json.charAt(inicio) == ' ') {
			inicio++;
		}

		// Si es string
		if (json.charAt(inicio) == '"') {
			inicio++;
			int fin = json.indexOf("\"", inicio);
			return json.substring(inicio, fin);
		}

		// Si es número
		int fin = json.indexOf(",", inicio);
		if (fin == -1) {
			fin = json.indexOf("}", inicio);
		}

		return json.substring(inicio, fin);
	}

	// ---------------- CAMBIAR CONTRASEÑA ----------------
	public static boolean changePassword(String usuario, String oldPassword, String newPassword) {

		String json = String.format("{\"usuario\":\"%s\",\"oldPassword\":\"%s\",\"newPassword\":\"%s\"}", usuario,
				oldPassword, newPassword);
		log.info("json cambiar contraseña enviado: " + json);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/change-password"))
					.header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(json)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			log.info("Respuesta del servidor: " + response.statusCode() + " - " + response.body());

			return response.statusCode() == 200;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
