package com.example.productos.config;

import com.example.productos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRedisInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Iniciando carga de usuarios en Redis...");
        
        // Limpiar usuarios existentes en Redis (opcional)
        usuarioService.limpiarUsuariosDeRedis();
        
        // Cargar todos los usuarios activos de MongoDB en Redis
        usuarioService.cargarTodosLosUsuariosEnRedis();
        
        System.out.println("Carga de usuarios en Redis completada.");
    }
}

