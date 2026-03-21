package Modelo;

public class LoginResponse {

	private Long id;
	private String usuario;
	private String nombre;

	public LoginResponse(Long id, String usuario, String nombre) {
		this.id = id;
		this.usuario = usuario;
		this.nombre = nombre;
	}

	public Long getId() {
		return id;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getNombre() {
		return nombre;
	}
}
