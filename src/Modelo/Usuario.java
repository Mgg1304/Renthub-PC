package Modelo;

public class Usuario {

	private Long id;

	private String nombre;

	private String usuario;

	private String contrasenya;

	public Usuario() {
	}

	public Usuario(String nombre, String usuario, String contrasenya) {
		this.nombre = nombre;
		this.usuario = usuario;
		this.contrasenya = contrasenya;
	}

	// getters y setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getContrasenya() {
		return contrasenya;
	}

	public void setContrasenya(String contrasenya) {
		this.contrasenya = contrasenya;
	}

}
