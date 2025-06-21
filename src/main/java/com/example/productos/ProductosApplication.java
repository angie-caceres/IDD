
package com.example.productos;

import com.example.productos.console.ConsolaPrincipal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ProductosApplication {

    public static void main(String[] args) {
        // Inicia el contexto de Spring Boot
        ApplicationContext context = SpringApplication.run(ProductosApplication.class, args);

        // Obtiene la consola desde el contexto y la inicia
        ConsolaPrincipal consola = context.getBean(ConsolaPrincipal.class);
        consola.iniciar();
    }
}

