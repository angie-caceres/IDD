package com.example.productos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.productos.dto.CarritoRequest;
import com.example.productos.service.CarritoService;

import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    
    @Autowired
    private CarritoService carritoService;
    
    @PostMapping("/agregar")
    public ResponseEntity<String> crearCarrito(@RequestBody CarritoRequest request) {
        carritoService.guardarCarrito(request.getClienteId(), request.getProductos());
        return ResponseEntity.ok("Carrito creado correctamente");
    }
    
    @GetMapping("/{clienteId}")
    public ResponseEntity<Map<String, String>> obtenerCarrito(@PathVariable String clienteId) {
        Map<String, String> carrito = carritoService.obtenerCarrito(clienteId);
        return ResponseEntity.ok(carrito);
    }

    @DeleteMapping("/{clienteId}/producto/{codigoProducto}")
    public ResponseEntity<String> eliminarDelCarrito(@PathVariable String clienteId,
                                                     @PathVariable String codigoProducto) {
        carritoService.eliminarProducto(clienteId, codigoProducto);
        return ResponseEntity.ok("Producto eliminado del carrito");
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<String> limpiarCarrito(@PathVariable String clienteId) {
        carritoService.limpiarCarrito(clienteId);
        return ResponseEntity.ok("Carrito limpiado");
    }
}
