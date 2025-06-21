package com.example.productos.model;

import java.io.Serializable;

public class LineaVenta implements Serializable {

    private String codigoProducto;
    private String nombreProducto;
    private double precioUnitario;
    private int cantidad;

    public LineaVenta() {}

    public LineaVenta(String codigoProducto, String nombreProducto, double precioUnitario, int cantidad) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    public double calcularSubtotal() {
        return precioUnitario * cantidad;
    }

    // Getters y setters
    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
