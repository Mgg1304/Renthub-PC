package Interfaz;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ReservaUltimoMesRow {
	private final SimpleIntegerProperty id;
	private final SimpleStringProperty producto;
	private final SimpleStringProperty usuario;
	private final SimpleStringProperty fechaInicio;
	private final SimpleStringProperty fechaFin;
	private final SimpleStringProperty estado;

	public ReservaUltimoMesRow(int id, String producto, String usuario, String fechaInicio, String fechaFin, String estado) {
		this.id = new SimpleIntegerProperty(id);
		this.producto = new SimpleStringProperty(valorSeguro(producto));
		this.usuario = new SimpleStringProperty(valorSeguro(usuario));
		this.fechaInicio = new SimpleStringProperty(valorSeguro(fechaInicio));
		this.fechaFin = new SimpleStringProperty(valorSeguro(fechaFin));
		this.estado = new SimpleStringProperty(valorSeguro(estado));
	}

	public SimpleIntegerProperty idProperty() {
		return id;
	}

	public SimpleStringProperty productoProperty() {
		return producto;
	}

	public SimpleStringProperty usuarioProperty() {
		return usuario;
	}

	public SimpleStringProperty fechaInicioProperty() {
		return fechaInicio;
	}

	public SimpleStringProperty fechaFinProperty() {
		return fechaFin;
	}

	public SimpleStringProperty estadoProperty() {
		return estado;
	}

	private static String valorSeguro(String texto) {
		return texto == null || texto.isBlank() ? "-" : texto;
	}
}
