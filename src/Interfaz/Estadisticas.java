package Interfaz;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import Controller.ApiClient;
import Modelo.Reserva;
import Modelo.SesionAdmin;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
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
		setTop(header);

		TableView<ReservaUltimoMesRow> tablaUltimoMes = crearTablaReservasUltimoMes();
		TableView<ValoracionMediaRow> tablaValoraciones = crearTablaValoracionesMedias();

		contenedor = new VBox(14,
				new Label("Reservas del ultimo mes"),
				tablaUltimoMes,
				new Label("Valoraciones medias de reservas"),
				tablaValoraciones);
		contenedor.setPadding(new Insets(20));
		VBox.setVgrow(tablaUltimoMes, Priority.ALWAYS);
		VBox.setVgrow(tablaValoraciones, Priority.ALWAYS);

		setCenter(contenedor);

		cargarDatos(tablaUltimoMes, tablaValoraciones);
	}

	private void cargarDatos(TableView<ReservaUltimoMesRow> tablaUltimoMes, TableView<ValoracionMediaRow> tablaValoraciones) {
		Long adminId = SesionAdmin.getIdActual();
		if (adminId == null) {
			log.warning("No hay sesion activa para cargar estadisticas.");
			return;
		}

		List<Reserva> reservas = ApiClient.obtenerReservasPorAdmin(adminId);

		LocalDate haceUnMes = LocalDate.now().minusMonths(1);
		List<ReservaUltimoMesRow> reservasUltimoMes = reservas.stream()
				.filter(Objects::nonNull)
				.filter(r -> r.getEstado() != null && r.getEstado().equalsIgnoreCase("TERMINADA"))
				.filter(r -> {
					LocalDate fecha = parseFecha(r.getFechaCreacion());
					return fecha != null && !fecha.isBefore(haceUnMes);
				})
				.map(r -> new ReservaUltimoMesRow(
						r.getId(),
						r.getProducto() != null ? r.getProducto().getNombre() : "Sin producto",
						r.getUsuario() != null ? r.getUsuario().getUsuario() : "Sin usuario",
						r.getFechaInicio(),
						r.getFechaFin(),
						r.getEstado()))
				.toList();

		Map<String, List<Reserva>> reservasPorProducto = reservas.stream()
				.filter(Objects::nonNull)
				.filter(r -> r.getProducto() != null)
				.collect(Collectors.groupingBy(r -> r.getProducto().getNombre() != null ? r.getProducto().getNombre() : "Sin producto"));

		List<ValoracionMediaRow> valoracionesMedias = reservasPorProducto.entrySet().stream()
				.map(entry -> {
					String producto = entry.getKey();
					long totalReservas = entry.getValue().size();

					double media = entry.getValue().stream()
							.map(Reserva::getProducto)
							.filter(Objects::nonNull)
							.map(p -> p.getValoracionMedia())
							.filter(Objects::nonNull)
							.mapToDouble(Double::doubleValue)
							.average()
							.orElse(0.0);

					return new ValoracionMediaRow(producto, totalReservas, String.format("%.2f", media));
				})
				.sorted(Comparator.comparing(ValoracionMediaRow::getProducto))
				.toList();

		tablaUltimoMes.setItems(FXCollections.observableArrayList(reservasUltimoMes));
		tablaValoraciones.setItems(FXCollections.observableArrayList(valoracionesMedias));
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

		TableColumn<ReservaUltimoMesRow, String> colUsuario = new TableColumn<>("Usuario");
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
		return tabla;
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

	private LocalDate parseFecha(String fecha) {
		if (fecha == null || fecha.isBlank()) {
			return null;
		}

		List<DateTimeFormatter> formatos = List.of(
				DateTimeFormatter.ISO_LOCAL_DATE_TIME,
				DateTimeFormatter.ISO_LOCAL_DATE,
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		for (DateTimeFormatter formato : formatos) {
			try {
				if (formato == DateTimeFormatter.ISO_LOCAL_DATE) {
					return LocalDate.parse(fecha, formato);
				}
				return LocalDateTime.parse(fecha, formato).toLocalDate();
			} catch (DateTimeParseException e) {
				// pruebo el siguiente formato
			}
		}

		try {
			return OffsetDateTime.parse(fecha).toLocalDate();
		} catch (DateTimeParseException e) {
			log.warning("No se pudo parsear la fecha: " + fecha);
			return null;
		}
	}

}
