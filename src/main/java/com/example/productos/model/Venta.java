
package com.example.productos.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "ventas")
public class Venta {
    
    @Id
    private String id;
    private List<LineaVenta> lineasVenta = new ArrayList<>();
    private String medioPago;
    private Date fecha;
    private double total;
    
    public Venta() {
        this.fecha = new Date();
    }
    
    public Venta(String medioPago) {
        this.medioPago = medioPago;
        this.fecha = new Date();
    }
    
    public void agregarProducto(Producto producto, int cantidad) {
        lineasVenta.add(new LineaVenta(producto, cantidad));
    }
    
    public double calcularTotal() {
        return lineasVenta.stream().mapToDouble(LineaVenta::calcularSubtotal).sum();
    }
    
    public void finalizarVenta() {
        this.total = calcularTotal();
    }
    
    public String getId() {
        return id;
    }
    
    public List<LineaVenta> getLineasVenta() {
        return lineasVenta;
    }
    
    public String getMedioPago() {
        return medioPago;
    }
    
    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public double getTotal() {
        return total;
    }

	
}