package Interfaz;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import Controller.ApiClient;
import Controller.ApiResult;
import Modelo.Producto;
import Modelo.Reserva;
import Modelo.SesionAdmin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class Estadisticas extends BorderPane {

	private static final Logger log = Logger.getLogger(Estadisticas.class.getName());

	private VBox contenedor;
	private Header header;

	public Estadisticas() {

		log.info("Mostrando estadísticas.");

		header = new Header();
		header.btnEstadisticas.setOnAction(null);
		header.btnEstadisticas.setStyle(
			    "-fx-background-color: #1179ff;" +
			    "-fx-text-fill: white;"
			);
		setTop(header);

		TableView<ReservaUltimoMesRow> tablaUltimoMes = crearTablaReservasUltimoMes();
		TableView<ValoracionMediaRow> tablaValoraciones = crearTablaValoracionesMedias();

		contenedor = new VBox(14, new Label("Reservas del ultimo mes"), tablaUltimoMes,
				new Label("Valoraciones medias de reservas"), tablaValoraciones);
		contenedor.setPadding(new Insets(20));
		VBox.setVgrow(tablaUltimoMes, Priority.ALWAYS);
		VBox.setVgrow(tablaValoraciones, Priority.ALWAYS);

		setCenter(contenedor);

		cargarDatos(tablaUltimoMes, tablaValoraciones);
	}

	private void cargarDatos(TableView<ReservaUltimoMesRow> tablaUltimoMes,
			TableView<ValoracionMediaRow> tablaValoraciones) {
		Long adminId = SesionAdmin.getIdActual();
		if (adminId == null) {
			log.warning("No hay sesion activa para cargar estadisticas.");
			return;
		}

		Task<EstadisticasData> cargaTask = new Task<>() {
			@Override
			protected EstadisticasData call() {
				ApiResult<List<Reserva>> reservasResult = ApiClient.obtenerReservasPorAdmin(adminId);
				List<Reserva> reservas = reservasResult.isOk() && reservasResult.getData() != null ? reservasResult.getData()
						: List.of();
				if (!reservasResult.isOk()) {
					log.warning("No se pudieron cargar reservas de estadisticas: " + reservasResult.getTechnicalMessage());
				}

				LocalDateTime haceUnMesConMargen = LocalDateTime.now().minusMonths(1);
				List<ReservaUltimoMesRow> reservasUltimoMes = reservas.stream().filter(Objects::nonNull)
						.filter(r -> esEstadoFinalizado(r.getEstado()))
						.filter(r -> {
							LocalDateTime fechaHora = obtenerFechaReferenciaReserva(r);
							return fechaHora != null && !fechaHora.isBefore(haceUnMesConMargen);
						}).map(r -> new ReservaUltimoMesRow(r.getId(),
								r.getProducto() != null ? r.getProducto().getNombre() : "Sin producto",
								r.getUsuario() != null
										? (r.getUsuario().getNombre() != null && !r.getUsuario().getNombre().isBlank()
												? r.getUsuario().getNombre()
												: r.getUsuario().getUsuario())
										: "Sin usuario",
								r.getFechaInicio(),
								r.getFechaFin(), r.getEstado())).toList();

				Map<String, List<Reserva>> reservasPorProducto = reservas.stream().filter(Objects::nonNull)
						.filter(r -> r.getProducto() != null)
						.collect(Collectors.groupingBy(
								r -> r.getProducto().getNombre() != null ? r.getProducto().getNombre() : "Sin producto"));

				Map<Integer, Double> valoracionMediaPorProductoId = new HashMap<>();
				Set<Integer> productosSinValoracion = reservas.stream().filter(Objects::nonNull)
						.map(Reserva::getProducto).filter(Objects::nonNull)
						.filter(p -> p.getValoracionMedia() == null || p.getValoracionMedia() == 0.0)
						.map(Producto::getId).collect(Collectors.toSet());

				Map<Integer, Double> valoracionesCargadas = new ConcurrentHashMap<>();
				List<CompletableFuture<Void>> futures = productosSinValoracion.stream()
						.map(productoId -> CompletableFuture.runAsync(() -> {
							ApiResult<Producto> productoResult = ApiClient.obtenerProducto(productoId);
							if (productoResult.isOk() && productoResult.getData() != null) {
								valoracionesCargadas.put(productoId, productoResult.getData().getValoracionMedia());
							} else {
								log.warning("No se pudo obtener producto " + productoId + ": "
										+ productoResult.getTechnicalMessage());
							}
						}, AsyncExecutor.io())).toList();
				CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
				valoracionMediaPorProductoId.putAll(valoracionesCargadas);

				List<ValoracionMediaRow> valoracionesMedias = reservasPorProducto.entrySet().stream().map(entry -> {
					String producto = entry.getKey();
					long totalReservas = entry.getValue().size();

					double media = entry.getValue().stream().map(Reserva::getProducto).filter(Objects::nonNull)
							.map(p -> obtenerValoracionMediaProducto(p.getId(), p.getValoracionMedia(),
									valoracionMediaPorProductoId))
							.filter(Objects::nonNull).mapToDouble(Double::doubleValue).average().orElse(0.0);

					return new ValoracionMediaRow(producto, totalReservas, String.format("%.2f", media));
				}).sorted(Comparator.comparing(ValoracionMediaRow::getProducto)).toList();

				return new EstadisticasData(reservasUltimoMes, valoracionesMedias);
			}
		};

		cargaTask.setOnSucceeded(evento -> {
			EstadisticasData data = cargaTask.getValue();
			tablaUltimoMes.setItems(FXCollections.observableArrayList(data.reservasUltimoMes()));
			tablaValoraciones.setItems(FXCollections.observableArrayList(data.valoracionesMedias()));
		});

		cargaTask.setOnFailed(evento -> {
			log.warning("No se pudieron cargar las estadisticas.");
			tablaUltimoMes.setItems(FXCollections.observableArrayList());
			tablaValoraciones.setItems(FXCollections.observableArrayList());
		});

		AsyncExecutor.io().submit(cargaTask);
	}

	private static class EstadisticasData {
		private final List<ReservaUltimoMesRow> reservasUltimoMes;
		private final List<ValoracionMediaRow> valoracionesMedias;

		private EstadisticasData(List<ReservaUltimoMesRow> reservasUltimoMes,
				List<ValoracionMediaRow> valoracionesMedias) {
			this.reservasUltimoMes = reservasUltimoMes;
			this.valoracionesMedias = valoracionesMedias;
		}

		private List<ReservaUltimoMesRow> reservasUltimoMes() {
			return reservasUltimoMes;
		}

		private List<ValoracionMediaRow> valoracionesMedias() {
			return valoracionesMedias;
		}
	}

	private TableView<ReservaUltimoMesRow> crearTablaReservasUltimoMes() {
		TableView<ReservaUltimoMesRow> tabla = new TableView<>();
		tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tabla.setMaxWidth(Double.MAX_VALUE);

		TableColumn<ReservaUltimoMesRow, Number> colId = new TableColumn<>("ID");
		colId.setCellValueFactory(data -> data.getValue().idProperty());
		colId.setMaxWidth(1f * Integer.MAX_VALUE * 8);

		TableColumn<ReservaUltimoMesRow, String> colProducto = new TableColumn<>("Producto");
		colProducto.setCellValueFactory(data -> data.getValue().productoProperty());
		colProducto.setMaxWidth(1f * Integer.MAX_VALUE * 20);

		TableColumn<ReservaUltimoMesRow, String> colUsuario = new TableColumn<>("Nombre de usuario");
		colUsuario.setCellValueFactory(data -> data.getValue().usuarioProperty());
		colUsuario.setMaxWidth(1f * Integer.MAX_VALUE * 18);

		TableColumn<ReservaUltimoMesRow, String> colInicio = new TableColumn<>("Fecha inicio");
		colInicio.setCellValueFactory(data -> data.getValue().fechaInicioProperty());
		colInicio.setMaxWidth(1f * Integer.MAX_VALUE * 18);

		TableColumn<ReservaUltimoMesRow, String> colFin = new TableColumn<>("Fecha fin");
		colFin.setCellValueFactory(data -> data.getValue().fechaFinProperty());
		colFin.setMaxWidth(1f * Integer.MAX_VALUE * 18);

		TableColumn<ReservaUltimoMesRow, String> colEstado = new TableColumn<>("Estado");
		colEstado.setCellValueFactory(data -> data.getValue().estadoProperty());
		colEstado.setMaxWidth(1f * Integer.MAX_VALUE * 18);

		tabla.getColumns().addAll(colId, colProducto, colUsuario, colInicio, colFin, colEstado);
		aplicarColorTitulosColumnas(tabla);
		return tabla;
	}

	private void aplicarColorTitulosColumnas(TableView<ReservaUltimoMesRow> tabla) {
		String azulSuave = "#6f9fcf";
		String verdeSuave = "#7dbf85";
		Map<String, String> colorPorTitulo = new HashMap<>();
		for (int i = 0; i < tabla.getColumns().size(); i++) {
			TableColumn<ReservaUltimoMesRow, ?> columna = tabla.getColumns().get(i);
			colorPorTitulo.put(columna.getText(), i % 2 == 0 ? azulSuave : verdeSuave);
		}

		Runnable aplicarEstilos = () -> {
			for (Node header : tabla.lookupAll(".column-header")) {
				Node label = header.lookup(".label");
				if (!(label instanceof Label etiqueta)) {
					continue;
				}
				String color = colorPorTitulo.get(etiqueta.getText());
				if (color == null) {
					continue;
				}
				header.setStyle("-fx-background-color: " + color + "; -fx-border-color: #ffffff; -fx-border-width: 0 1 0 0;");
				etiqueta.setStyle("-fx-text-fill: white; -fx-font-weight: 700;");
			}
		};

		tabla.skinProperty().addListener((obs, anterior, actual) -> Platform.runLater(aplicarEstilos));
		Platform.runLater(aplicarEstilos);
	}

	private TableView<ValoracionMediaRow> crearTablaValoracionesMedias() {
		TableView<ValoracionMediaRow> tabla = new TableView<>();
		tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tabla.setMaxWidth(Double.MAX_VALUE);

		TableColumn<ValoracionMediaRow, String> colProducto = new TableColumn<>("Producto");
		colProducto.setCellValueFactory(data -> data.getValue().productoProperty());
		colProducto.setMaxWidth(1f * Integer.MAX_VALUE * 45);

		TableColumn<ValoracionMediaRow, Number> colTotal = new TableColumn<>("Total reservas");
		colTotal.setCellValueFactory(data -> data.getValue().totalReservasProperty());
		colTotal.setMaxWidth(1f * Integer.MAX_VALUE * 25);

		TableColumn<ValoracionMediaRow, String> colMedia = new TableColumn<>("Valoracion media");
		colMedia.setCellValueFactory(data -> data.getValue().mediaProperty());
		colMedia.setMaxWidth(1f * Integer.MAX_VALUE * 30);

		tabla.getColumns().addAll(colProducto, colTotal, colMedia);
		return tabla;
	}

	private LocalDateTime parseFechaHora(String fecha) {
		if (fecha == null || fecha.isBlank()) {
			return null;
		}

		List<DateTimeFormatter> formatosFechaHora = List.of(DateTimeFormatter.ISO_LOCAL_DATE_TIME,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		for (DateTimeFormatter formato : formatosFechaHora) {
			try {
				return LocalDateTime.parse(fecha, formato);
			} catch (DateTimeParseException e) {
				// pruebo el siguiente formato
			}
		}

		try {
			return LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		} catch (DateTimeParseException e) {
			// pruebo con offset
		}

		try {
			return OffsetDateTime.parse(fecha).toLocalDateTime();
		} catch (DateTimeParseException e) {
			log.warning("No se pudo parsear la fecha: " + fecha);
			return null;
		}
	}

	private boolean esEstadoFinalizado(String estado) {
		if (estado == null || estado.isBlank()) {
			return false;
		}
		String estadoNormalizado = estado.trim();
		return estadoNormalizado.equalsIgnoreCase("FINALIZADA")
				|| estadoNormalizado.equalsIgnoreCase("TERMINADA");
	}

	private LocalDateTime obtenerFechaReferenciaReserva(Reserva reserva) {
		LocalDateTime fechaFin = parseFechaHora(reserva.getFechaFin());
		if (fechaFin != null) {
			return fechaFin;
		}
		return parseFechaHora(reserva.getFechaCreacion());
	}

	private Double obtenerValoracionMediaProducto(int productoId, Double valoracionEnReserva,
			Map<Integer, Double> cacheValoraciones) {
		if (cacheValoraciones.containsKey(productoId)) {
			return cacheValoraciones.get(productoId);
		}

		Double valoracion = valoracionEnReserva;

		cacheValoraciones.put(productoId, valoracion);
		return valoracion;
	}

}
