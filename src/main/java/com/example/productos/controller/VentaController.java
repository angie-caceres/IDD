
package com.example.productos.controller;

import com.example.productos.model.Venta;
import com.example.productos.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class VentaController {
    
    @Autowired
    private VentaService ventaService;
    
    @PostMapping("/venta")
    public ResponseEntity<Venta> realizarVenta(@RequestBody VentaRequest request) {
        try {
            Venta venta = ventaService.realizarVenta(
                request.getCodigos(),
                request.getCantidades(),
                request.getMedioPago()
            );
            return ResponseEntity.ok(venta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    public static class VentaRequest {
        private String[] codigos;
        private int[] cantidades;
        private String medioPago;
        
        public String[] getCodigos() {
            return codigos;
        }
        
        public void setCodigos(String[] codigos) {
            this.codigos = codigos;
        }
        
        public int[] getCantidades() {
            return cantidades;
        }
        
        public void setCantidades(int[] cantidades) {
            this.cantidades = cantidades;
        }
        
        public String getMedioPago() {
            return medioPago;
        }
        
        public void setMedioPago(String medioPago) {
            this.medioPago = medioPago;
        }
    }
}
