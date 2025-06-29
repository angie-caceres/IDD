
package com.example.productos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

@Document(collection = "productos")
public class Producto implements Serializable {
    
    @Id
    private String id;
    private String codigo;
    private String descripcion;
    private double precioUnitario;
    private int cantidadStock;
    private int stockMinimo;
    
    public Producto() {}
    
    public Producto(String codigo, String descripcion, double precioUnitario, int cantidadStock, int stockMinimo) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.cantidadStock = cantidadStock;
        this.stockMinimo = stockMinimo;
    }
    
    public boolean esBajoStock() {
        return this.cantidadStock <= this.stockMinimo;
    }
    
    public void actualizarStock(int cantidad) {
        this.cantidadStock += cantidad;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public double getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public int getCantidadStock() {
        return cantidadStock;
    }
    
    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }
    
    public int getStockMinimo() {
        return stockMinimo;
    }
    
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}