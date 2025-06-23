package com.example.productos.repository;

import com.example.productos.model.Venta;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface VentaRepository extends MongoRepository<Venta, String> {
    
	List<Venta> findByEmailVendedor(String emailVendedor);
 // permite buscar ventas por email del vendedor
}
