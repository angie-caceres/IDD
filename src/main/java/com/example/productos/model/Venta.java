
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
    private String emailVendedor;
    private double total;
    
    public Venta() {
        this.fecha = new Date();
    }
    
    public Venta(String medioPago, String emailVendedor) {
        this.medioPago = medioPago;
        this.emailVendedor = emailVendedor;
        this.fecha = new Date();
    }
    
    public void agregarProducto(String codigo, String nombre, double precioUnitario, int cantidad) {
        LineaVenta linea = new LineaVenta(codigo, nombre, precioUnitario, cantidad);
        this.lineasVenta.add(linea);
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

	public String getVendedor() {
		// TODO Auto-generated method stub
		return emailVendedor;
	}

	
}