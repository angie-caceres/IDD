package com.example.productos.service;

import com.example.productos.model.Producto;
import com.example.productos.repository.ProductoRepository;
// import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
// import java.util.concurrent.TimeUnit;

@Service
public class CatalogoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    /* Redis comentado - antes se usaba para cachear productos
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String PREFIX = "producto:";
    */
    
    public Producto obtenerProductoPorCodigo(String codigo) {
        /* Código Redis comentado - antes buscaba en cache primero
        String key = PREFIX + codigo;
        String json = redisTemplate.opsForValue().get(key);
        
        if (json != null) {
            return new Gson().fromJson(json, Producto.class);
        }
        */
        
        // Ahora busca directamente en MongoDB
        return productoRepository.findByCodigo(codigo).orElse(null);
        
        /* Código Redis comentado - antes guardaba en cache
        if (producto != null) {
            redisTemplate.opsForValue().set(key, new Gson().toJson(producto), 10, TimeUnit.MINUTES);
        }
        return producto;
        */
    }
    
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }
    
    public Producto guardarProducto(Producto producto) {
        Producto productoGuardado = productoRepository.save(producto);
        
        /* Código Redis comentado - antes actualizaba cache
        redisTemplate.opsForValue().set(PREFIX + producto.getCodigo(), 
                                       new Gson().toJson(productoGuardado), 
                                       10, TimeUnit.MINUTES);
        */
        
        return productoGuardado;
    }
    
    public Producto actualizarProducto(Producto producto) {
        if (productoRepository.findByCodigo(producto.getCodigo()).isPresent()) {
            Producto productoActualizado = productoRepository.save(producto);
            
            /* Código Redis comentado - antes actualizaba cache
            redisTemplate.opsForValue().set(PREFIX + producto.getCodigo(), 
                                           new Gson().toJson(productoActualizado), 
                                           10, TimeUnit.MINUTES);
            */
            
            return productoActualizado;
        }
        return null;
    }
}
