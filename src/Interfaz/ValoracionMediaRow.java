package Interfaz;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ValoracionMediaRow {
	private final SimpleStringProperty producto;
	private final SimpleIntegerProperty totalReservas;
	private final SimpleStringProperty media;

	public ValoracionMediaRow(String producto, long totalReservas, String media) {
		this.producto = new SimpleStringProperty(valorSeguro(producto));
		this.totalReservas = new SimpleIntegerProperty((int) totalReservas);
		this.media = new SimpleStringProperty(valorSeguro(media));
	}

	public String getProducto() {
		return producto.get();
	}

	public SimpleStringProperty productoProperty() {
		return producto;
	}

	public SimpleIntegerProperty totalReservasProperty() {
		return totalReservas;
	}

	public SimpleStringProperty mediaProperty() {
		return media;
	}

	private static String valorSeguro(String texto) {
		return texto == null || texto.isBlank() ? "-" : texto;
	}
}
