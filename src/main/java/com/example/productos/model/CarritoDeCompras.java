package com.example.productos.model;

import java.util.Map;

public class CarritoDeCompras {
    private String id;
    private Map<String, Integer> productos; // clave: idProducto, valor: cantidad

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Integer> getProductos() {
        return productos;
    }

    public void setProductos(Map<String, Integer> productos) {
        this.productos = productos;
    }
}