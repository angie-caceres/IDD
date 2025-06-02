
package com.example.productos.service;

/* StockManager comentado - ahora el stock se maneja directamente en MongoDB
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockManager {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String PREFIX = "stock:";
    
    public void setStock(String codigo, int cantidad) {
        redisTemplate.opsForValue().set(PREFIX + codigo, String.valueOf(cantidad));
    }
    
    public boolean verificarStock(String codigo, int cantidadRequerida) {
        String value = redisTemplate.opsForValue().get(PREFIX + codigo);
        if (value == null) return false;
        int stockActual = Integer.parseInt(value);
        return stockActual >= cantidadRequerida;
    }
    
    public void descontarStock(String codigo, int cantidad) {
        redisTemplate.opsForValue().increment(PREFIX + codigo, -cantidad);
    }
    
    public int obtenerStock(String codigo) {
        String value = redisTemplate.opsForValue().get(PREFIX + codigo);
        return value == null ? 0 : Integer.parseInt(value);
    }
}
*/