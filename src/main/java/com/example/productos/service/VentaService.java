
package com.example.productos.service;

import com.example.productos.model.Producto;
import com.example.productos.model.Venta;
import com.example.productos.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaService {
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private CatalogoService catalogoService;
    
    /* StockManager comentado - ahora verificamos stock directamente en el producto
    @Autowired
    private StockManager stockManager;
    */
    
    public Venta realizarVenta(String[] codigos, int[] cantidades, String medioPago) {
        Venta venta = new Venta(medioPago);
        
        for (int i = 0; i < codigos.length; i++) {
            String codigo = codigos[i];
            int cantidad = cantidades[i];
            
            Producto producto = catalogoService.obtenerProductoPorCodigo(codigo);
            
            // Verificación de stock ahora se hace directamente en el producto
            if (producto == null || producto.getCantidadStock() < cantidad) {
                throw new IllegalArgumentException("Producto no encontrado o sin stock suficiente: " + codigo);
            }
            
            venta.agregarProducto(producto, cantidad);
            
            // Actualizar stock directamente en el producto y guardarlo en MongoDB
            producto.actualizarStock(-cantidad);
            catalogoService.guardarProducto(producto);
            
            /* Código StockManager comentado
            if (producto == null || !stockManager.verificarStock(codigo, cantidad)) {
                throw new IllegalArgumentException("Producto no encontrado o sin stock suficiente: " + codigo);
            }
            
            venta.agregarProducto(producto, cantidad);
            stockManager.descontarStock(codigo, cantidad);
            */
        }
        
        venta.finalizarVenta();
        ventaRepository.save(venta);
        return venta;
    }
}