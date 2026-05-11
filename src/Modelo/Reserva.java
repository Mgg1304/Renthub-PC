package Modelo;

public class Reserva {

	// Atributos
	private int id;
	private Usuario usuario;
	private Producto producto;
	private String fechaInicio;
	private String fechaFin;
	private String estado;
	private String fechaCreacion;

	// Constructor vacío
	public Reserva() {
	}

	// Constructor completo
	public Reserva(int id, Usuario usuario, Producto producto, String fechaInicio,
			String fechaFin, String estado, String fechaCreacion) {
		
		this.id = id;
		this.usuario = usuario;
		this.producto = producto;
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
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

	// Métodos
	@Override
	public String toString() {
		return "Reserva [id=" + id + 
				", usuario=" + usuario + 
				", producto=" + producto + 
				", fechaInicio=" + fechaInicio + 
				", fechaFin=" + fechaFin + 
				", estado=" + estado + 
				", fechaCreacion=" + fechaCreacion + "]";
	}
}