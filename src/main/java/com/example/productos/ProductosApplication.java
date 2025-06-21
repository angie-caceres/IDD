
package com.example.productos;

import org.springframework.boot.SpringApplication;
import com.example.productos.console.ConsolaPrincipal;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductosApplication.class, args);
        new ConsolaPrincipal().iniciar();

    }
}
