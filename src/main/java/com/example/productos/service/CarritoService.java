package com.example.productos.service;

/* Servicio para manejar el carrito de compras usando Redis
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CarritoService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String CARRITO_PREFIX = "carrito:";
    private static final int CARRITO_EXPIRATION_HOURS = 24;
    
    public void agregarProducto(String usuarioId, String codigoProducto, int cantidad) {
        String key = CARRITO_PREFIX + usuarioId;
        String field = codigoProducto;
        
        // Si el producto ya existe, suma la cantidad
        Object cantidadActualObj = redisTemplate.opsForHash().get(key, field);
        int nuevaCantidad = cantidad;
        
        if (cantidadActualObj != null) {
            nuevaCantidad += Integer.parseInt(cantidadActualObj.toString());
        }
        
        redisTemplate.opsForHash().put(key, field, String.valueOf(nuevaCantidad));
        redisTemplate.expire(key, CARRITO_EXPIRATION_HOURS, TimeUnit.HOURS);
    }
    
    public Map<String, Integer> obtenerCarrito(String usuarioId) {
        String key = CARRITO_PREFIX + usuarioId;
        Map<Object, Object> carritoRaw = redisTemplate.opsForHash().entries(key);
        
        Map<String, Integer> carrito = new HashMap<>();
        for (Map.Entry<Object, Object> entry : carritoRaw.entrySet()) {
            carrito.put(entry.getKey().toString(), Integer.parseInt(entry.getValue().toString()));
        }
        
        return carrito;
    }
    
    public void eliminarProducto(String usuarioId, String codigoProducto) {
        String key = CARRITO_PREFIX + usuarioId;
        redisTemplate.opsForHash().delete(key, codigoProducto);
    }
    
    public void limpiarCarrito(String usuarioId) {
        String key = CARRITO_PREFIX + usuarioId;
        redisTemplate.delete(key);
    }
    
    public void actualizarCantidad(String usuarioId, String codigoProducto, int cantidad) {
        String key = CARRITO_PREFIX + usuarioId;
        if (cantidad <= 0) {
            eliminarProducto(usuarioId, codigoProducto);
        } else {
            redisTemplate.opsForHash().put(key, codigoProducto, String.valueOf(cantidad));
            redisTemplate.expire(key, CARRITO_EXPIRATION_HOURS, TimeUnit.HOURS);
        }
    }
}
*/