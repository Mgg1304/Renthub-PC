package Modelo;

public class Reserva {
	
	// Atributos
	private int id;
	private int id_usuario;
	private int id_producto;
	private String fechaInicio;
	private String fechaFin;
	private String estado;
	private String fechaCreacion;

	// Constructor
	public Reserva(int id, int id_usuario, int id_producto, String fechaInicio, String fechaFin,
			String estado, String fechaCreacion) {
		this.id = id;
		this.id_usuario = id_usuario;
		this.id_producto = id_producto;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.estado = estado;
		this.fechaCreacion = fechaCreacion;
	}

	// Getters y Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUsuario() {
		return id_usuario;
	}

	public void setUsuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}

	public int getIdProducto() {
		return id_producto;
	}

	public void setIdProducto(int id_producto) {
		this.id_producto = id_producto;
	}

	public String getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public String getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	
	//Metodos
	@Override
	public String toString() {
		return "Reserva [id=" + id + ", usuario=" + id_usuario + ", producto=" + id_producto
				+ ", fechaInicio=" + fechaInicio + ", fechaFin=" + fechaFin + ", estado=" + estado
				+ ", fechaCreacion=" + fechaCreacion + "]";
	}
	
	
}
