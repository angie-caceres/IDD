package com.example.productos.controller;

import com.example.productos.model.Producto;
import com.example.productos.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.List; 
@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    
    @Autowired
    private CatalogoService catalogoService;
    
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        List<Producto> productos = catalogoService.listarProductos();
        return ResponseEntity.ok(productos);
    }
    
    @GetMapping("/{codigo}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable String codigo) {
        Producto producto = catalogoService.obtenerProductoPorCodigo(codigo);
        if (producto != null) {
            return ResponseEntity.ok(producto);
        }
        return ResponseEntity.notFound().build();
    }

	/*@GetMapping("/categoria")
	public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@RequestParam String categoria) {
		List<Producto> productos = catalogoService.obtenerProductosPorCategoria(categoria);
		if (productos.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(productos);
	}*/
    @GetMapping("/categoria")
    public List<Producto> obtenerProductosPorCategoria(@RequestParam String categoria) {
        return catalogoService.obtenerProductosPorCategoria(categoria);
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto productoGuardado = catalogoService.guardarProducto(producto);
        return ResponseEntity.ok(productoGuardado);
    }
    
    @PutMapping("/{codigo}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable String codigo, @RequestBody Producto producto) {
        producto.setCodigo(codigo);
        Producto productoActualizado = catalogoService.actualizarProducto(producto);
        if (productoActualizado != null) {
            return ResponseEntity.ok(productoActualizado);
        }
        return ResponseEntity.notFound().build();
    }
}