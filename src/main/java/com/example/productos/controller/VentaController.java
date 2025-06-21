package com.example.productos.controller;

import com.example.productos.model.Venta;
import com.example.productos.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {
    
    @Autowired
    private VentaService ventaService;
    
    /**
     * Procesa una venta desde un carrito
     */
    @PostMapping("/procesar/{carritoId}")
    public ResponseEntity<?> procesarVenta(@PathVariable String carritoId,
                                         @RequestParam String medioPago) {
        try {
            Venta venta = ventaService.procesarVentaDesdeCarrito(carritoId, medioPago);
            return ResponseEntity.ok(venta); 
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error al procesar venta: " + e.getMessage());
        }
    }
    
    
   
    
    /**
     * Obtiene una venta específica por ID
     */
    @GetMapping("/{ventaId}")
    public ResponseEntity<Venta> obtenerVenta(@PathVariable String ventaId) {
        Venta venta = ventaService.obtenerVenta(ventaId);
        if (venta != null) {
            return ResponseEntity.ok(venta);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * Lista todas las ventas
     */
    @GetMapping
    public ResponseEntity<Iterable<Venta>> listarVentas() {
        Iterable<Venta> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }
}