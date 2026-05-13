package Controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import Modelo.LoginResponse;
import Modelo.Producto;
import Modelo.Reserva;
import Modelo.Usuario;
import Modelo.Valoracion;

public class ApiClient {

	private static final Logger log = Logger.getLogger(ApiClient.class.getName());
	private static final String BASE_URL = "https://romantic-insight-production.up.railway.app/renthub";
	private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(8);
	private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);

	private static final HttpClient client = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();
	private static final Gson gson = new Gson();

	public static ApiResult<Void> registerAdmin(String usuario, String nombre, String password) {
		String json = gson.toJson(new RegistroRequest(usuario, nombre, password));
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/admin/register"))
				.timeout(REQUEST_TIMEOUT).header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json)).build();
		ApiResult<String> http = sendRequest(request, "POST /admin/register");
		if (!http.isOk()) {
			return propagate(http);
		}
		return fromStatusWithoutBody(http.getStatusCode(), "Cuenta creada correctamente.",
				"No se pudo crear la cuenta.");
	}

	public static ApiResult<LoginResponse> loginAdmin(String usuario, String password) {
		String json = gson.toJson(new LoginRequest(usuario, password));
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/admin/login")).timeout(REQUEST_TIMEOUT)
				.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
		ApiResult<String> http = sendRequest(request, "POST /admin/login");
		if (!http.isOk()) {
			return propagate(http);
		}
		if (http.getStatusCode() == 200) {
			return parseJson(http.getData(), LoginResponse.class, "No se pudo interpretar la respuesta del login.");
		}
		return mapHttpStatus(http.getStatusCode(), "Credenciales incorrectas.", "Login no autorizado.");
	}

	public static ApiResult<Void> changePasswordAdmin(String usuario, String oldPassword, String newPassword) {
		String json = gson.toJson(new ChangePasswordRequest(usuario, oldPassword, newPassword));
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/admin/change-password"))
				.timeout(REQUEST_TIMEOUT).header("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(json)).build();
		ApiResult<String> http = sendRequest(request, "PUT /admin/change-password");
		if (!http.isOk()) {
			return propagate(http);
		}
		return fromStatusWithoutBody(http.getStatusCode(), "Contraseña actualizada correctamente.",
				"No se pudo cambiar la contraseña.");
	}

	public static ApiResult<List<Producto>> obtenerProductosPorAdmin(Long adminId) {
		if (adminId == null) {
			return ApiResult.error(ApiErrorType.VALIDATION, null, "Sesion no valida.",
					"adminId nulo al solicitar productos", null);
		}
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/productos/admin/" + adminId))
				.timeout(REQUEST_TIMEOUT).GET().build();
		ApiResult<String> http = sendRequest(request, "GET /productos/admin/{id}");
		if (!http.isOk()) {
			return propagate(http);
		}
		if (http.getStatusCode() == 200) {
			Type listType = new TypeToken<List<Producto>>() {
			}.getType();
			return parseJson(http.getData(), listType, "No se pudieron interpretar los productos.");
		}
		return mapHttpStatus(http.getStatusCode(), "No se pudieron cargar los productos.",
				"Fallo al obtener productos por admin.");
	}

	public static ApiResult<Producto> obtenerProducto(long productoId) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/productos/" + productoId))
				.timeout(REQUEST_TIMEOUT).GET().build();
		ApiResult<String> http = sendRequest(request, "GET /productos/{id}");
		if (!http.isOk()) {
			return propagate(http);
		}
		if (http.getStatusCode() == 200) {
			return parseJson(http.getData(), Producto.class, "No se pudo interpretar el producto.");
		}
		return mapHttpStatus(http.getStatusCode(), "No se pudo cargar el producto.", "Producto no disponible.");
	}

	public static ApiResult<Void> crearProducto(String nombre, String descripcion, String categoria, double precioDia,
			int stock, long adminId, List<File> archivos) {
		if (archivos == null || archivos.isEmpty()) {
			return ApiResult.error(ApiErrorType.VALIDATION, null, "Debes adjuntar al menos un archivo.",
					"Lista de archivos vacia en crearProducto", null);
		}

		String boundary = "===" + System.currentTimeMillis() + "===";
		String lineFeed = "\r\n";

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
				writer.append("--").append(boundary).append(lineFeed);
				writer.append("Content-Disposition: form-data; name=\"files\"; filename=\"").append(archivo.getName())
						.append("\"").append(lineFeed);
				String contentType = Files.probeContentType(archivo.toPath());
				if (contentType == null) {
					contentType = "application/octet-stream";
				}
				writer.append("Content-Type: ").append(contentType).append(lineFeed);
				writer.append(lineFeed);
				writer.flush();
				Files.copy(archivo.toPath(), outputStream);
				outputStream.write(lineFeed.getBytes());
				writer.flush();
			}

			writer.append("--").append(boundary).append("--").append(lineFeed);
			writer.close();

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/productos/crear"))
					.timeout(REQUEST_TIMEOUT).header("Content-Type", "multipart/form-data; boundary=" + boundary)
					.POST(HttpRequest.BodyPublishers.ofByteArray(outputStream.toByteArray())).build();
			ApiResult<String> http = sendRequest(request, "POST /productos/crear");
			if (!http.isOk()) {
				return propagate(http);
			}
			return fromStatusWithoutBody(http.getStatusCode(), "Producto creado correctamente.",
					"No se pudo publicar el producto.");
		} catch (Exception e) {
			log.log(Level.WARNING, "error_type=UNKNOWN endpoint=POST /productos/crear message=Error preparando multipart",
					e);
			return ApiResult.error(ApiErrorType.UNKNOWN, null, "No se pudo preparar la subida del producto.",
					e.getMessage(), e);
		}
	}

	public static ApiResult<List<String>> obtenerUrlsImagenesPorProducto(long productoId) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/archivos/producto/" + productoId))
				.timeout(REQUEST_TIMEOUT).GET().build();
		ApiResult<String> http = sendRequest(request, "GET /archivos/producto/{id}");
		if (!http.isOk()) {
			return propagate(http);
		}
		if (http.getStatusCode() == 200) {
			Type listType = new TypeToken<List<String>>() {
			}.getType();
			return parseJson(http.getData(), listType, "No se pudieron interpretar las imagenes.");
		}
		return mapHttpStatus(http.getStatusCode(), "No se pudieron cargar las imagenes del producto.",
				"No se pudieron obtener URLs de imagenes");
	}

	public static ApiResult<Usuario> obtenerUsuario(long usuarioId) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/usuarios/" + usuarioId))
				.timeout(REQUEST_TIMEOUT).GET().build();
		ApiResult<String> http = sendRequest(request, "GET /usuarios/{id}");
		if (!http.isOk()) {
			return propagate(http);
		}
		if (http.getStatusCode() == 200) {
			return parseJson(http.getData(), Usuario.class, "No se pudo interpretar el usuario.");
		}
		return mapHttpStatus(http.getStatusCode(), "No se pudo cargar el usuario.", "Usuario no disponible.");
	}

	public static ApiResult<List<Reserva>> obtenerReservasPorAdmin(Long adminId) {
		if (adminId == null) {
			return ApiResult.error(ApiErrorType.VALIDATION, null, "Sesion no valida.",
					"adminId nulo al solicitar reservas", null);
		}
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/reservas/admin/" + adminId))
				.timeout(REQUEST_TIMEOUT).GET().build();
		ApiResult<String> http = sendRequest(request, "GET /reservas/admin/{id}");
		if (!http.isOk()) {
			return propagate(http);
		}
		if (http.getStatusCode() == 200) {
			Type listType = new TypeToken<List<Reserva>>() {
			}.getType();
			return parseJson(http.getData(), listType, "No se pudieron interpretar las reservas.");
		}
		return mapHttpStatus(http.getStatusCode(), "No se pudieron cargar las reservas.",
				"Fallo al obtener reservas por admin.");
	}

	public static ApiResult<List<Valoracion>> obtenerValoracionesPorProducto(int productoId) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/valoraciones/producto/" + productoId))
				.timeout(REQUEST_TIMEOUT).GET().build();
		ApiResult<String> http = sendRequest(request, "GET /valoraciones/producto/{id}");
		if (!http.isOk()) {
			return propagate(http);
		}
		if (http.getStatusCode() == 200) {
			Type listType = new TypeToken<List<Valoracion>>() {
			}.getType();
			return parseJson(http.getData(), listType, "No se pudieron interpretar las valoraciones.");
		}
		return mapHttpStatus(http.getStatusCode(), "No se pudieron cargar las valoraciones.",
				"Fallo al obtener valoraciones por producto.");
	}

	public static ApiResult<Void> confirmarReserva(long id) {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/reservas/confirmar/" + id))
				.timeout(REQUEST_TIMEOUT).PUT(HttpRequest.BodyPublishers.noBody()).build();
		ApiResult<String> http = sendRequest(request, "PUT /reservas/confirmar/{id}");
		if (!http.isOk()) {
			return propagate(http);
		}
		return fromStatusWithoutBody(http.getStatusCode(), "Reserva confirmada correctamente.",
				"No se pudo confirmar la reserva.");
	}

	private static ApiResult<String> sendRequest(HttpRequest request, String endpoint) {
		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			log.info("event=api_response endpoint=" + endpoint + " status=" + response.statusCode());
			return ApiResult.success(response.body() == null ? "" : response.body(), response.statusCode());
		} catch (HttpTimeoutException e) {
			log.log(Level.WARNING,
					"error_type=TIMEOUT endpoint=" + endpoint + " message=La peticion supero el tiempo maximo", e);
			return ApiResult.error(ApiErrorType.TIMEOUT, null, "La operación tardó demasiado. Inténtalo de nuevo.",
					e.getMessage(), e);
		} catch (ConnectException e) {
			log.log(Level.WARNING, "error_type=NETWORK endpoint=" + endpoint + " message=No se pudo conectar", e);
			return ApiResult.error(ApiErrorType.NETWORK, null,
					"No se pudo conectar con el servidor. Revisa tu conexión.", e.getMessage(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.log(Level.WARNING, "error_type=UNKNOWN endpoint=" + endpoint + " message=Hilo interrumpido", e);
			return ApiResult.error(ApiErrorType.UNKNOWN, null, "La operación fue interrumpida.", e.getMessage(), e);
		} catch (Exception e) {
			log.log(Level.WARNING, "error_type=NETWORK endpoint=" + endpoint + " message=Fallo de red", e);
			return ApiResult.error(ApiErrorType.NETWORK, null,
					"No se pudo conectar con el servidor. Revisa tu conexión.", e.getMessage(), e);
		}
	}

	private static <T> ApiResult<T> parseJson(String body, Class<T> clazz, String userMessage) {
		try {
			return ApiResult.success(gson.fromJson(body, clazz));
		} catch (JsonSyntaxException e) {
			log.log(Level.WARNING, "error_type=PARSE message=JSON invalido", e);
			return ApiResult.error(ApiErrorType.PARSE, null, userMessage, e.getMessage(), e);
		}
	}

	private static <T> ApiResult<T> parseJson(String body, Type type, String userMessage) {
		try {
			return ApiResult.success(gson.fromJson(body, type));
		} catch (JsonSyntaxException e) {
			log.log(Level.WARNING, "error_type=PARSE message=JSON invalido", e);
			return ApiResult.error(ApiErrorType.PARSE, null, userMessage, e.getMessage(), e);
		}
	}

	private static <T> ApiResult<T> mapHttpStatus(int statusCode, String userMessage, String technicalMessage) {
		if (statusCode >= 400 && statusCode <= 499) {
			String msg = statusCode == 401 || statusCode == 403 ? "Credenciales inválidas o sin permisos." : userMessage;
			if (statusCode == 404) {
				msg = "Recurso no encontrado.";
			}
			return ApiResult.error(ApiErrorType.HTTP_4XX, statusCode, msg, technicalMessage, null);
		}
		if (statusCode >= 500) {
			return ApiResult.error(ApiErrorType.HTTP_5XX, statusCode,
					"Error interno del servidor. Inténtalo más tarde.", technicalMessage, null);
		}
		return ApiResult.error(ApiErrorType.UNKNOWN, statusCode, "Respuesta inesperada del servidor.", technicalMessage,
				null);
	}

	private static ApiResult<Void> fromStatusWithoutBody(int statusCode, String successMessage, String failMessage) {
		if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
			return ApiResult.success(null);
		}
		return mapHttpStatus(statusCode, failMessage, failMessage + " status=" + statusCode);
	}

	private static <T> ApiResult<T> propagate(ApiResult<?> source) {
		return ApiResult.error(source.getErrorType(), source.getStatusCode(), source.getUserMessage(),
				source.getTechnicalMessage(), source.getException());
	}

	private static class RegistroRequest {
		String usuario;
		String nombre;
		String password;

		RegistroRequest(String usuario, String nombre, String password) {
			this.usuario = usuario;
			this.nombre = nombre;
			this.password = password;
		}
	}

	private static class LoginRequest {
		String usuario;
		String password;

		LoginRequest(String usuario, String password) {
			this.usuario = usuario;
			this.password = password;
		}
	}

	private static class ChangePasswordRequest {
		String usuario;
		String oldPassword;
		String newPassword;

		ChangePasswordRequest(String usuario, String oldPassword, String newPassword) {
			this.usuario = usuario;
			this.oldPassword = oldPassword;
			this.newPassword = newPassword;
		}
	}

	private static void escribirCampo(PrintWriter writer, String boundary, String nombre, String valor) {
		String lineFeed = "\r\n";
		writer.append("--").append(boundary).append(lineFeed);
		writer.append("Content-Disposition: form-data; name=\"").append(nombre).append("\"").append(lineFeed);
		writer.append(lineFeed);
		writer.append(valor == null ? "" : valor).append(lineFeed);
		writer.flush();
	}
}
