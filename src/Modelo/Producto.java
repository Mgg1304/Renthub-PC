package Modelo;

import java.util.logging.Logger;

import com.google.gson.annotations.SerializedName;

public class Producto {

	private static final Logger log = Logger.getLogger(Producto.class.getName());

	// Atributos
	@SerializedName("idProducto")
	private int id;

	private String nombre;

	private String descripcion;

	private String categoria;

	@SerializedName("precioDia")
	private double precioPorDia;

	private int stock;

	@SerializedName(value = "ratingAvg")
	private double valoracionMedia;

	// Constructor
	public Producto(int id, String nombre, String descripcion, String categoria, double precioPorDia, int stock) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.precioPorDia = precioPorDia;
		this.stock = stock;
		this.valoracionMedia = 0.0;
		log.info("Producto creado: " + this);
	}

	public Producto(int id, String nombre, String descripcion, String categoria, double precioPorDia, int stock,
			double valoracionMedia) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.precioPorDia = precioPorDia;
		this.stock = stock;
		this.valoracionMedia = valoracionMedia;
		log.info("Producto creado con valoración: " + this);
	}

	// Getters y Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public double getPrecioPorDia() {
		return precioPorDia;
	}

	public void setPrecioPorDia(double precioPorDia) {
		this.precioPorDia = precioPorDia;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	

	public Double getValoracionMedia() {
		return valoracionMedia;
	}

	public void setValoracionMedia(Double valoracionMedia) {
		this.valoracionMedia = valoracionMedia != null ? valoracionMedia : 0.0;
	}

	@Override
	public String toString() {
		return "Producto{" + "id=" + id + ", nombre='" + nombre + '\'' + ", precioPorDia=" + precioPorDia
				+ ", valoracionMedia=" + valoracionMedia + '}';
	}

}
