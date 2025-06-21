package com.example.productos.dto;

public class UsuarioRegistroRequest {
    
    private String nombre;
    private String email;
    private String contraseña;
    private String perfil;

    public UsuarioRegistroRequest() {}

    public UsuarioRegistroRequest(String nombre, String email, String contraseña, String perfil) {
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
        this.perfil = perfil;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
}
