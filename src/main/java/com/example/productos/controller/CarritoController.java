package com.example.productos.controller;

/* 
import com.example.productos.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    
    @Autowired
    private CarritoService carritoService;
    
    @PostMapping("/agregar")
    public ResponseEntity<String> agregarAlCarrito(@RequestParam String usuarioId, 
                                                   @RequestParam String codigoProducto, 
                                                   @RequestParam int cantidad) {
        try {
            carritoService.agregarProducto(usuarioId, codigoProducto, cantidad);
            return ResponseEntity.ok("Producto agregado al carrito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/{usuarioId}")
    public ResponseEntity<Map<String, Integer>> obtenerCarrito(@PathVariable String usuarioId) {
        Map<String, Integer> carrito = carritoService.obtenerCarrito(usuarioId);
        return ResponseEntity.ok(carrito);
    }
    
    @DeleteMapping("/{usuarioId}/producto/{codigoProducto}")
    public ResponseEntity<String> eliminarDelCarrito(@PathVariable String usuarioId, 
                                                     @PathVariable String codigoProducto) {
        carritoService.eliminarProducto(usuarioId, codigoProducto);
        return ResponseEntity.ok("Producto eliminado del carrito");
    }
    
    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<String> limpiarCarrito(@PathVariable String usuarioId) {
        carritoService.limpiarCarrito(usuarioId);
        return ResponseEntity.ok("Carrito limpiado");
    }
    
    @PutMapping("/{usuarioId}/producto/{codigoProducto}")
    public ResponseEntity<String> actualizarCantidad(@PathVariable String usuarioId,
                                                     @PathVariable String codigoProducto,
                                                     @RequestParam int cantidad) {
        carritoService.actualizarCantidad(usuarioId, codigoProducto, cantidad);
        return ResponseEntity.ok("Cantidad actualizada");
    }
}
*/