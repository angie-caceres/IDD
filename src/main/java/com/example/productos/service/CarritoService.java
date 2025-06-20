package com.example.productos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.productos.model.Producto;

import java.util.Map;

@Service
public class CarritoService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 👇 Cambio importante: ahora los valores también son String
    private final HashOperations<String, String, String> hashOperations;

    private CatalogoService catalogoService;

    @Autowired
    public CarritoService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.catalogoService = new CatalogoService(); // Asegurate de usar @Autowired si es necesario
    }

    // Guarda el carrito como Strings (convierte los Integer a String)
    public void guardarCarrito(String carritoId, Map<String, Integer> productos) {
        productos.forEach((key, value) -> 
            hashOperations.put(carritoId, key, String.valueOf(value))
        );
    }

    // 👇 Ahora devuelve Map<String, String>
    public Map<String, String> obtenerCarrito(String carritoId) {
        return hashOperations.entries(carritoId);
    }

    public void eliminarProducto(String carritoId, String codigoProducto) {
        hashOperations.delete(carritoId, codigoProducto);
    }

    public void limpiarCarrito(String carritoId) {
        redisTemplate.delete(carritoId);
    }

    public void actualizarCantidad(String carritoId, String codigoProducto, int cantidad) {
        if (cantidad <= 0) {
            eliminarProducto(carritoId, codigoProducto);
        } else {
            hashOperations.put(carritoId, codigoProducto, String.valueOf(cantidad));
        }
    }

    @Transactional(readOnly = true)
    public boolean verificarStockCarrito(String carritoId) {
        Map<String, String> productosCarrito = obtenerCarrito(carritoId);

        for (Map.Entry<String, String> item : productosCarrito.entrySet()) {
            String codigoProducto = item.getKey();
            int cantidadSolicitada;
            try {
                cantidadSolicitada = Integer.parseInt(item.getValue());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cantidad inválida en el carrito para el producto " + codigoProducto);
            }

            // Consultar stock desde Redis (no MongoDB)
            Object stockObj = redisTemplate.opsForHash().get(codigoProducto, "cantidadStock");

            if (stockObj == null) {
                System.out.println("DEBUG: Producto no encontrado en Redis: " + codigoProducto);
                return false;
            }

            int stockDisponible = Integer.parseInt(stockObj.toString());

            if (stockDisponible < cantidadSolicitada) {
                return false;
            }
        }

        return true;
    }
}

    
    
    
    
    

    
    
 