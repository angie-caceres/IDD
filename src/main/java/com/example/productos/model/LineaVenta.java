
package com.example.productos.model;

import java.io.Serializable;

public class LineaVenta implements Serializable {
    
    private Producto producto;
    private int cantidad;
    
    public LineaVenta() {}
    
    public LineaVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }
    
    public double calcularSubtotal() {
        return producto.getPrecioUnitario() * cantidad;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
