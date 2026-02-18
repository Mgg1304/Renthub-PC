package Modelo;

public class Valoracion {

	// Atributos
	private int idValoracion;
	private int id_producto;
	private int id_usuario;
	private String comentario;
	private double estrellas;
	
	// Constructor
	public Valoracion(int idValoracion, int id_producto, int id_usuario, String comentario, double estrellas) {
		this.idValoracion = idValoracion;
		this.id_producto = id_producto;
		this.id_usuario = id_usuario;
		this.comentario = comentario;
		this.estrellas = estrellas;
	}

	// Getters y Setters
	public int getIdValoracion() {
		return idValoracion;
	}

	public void setIdValoracion(int idValoracion) {
		this.idValoracion = idValoracion;
	}

	public int getProducto() {
		return id_producto;
	}

	public void setProducto(int id_producto) {
		this.id_producto = id_producto;
	}

	public int getUsuario() {
		return id_usuario;
	}

	public void setUsuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public double getEstrellas() {
		return estrellas;
	}

	public void setEstrellas(double estrellas) {
		this.estrellas = estrellas;
	}
	
	
}
