
/*package com.example.productos.config;

import com.example.productos.model.Producto;
import com.example.productos.repository.ProductoRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RedisDataLoader {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        limpiarYRecargarProductosEnRedis();
    }

    private void limpiarYRecargarProductosEnRedis() {
        // Eliminar todos los productos existentes en Redis
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        // Cargar los productos desde la base de datos
        List<Producto> productos = productoRepository.findAll();
        for (Producto producto : productos) {
            Map<String, Object> productoHash = new HashMap<>();
            productoHash.put("nombre", producto.getNombre());
            productoHash.put("precioUnitario", producto.getPrecioUnitario());
            productoHash.put("cantidadStock", producto.getCantidadStock());

            // Usar el código del producto como clave
            redisTemplate.opsForHash().putAll(producto.getCodigo(), productoHash);
        }
        System.out.println("Productos eliminados y recargados en Redis.");
    }
}
// Este código inicializa los datos de productos en Redis al iniciar la aplicación,
*/



package com.example.productos.config;

import com.example.productos.model.Producto;
import com.example.productos.repository.ProductoRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RedisDataLoader {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void init() {
        limpiarYRecargarProductosEnRedis();
    }

    private void limpiarYRecargarProductosEnRedis() {
        // Eliminar todos los productos existentes en Redis
        redisTemplate.getConnectionFactory().getConnection().flushDb();

        // Cargar los productos desde la base de datos
        List<Producto> productos = productoRepository.findAll();
        for (Producto producto : productos) {
            Map<String, Object> productoHash = new HashMap<>();
            productoHash.put("nombre", producto.getNombre());

            // ✅ Convertir valores numéricos a texto
            productoHash.put("precioUnitario", String.valueOf(producto.getPrecioUnitario()));
            productoHash.put("cantidadStock", String.valueOf(producto.getCantidadStock()));

            // Usar el código del producto como clave
            redisTemplate.opsForHash().putAll(producto.getCodigo(), productoHash);
        }

        System.out.println("Productos eliminados y recargados en Redis.");
    }
}
