

package com.example.productos.repository;
import org.springframework.data.mongodb.repository.Query;
import com.example.productos.model.Producto;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProductoRepository extends MongoRepository<Producto, String> {

    // Método para buscar un producto por su código (ya existente)
    Optional<Producto> findByCodigo(String codigo);

    // Método para listar todos los productos (ya proporcionado por MongoRepository)
    // List<Producto> findAll();

    // Método para guardar un producto nuevo validando que no se duplique el ID
    default Producto saveIfNotExists(Producto producto) {
        if (existsById(producto.getId())) {
            throw new IllegalArgumentException("El ID del producto ya existe: " + producto.getId());
        }
        return save(producto);
    }


    //List<Producto> findByCategoria(String categoria);

	List<Producto> findByCategoriaIgnoreCase(String categoria);

	@Query(value = "{ 'codigo': ?0 }", delete = true)
    void deleteByCodigo(String codigo);

    // Método para actualizar un producto por su ID (ya proporcionado por MongoRepository)
    // Producto save(Producto producto);

}