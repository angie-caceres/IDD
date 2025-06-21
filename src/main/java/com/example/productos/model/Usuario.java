// 1. Modelo Usuario
package com.example.productos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.io.Serializable;
import java.util.Date;

@Document(collection = "usuarios")
public class Usuario implements Serializable {

    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    private String perfil; // "administrador" o "vendedor"
    private String nombre;
    private String contraseña;
    private Date fechaRegistro;
    private boolean activo;

    public Usuario() {
        this.fechaRegistro = new Date();
        this.activo = true;
    }

    public Usuario(String nombre, String email, String contraseña) {
        this();
        this.nombre = nombre;
        this.email = email;
        this.contraseña = contraseña;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}