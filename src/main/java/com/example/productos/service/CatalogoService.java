/*package com.example.productos.service;

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
    
    
    
    public Producto obtenerProductoPorCodigo(String codigo) {
        
        
        // Ahora busca directamente en MongoDB
        return productoRepository.findByCodigo(codigo).orElse(null);
        
        
    }
    
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }
    
    public Producto guardarProducto(Producto producto) {
        Producto productoGuardado = productoRepository.save(producto);
        
        
        return productoGuardado;
    }
    
    public Producto actualizarProducto(Producto producto) {
        if (productoRepository.findByCodigo(producto.getCodigo()).isPresent()) {
            Producto productoActualizado = productoRepository.save(producto);
            
            
            
            return productoActualizado;
            
        }
        return null;
    }
}*/

// CAMBIOS EN CatalogoService.java

package com.example.productos.service;


import com.example.productos.model.Producto;
import com.example.productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

@Service
public class CatalogoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Producto obtenerProductoPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo).orElse(null);
    }

    
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        String buscada = categoria.trim().toLowerCase();
        
        return productoRepository.findAll().stream()
            .filter(p -> p.getCategoria() != null &&
                         p.getCategoria().trim().toLowerCase().equals(buscada))
            .toList();
    }



    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto guardarProducto(Producto producto) {
        Producto productoGuardado = productoRepository.save(producto);
        guardarProductoEnRedis(productoGuardado);
        return productoGuardado;
    }

    public Producto actualizarProducto(Producto producto) {
        Producto existente = productoRepository.findByCodigo(producto.getCodigo()).orElse(null);
        if (existente != null) {
            // Asegurar que no se cambie el ID
            producto.setId(existente.getId());
            Producto actualizado = productoRepository.save(producto);
            guardarProductoEnRedis(actualizado);
            return actualizado;
        }
        return null;
    }

    private void guardarProductoEnRedis(Producto producto) {
        Map<String, Object> productoHash = new HashMap<>();
        productoHash.put("nombre", producto.getNombre());
        productoHash.put("categoria", producto.getCategoria());
        productoHash.put("precioUnitario", String.valueOf(producto.getPrecioUnitario()));
        productoHash.put("cantidadStock", String.valueOf(producto.getCantidadStock()));

        redisTemplate.opsForHash().putAll(producto.getCodigo(), productoHash);
    }
}

