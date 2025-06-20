package com.example.productos.repository;

import com.example.productos.model.Venta;

//import java.sql.Date;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VentaRepository extends MongoRepository<Venta, String> {
	// Aquí puedes definir métodos adicionales si es necesario, por ejemplo:
	//List<Venta> findByUsuarioId(String usuarioId);
	
	//List<Venta> findByFechaBetween(Date startDate, Date endDate);
	
}