package Interfaz;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import Controller.ApiClient;
import Modelo.Reserva;
import Modelo.SesionAdmin;
import Modelo.Valoracion;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Perfil extends BorderPane {
	
	private static final Logger log = Logger.getLogger(Perfil.class.getName());

	VBox contenedor;
	Header header;
	
	public Perfil() {
		
		log.info("Mostrando perfil del usuario.");
		
		header = new Header();
		header.btnPerfil.setOnAction(null);
		header.btnPerfil.setStyle(
			    "-fx-background-color: #31c533;" +
			    "-fx-text-fill: white;"
			);
		setTop(header);

		contenedor = new VBox();
		contenedor.setPadding(new Insets(24));

		HBox columnas = new HBox(24, crearColumnaInformacionPersonal(), crearColumnaSuscripcion(),
				crearColumnaValoraciones());
		columnas.setFillHeight(true);
		contenedor.getChildren().add(columnas);

		setCenter(contenedor);
	}

	private VBox crearColumnaInformacionPersonal() {
		Label titulo = new Label("Informacion personal");
		titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

		String nombre = SesionAdmin.getNombreActual() != null ? SesionAdmin.getNombreActual() : "Sin nombre";
		String usuario = SesionAdmin.getUsuarioActual() != null ? SesionAdmin.getUsuarioActual() : "Sin usuario";

		Label lblNombre = new Label("Nombre: " + nombre);
		Label lblUsuario = new Label("Usuario: " + usuario);

		VBox columna = new VBox(12, titulo, lblNombre, lblUsuario);
		columna.setPadding(new Insets(16));
		columna.setStyle("-fx-background-color: #f7f7f7; -fx-background-radius: 8;");
		HBox.setHgrow(columna, Priority.ALWAYS);
		columna.setPrefWidth(260);
		return columna;
	}

	private VBox crearColumnaSuscripcion() {
		Label titulo = new Label("Plan de suscripcion");
		titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

		Label nivel = new Label("Nivel basico");
		nivel.setStyle("-fx-font-weight: bold;");

		Label descripcion = new Label(
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat pariatur.");
		descripcion.setWrapText(true);

		VBox columna = new VBox(12, titulo, nivel, descripcion);
		columna.setPadding(new Insets(16));
		columna.setStyle("-fx-background-color: #f7f7f7; -fx-background-radius: 8;");
		HBox.setHgrow(columna, Priority.ALWAYS);
		columna.setPrefWidth(320);
		return columna;
	}

	private VBox crearColumnaValoraciones() {
		Label titulo = new Label("Ultimas valoraciones");
		titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

		VBox listaValoraciones = new VBox(10);
		Label lblCarga = new Label("Cargando valoraciones...");
		listaValoraciones.getChildren().add(lblCarga);

		Task<List<ValoracionConProducto>> cargaValoracionesTask = new Task<>() {
			@Override
			protected List<ValoracionConProducto> call() {
				Long adminId = SesionAdmin.getIdActual();
				List<Reserva> reservas = adminId != null ? ApiClient.obtenerReservasPorAdmin(adminId) : List.of();
				if (reservas == null) {
					reservas = List.of();
				}

				Map<Integer, String> nombreProductoPorId = reservas.stream().filter(Objects::nonNull)
						.filter(r -> r.getProducto() != null)
						.collect(Collectors.toMap(r -> r.getProducto().getId(),
								r -> r.getProducto().getNombre() != null ? r.getProducto().getNombre() : "Sin producto",
								(nombreA, nombreB) -> nombreA));

				return nombreProductoPorId.entrySet().stream().flatMap(entry -> {
					List<Valoracion> valoraciones = ApiClient.obtenerValoracionesPorProducto(entry.getKey());
					if (valoraciones == null) {
						valoraciones = List.of();
					}
					return valoraciones.stream().filter(Objects::nonNull)
							.map(valoracion -> new ValoracionConProducto(entry.getValue(), valoracion));
				}).sorted(Comparator.comparing((ValoracionConProducto v) -> v.valoracion().getIdValoracion()).reversed()).limit(4)
						.toList();
			}
		};

		cargaValoracionesTask.setOnSucceeded(evento -> {
			List<ValoracionConProducto> ultimasValoraciones = cargaValoracionesTask.getValue();
			Platform.runLater(() -> {
				listaValoraciones.getChildren().clear();
				if (ultimasValoraciones == null || ultimasValoraciones.isEmpty()) {
					listaValoraciones.getChildren().add(new Label("No hay valoraciones disponibles."));
					return;
				}

				ultimasValoraciones.forEach(valoracionConProducto -> listaValoraciones.getChildren()
						.add(crearTarjetaValoracion(valoracionConProducto.nombreProducto(),
								valoracionConProducto.valoracion())));
			});
		});

		cargaValoracionesTask.setOnFailed(evento -> {
			log.warning("No se pudieron cargar las valoraciones del perfil.");
			Platform.runLater(() -> {
				listaValoraciones.getChildren().clear();
				listaValoraciones.getChildren().add(new Label("No se pudieron cargar las valoraciones."));
			});
		});

		Thread hiloCarga = new Thread(cargaValoracionesTask);
		hiloCarga.setDaemon(true);
		hiloCarga.start();

		VBox columna = new VBox(12, titulo, listaValoraciones);
		columna.setPadding(new Insets(16));
		columna.setStyle("-fx-background-color: #f7f7f7; -fx-background-radius: 8;");
		HBox.setHgrow(columna, Priority.ALWAYS);
		columna.setPrefWidth(420);
		return columna;
	}

	private GridPane crearTarjetaValoracion(String nombreProducto, Valoracion valoracion) {
		double estrellas = valoracion != null ? valoracion.getEstrellas() : 0.0;
		String textoValoracion = valoracion != null && valoracion.getComentario() != null
				&& !valoracion.getComentario().isBlank() ? valoracion.getComentario() : "Sin texto de valoracion.";

		Label producto = new Label(nombreProducto);
		producto.setStyle("-fx-font-weight: bold;");

		Label rating = new Label(String.format("%.1f/5", estrellas));
		Label texto = new Label(textoValoracion);
		texto.setWrapText(true);

		Region separador = new Region();
		HBox.setHgrow(separador, Priority.ALWAYS);

		HBox cabecera = new HBox(8, producto, separador, rating);

		GridPane tarjeta = new GridPane();
		tarjeta.setVgap(8);
		tarjeta.add(cabecera, 0, 0);
		tarjeta.add(texto, 0, 1);
		tarjeta.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 6; -fx-padding: 10;");

		return tarjeta;
	}

	private static class ValoracionConProducto {
		private final String nombreProducto;
		private final Valoracion valoracion;

		ValoracionConProducto(String nombreProducto, Valoracion valoracion) {
			this.nombreProducto = nombreProducto;
			this.valoracion = valoracion;
		}

		String nombreProducto() {
			return nombreProducto;
		}

		Valoracion valoracion() {
			return valoracion;
		}
	}
}
