package com.example.productos.dto;

import java.util.Map;

public class CarritoRequest {
    private String clienteId;
    private Map<String, Integer> productos;

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public Map<String, Integer> getProductos() {
        return productos;
    }

    public void setProductos(Map<String, Integer> productos) {
        this.productos = productos;
    }
}