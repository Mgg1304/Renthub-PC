package Modelo;

public class Producto {

	// Atributos
	private int id;
	private String nombre;
	private String descripcion;
	private String categoria;
	private double precioPorDia;
	private int stock;
	private Double valoracionMedia;

	// Constructor
	public Producto(int id, String nombre, String descripcion, String categoria, double precioPorDia, int stock) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.precioPorDia = precioPorDia;
		this.stock = stock;
		this.valoracionMedia = null;
	}

	public Producto(int id, String nombre, String descripcion, String categoria, double precioPorDia, int stock,
			Double valoracionMedia) {
		this.id = id;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.categoria = categoria;
		this.precioPorDia = precioPorDia;
		this.stock = stock;
		this.valoracionMedia = valoracionMedia;
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
		this.valoracionMedia = valoracionMedia;
	}

}
