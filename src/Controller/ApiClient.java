package Controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import Modelo.LoginResponse;
import Modelo.Producto;
import Modelo.Reserva;
import Modelo.Usuario;
import Modelo.Valoracion;

public class ApiClient {

	private static final Logger log = Logger.getLogger(ApiClient.class.getName());

	private static final String BASE_URL = "https://romantic-insight-production.up.railway.app/renthub";

	private static final HttpClient client = HttpClient.newHttpClient();

	private static final Gson gson = new Gson();

	// ---------------- REGISTRO ADMIN ----------------
	public static boolean registerAdmin(String usuario, String nombre, String password) {

		// Crear JSON con Gson
		String json = gson.toJson(new RegistroRequest(usuario, nombre, password));

		log.info("json registro enviado: " + json);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/admin/register"))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			log.info("Respuesta del servidor: " + response.statusCode() + " - " + response.body());

			return response.statusCode() == 200;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ---------------- LOGIN ADMIN ----------------
	public static LoginResponse loginAdmin(String usuario, String password) {

		String json = gson.toJson(new LoginRequest(usuario, password));

		log.info("json login enviado: " + json);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/admin/login"))
					.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			log.info("Respuesta del servidor: " + response.statusCode() + " - " + response.body());

			if (response.statusCode() == 200) {
				return gson.fromJson(response.body(), LoginResponse.class);
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// ---------------- CAMBIAR CONTRASEÑA ----------------
	public static boolean changePasswordAdmin(String usuario, String oldPassword, String newPassword) {

		String json = gson.toJson(new ChangePasswordRequest(usuario, oldPassword, newPassword));

		log.info("json cambiar contraseña enviado: " + json);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/admin/change-password"))
					.header("Content-Type", "application/json").PUT(HttpRequest.BodyPublishers.ofString(json)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			log.info("Respuesta del servidor: " + response.statusCode() + " - " + response.body());

			return response.statusCode() == 200;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// ---------------- OBTENER PRODUCTOS ----------------
	public static List<Producto> obtenerProductosPorAdmin(Long adminId) {

		log.info("Solicitando productos del admin: " + adminId);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/productos/admin/" + adminId))
					.GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {

				log.info("JSON recibido: " + response.body());

				Type listType = new TypeToken<List<Producto>>() {
				}.getType();

				List<Producto> productos = gson.fromJson(response.body(), listType);

				log.info("Productos parseados: " + productos.size());

				return productos;
			}

			return List.of();

		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}
	
	
	public static Producto obtenerProducto(long productoId) {

		log.info("Solicitando producto: " + productoId);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/productos/" + productoId))
					.GET().build();
			
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			if(response.statusCode() == 200) {
				
				log.info("JSON recibido: " + response.body());

				Producto producto = gson.fromJson(response.body(), Producto.class);

				log.info("Producto recibido: " + producto.toString());

				return producto;
				
			} else {
				log.warning("Error al obtener producto. Código: " + response.statusCode() + " - " + response.body());
				
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// ---------------- CREAR PRODUCTO ----------------
	public static boolean crearProducto(String nombre, String descripcion, String categoria, double precioDia,
			int stock, long adminId, List<File> archivos) {

		String boundary = "===" + System.currentTimeMillis() + "===";
		String LINE_FEED = "\r\n";

		try {

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

			escribirCampo(writer, boundary, "nombre", nombre);
			escribirCampo(writer, boundary, "descripcion", descripcion);
			escribirCampo(writer, boundary, "categoria", categoria);
			escribirCampo(writer, boundary, "precioDia", String.valueOf(precioDia));
			escribirCampo(writer, boundary, "stock", String.valueOf(stock));
			escribirCampo(writer, boundary, "adminId", String.valueOf(adminId));

			for (File archivo : archivos) {

				writer.append("--").append(boundary).append(LINE_FEED);
				writer.append("Content-Disposition: form-data; name=\"files\"; filename=\"").append(archivo.getName())
						.append("\"").append(LINE_FEED);

				String contentType = Files.probeContentType(archivo.toPath());
				if (contentType == null) {
					contentType = "application/octet-stream";
				}

				writer.append("Content-Type: ").append(contentType).append(LINE_FEED);
				writer.append(LINE_FEED);
				writer.flush();

				Files.copy(archivo.toPath(), outputStream);
				outputStream.write(LINE_FEED.getBytes());

				writer.flush();
			}

			writer.append("--").append(boundary).append("--").append(LINE_FEED);
			writer.close();

			byte[] body = outputStream.toByteArray();

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/productos/crear"))
					.header("Content-Type", "multipart/form-data; boundary=" + boundary)
					.POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			log.info("Respuesta crear producto: " + response.statusCode() + " - " + response.body());

			return response.statusCode() == 200;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	// ---------------- OBTENER URLS DE IMÁGENES ----------------
	public static List<String> obtenerUrlsImagenesPorProducto(long i) {

		log.info("Solicitando imágenes del producto: " + i);

		try {

			log.info("URL llamada: " + BASE_URL + "/archivos/producto/" + i);
			
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/archivos/producto/" + i)).GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			log.info("JSON recibido: " + response.body());

			if (response.statusCode() == 200) {

				log.info("JSON imágenes: " + response.body());

				Type listType = new TypeToken<List<String>>() {
				}.getType();
				return gson.fromJson(response.body(), listType);
			}

			return List.of();

		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	
	// ---------------- OBTENER USUARIO ----------------
	
	public static Usuario obtenerUsuario(long usuarioId) {

		log.info("Solicitando usuario: " + usuarioId);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/usuarios/" + usuarioId))
					.GET().build();
			
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			if(response.statusCode() == 200) {
				
				log.info("JSON recibido: " + response.body());

				Usuario usuario = gson.fromJson(response.body(), Usuario.class);

				log.info("Producto recibido: " + usuario.toString());

				return usuario;
				
			} else {
				log.warning("Error al obtener producto. Código: " + response.statusCode() + " - " + response.body());
				
			}

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// ---------------- OBTENER RESERVAS POR ADMIN ----------------
	
	public static List<Reserva> obtenerReservasPorAdmin(Long adminId) {

		log.info("Solicitando reservas del admin: " + adminId);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/reservas/admin/" + adminId))
					.GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {

				log.info("JSON recibido: " + response.body());

				Type listType = new TypeToken<List<Reserva>>() {
				}.getType();

				List<Reserva> reservas = gson.fromJson(response.body(), listType);

				log.info("Reservas parseadas: " + reservas.size());

				return reservas;
			}

			return List.of();

		} catch (Exception e) {
			e.printStackTrace();
			return List.of();
		}
	}

	// ---------------- OBTENER VALORACIONES POR PRODUCTO ----------------

	public static List<Valoracion> obtenerValoracionesPorProducto(int productoId) {

		log.info("Solicitando valoraciones del producto: " + productoId);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/valoraciones/producto/" + productoId))
					.GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {

				log.info("JSON valoraciones recibido: " + response.body());

				Type listType = new TypeToken<List<Valoracion>>() {
				}.getType();

				List<Valoracion> valoraciones = gson.fromJson(response.body(), listType);

				return valoraciones != null ? valoraciones : List.of();
			}

			log.warning("No se pudieron obtener valoraciones del producto " + productoId + ". Codigo: "
					+ response.statusCode());
			return List.of();

		} catch (Exception e) {
			log.warning("Error al obtener valoraciones del producto " + productoId + ": " + e.getMessage());
			return List.of();
		}
	}
	
	// ---------------- CONFIRMAR RESERVA ----------------
	public static void confirmarReserva(long id) {
		
		log.info("Confirmando reserva: " + id);

		try {

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/reservas/confirmar/" + id))
					.PUT(HttpRequest.BodyPublishers.noBody()).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			log.info("Respuesta confirmar reserva: " + response.statusCode() + " - " + response.body());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ---------------- CLASES AUXILIARES ----------------

	private static class RegistroRequest {
		String usuario;
		String nombre;
		String password;

		public RegistroRequest(String usuario, String nombre, String password) {
			this.usuario = usuario;
			this.nombre = nombre;
			this.password = password;
		}
	}

	private static class LoginRequest {
		String usuario;
		String password;

		public LoginRequest(String usuario, String password) {
			this.usuario = usuario;
			this.password = password;
		}
	}

	private static class ChangePasswordRequest {
		String usuario;
		String oldPassword;
		String newPassword;

		public ChangePasswordRequest(String usuario, String oldPassword, String newPassword) {
			this.usuario = usuario;
			this.oldPassword = oldPassword;
			this.newPassword = newPassword;
		}
	}

	private static void escribirCampo(PrintWriter writer, String boundary, String nombre, String valor) {

		String LINE_FEED = "\r\n";

		writer.append("--").append(boundary).append(LINE_FEED);
		writer.append("Content-Disposition: form-data; name=\"").append(nombre).append("\"").append(LINE_FEED);
		writer.append(LINE_FEED);
		writer.append(valor).append(LINE_FEED);
		writer.flush();
	}

}
