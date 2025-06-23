
// CAMBIOS EN CatalogoService.java

package com.example.productos.service;

import com.example.productos.model.Producto;
import com.example.productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
				.filter(p -> p.getCategoria() != null && p.getCategoria().trim().toLowerCase().equals(buscada))
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

	public boolean existeProductoPorCodigo(String codigo) {
		return productoRepository.findByCodigo(codigo).isPresent();
	}
	public boolean existeProductoEnRedis(String codigo) {
	    return redisTemplate.hasKey(codigo);
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

	public boolean eliminarProductoPorCodigo(String codigo) {
		// Verificar si el producto existe en MongoDB
		Optional<Producto> producto = productoRepository.findByCodigo(codigo);
		if (producto.isPresent()) {
			// Eliminar de MongoDB
			productoRepository.deleteByCodigo(codigo);

			// Eliminar de Redis
			redisTemplate.delete(codigo);

			return true; // Producto eliminado correctamente
		}
		return false; // Producto no encontrado
	}

	private void guardarProductoEnRedis(Producto producto) {
		Map<String, Object> productoHash = new HashMap<>();
		productoHash.put("nombre", producto.getNombre());
		productoHash.put("categoria", producto.getCategoria());
		productoHash.put("precioUnitario", String.valueOf(producto.getPrecioUnitario()));
		productoHash.put("cantidadStock", String.valueOf(producto.getCantidadStock()));

		redisTemplate.opsForHash().putAll(producto.getCodigo(), productoHash);
	}

	public List<Producto> obtenerProductosDesdeRedis() {
		List<Producto> productos = new ArrayList<>();
		try {
			Set<String> claves = redisTemplate.keys("*");
			if (claves == null)
				return productos;

			for (String clave : claves) {
				Map<Object, Object> datos = redisTemplate.opsForHash().entries(clave);

				if (datos.containsKey("nombre") && datos.containsKey("precioUnitario") && datos.containsKey("categoria")
						&& datos.containsKey("cantidadStock")) {

					String nombre = datos.get("nombre").toString();
					String categoria = datos.get("categoria").toString();
					double precio = Double.parseDouble(datos.get("precioUnitario").toString());
					int stock = Integer.parseInt(datos.get("cantidadStock").toString());

					Producto p = new Producto();
					p.setCodigo(clave);
					p.setNombre(nombre);
					p.setCategoria(categoria);
					p.setPrecioUnitario(precio);
					p.setCantidadStock(stock);

					productos.add(p);
				}
			}
		} catch (Exception e) {
			System.err.println("Error al obtener productos desde Redis: " + e.getMessage());
		}

		return productos;
	}

}
