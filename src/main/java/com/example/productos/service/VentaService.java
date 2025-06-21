

package com.example.productos.service;

import com.example.productos.model.Producto;
import com.example.productos.model.Venta;
import com.example.productos.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CatalogoService catalogoService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public Venta procesarVentaDesdeCarrito(String carritoId, String medioPago) {
        // Obtener el carrito crudo desde Redis (valores como Strings)
        Map<String, String> carritoRaw = carritoService.obtenerCarrito(carritoId);
        Map<String, Integer> productosCarrito = new HashMap<>();

        // Convertir los valores del carrito a Integer
        for (Map.Entry<String, String> entry : carritoRaw.entrySet()) {
            try {
                productosCarrito.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cantidad inválida para el producto " + entry.getKey());
            }
        }

        if (productosCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        if (!carritoService.verificarStockCarrito(carritoId)) {
            throw new RuntimeException("Stock insuficiente para algunos productos");
        }

        Venta venta = new Venta(medioPago);

        for (Map.Entry<String, Integer> item : productosCarrito.entrySet()) {
            String codigoProducto = item.getKey();
            int cantidad = item.getValue();

            Producto producto = catalogoService.obtenerProductoPorCodigo(codigoProducto);
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado: " + codigoProducto);
            }

            if (producto.getCantidadStock() < cantidad) {
                throw new RuntimeException("Stock insuficiente para: " + producto.getDescripcion());
            }

            producto.actualizarStock(-cantidad);
            catalogoService.actualizarProducto(producto);

            // ✅ CORREGIDO: guardar el stock como String en Redis
            redisTemplate.opsForHash().put(codigoProducto, "cantidadStock", String.valueOf(producto.getCantidadStock()));

            venta.agregarProducto(
            	    producto.getCodigo(),
            	    producto.getNombre(),
            	    producto.getPrecioUnitario(),
            	    cantidad
            	);

        }

        venta.finalizarVenta();
        Venta ventaGuardada = ventaRepository.save(venta);
        carritoService.limpiarCarrito(carritoId);

        return ventaGuardada;
    }

    public Venta obtenerVenta(String ventaId) {
        Optional<Venta> venta = ventaRepository.findById(ventaId);
        return venta.orElse(null);
    }

    public Iterable<Venta> listarVentas() {
        return ventaRepository.findAll();
    }
}


