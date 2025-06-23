
package com.example.productos.console;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.productos.model.Producto;
import com.example.productos.model.Venta;
import com.example.productos.service.CatalogoService;
import com.example.productos.service.VentaService;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.productos.service.CarritoService;

@Component
public class ConsolaVendedor {

	@Autowired
	@Lazy
	private ConsolaPrincipal consolaPrincipal;

	@Autowired
	private CatalogoService catalogoService;

	@Autowired
	private CarritoService carritoService;

	@Autowired
	private VentaService ventaService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public void iniciar(String emailUsuario) {
		// String carritoId = emailUsuario;
		Scanner scanner = new Scanner(System.in);
		String opcion;

		do {
			System.out.println("\nSeleccione una opción:");
			System.out.println("1. Gestión de inventario");
			System.out.println("2. Generar venta");
			System.out.println("3. Ver historial de ventas");
			System.out.println("4. Ver catálogo de productos");
			System.out.println("5. Ver detalle de producto por código");
			System.out.println("6. Salir");
			System.out.print("Opción: ");
			opcion = scanner.nextLine();

			switch (opcion) {
			case "1":
				String opcionStock;
				do {
					System.out.println("\nGestión de inventario:");
					System.out.println("1. Actualizar producto");
					System.out.println("2. Agregar producto");
					System.out.println("3. Eliminar producto");
					System.out.println("4. Finalizar");
					System.out.print("Opción: ");
					opcionStock = scanner.nextLine();
///GESTION DE STOCK
					switch (opcionStock) {
///ACTUALIZAR PRODUCTO

					case "1":
						System.out.println("\nActualizar producto:");
						String codigoActualizar;
						Producto producto = null;

						// Validar que el código exista en Redis y obtener el producto desde Mongo
						do {
							System.out.print("Ingrese el código del producto a actualizar: ");
							codigoActualizar = scanner.nextLine().trim();

							if (!catalogoService.existeProductoEnRedis(codigoActualizar)) {
								System.out.println("El código no existe. Intente nuevamente.");
								continue;
							}

							producto = catalogoService.obtenerProductoPorCodigo(codigoActualizar);

							if (producto == null) {
								System.out
										.println("No se encontró el producto completo en MongoDB. Intente nuevamente.");
							}

						} while (producto == null);

						// Submenú para actualizar campos específicos
						String opcionActualizar;
						do {
							System.out.println("\nSeleccione el campo a actualizar:");
							System.out.println("1. Nombre");
							System.out.println("2. Descripción");
							System.out.println("3. Precio");
							System.out.println("4. Categoría");
							System.out.println("5. Stock");
							System.out.println("6. Volver al menú principal");
							System.out.print("Ingrese una opción: ");
							opcionActualizar = scanner.nextLine().trim();

							switch (opcionActualizar) {
							case "1":
								String nuevoNombre;
								do {
									System.out.print("Ingrese el nuevo nombre: ");
									nuevoNombre = scanner.nextLine().trim();
									if (nuevoNombre.equalsIgnoreCase(producto.getNombre())) {
										System.out
												.println("El nuevo nombre es igual al actual. Ingrese uno diferente.");
									}
								} while (nuevoNombre.equalsIgnoreCase(producto.getNombre()));

								producto.setNombre(nuevoNombre);
								catalogoService.actualizarProducto(producto);
								System.out.println("Nombre actualizado correctamente.");
								break;

							case "2":
								String nuevaDescripcion;
								do {
									System.out.print("Ingrese la nueva descripción: ");
									nuevaDescripcion = scanner.nextLine().trim();
									if (nuevaDescripcion.equalsIgnoreCase(producto.getDescripcion())) {
										System.out.println(
												"La descripción ingresada es igual a la actual. Ingrese una diferente.");
									}
								} while (nuevaDescripcion.equalsIgnoreCase(producto.getDescripcion()));

								producto.setDescripcion(nuevaDescripcion);
								catalogoService.actualizarProducto(producto);
								System.out.println("Descripción actualizada correctamente.");
								break;

							case "3":

								double nuevoPrecio = -1;

								do {
									try {
										System.out.print("Ingrese el nuevo precio: ");
										nuevoPrecio = Double.parseDouble(scanner.nextLine().trim());

										if (nuevoPrecio <= 0) {
											System.out.println("El precio debe ser mayor a 0. Intente nuevamente.");
											nuevoPrecio = -1;
											continue;
										}

										if (nuevoPrecio == producto.getPrecioUnitario()) {
											System.out.println(
													"El precio ingresado es igual al actual. Ingrese uno diferente.");
											nuevoPrecio = -1;
										}
									} catch (NumberFormatException e) {
										System.out.println("Error: el precio debe ser un número decimal.");
									}
								} while (nuevoPrecio < 0);

								producto.setPrecioUnitario(nuevoPrecio);
								catalogoService.actualizarProducto(producto);
								System.out.println("Precio actualizado correctamente.");
								break;

							case "4":
								String nuevaCategoria;
								do {
									System.out.print("Ingrese la nueva categoría: ");
									nuevaCategoria = scanner.nextLine().trim();
									if (nuevaCategoria.equalsIgnoreCase(producto.getCategoria())) {
										System.out.println(
												"La categoría ingresada es igual a la actual. Ingrese una diferente.");
									}
								} while (nuevaCategoria.equalsIgnoreCase(producto.getCategoria()));

								producto.setCategoria(nuevaCategoria);
								catalogoService.actualizarProducto(producto);
								System.out.println("Categoría actualizada correctamente.");
								break;

							case "5":

								int cantidadAAgregar = -1;

								do {
									try {
										System.out.print("Ingrese la cantidad a agregar al stock: ");
										cantidadAAgregar = Integer.parseInt(scanner.nextLine().trim());

										if (cantidadAAgregar <= 0) {
											System.out.println("La cantidad debe ser mayor a 0. Intente nuevamente.");
											cantidadAAgregar = -1;
										}
									} catch (NumberFormatException e) {
										System.out.println("Error: la cantidad debe ser un número entero.");
									}
								} while (cantidadAAgregar < 0);

// Sumar la cantidad ingresada al stock actual
								int nuevoStock = producto.getCantidadStock() + cantidadAAgregar;
								producto.setCantidadStock(nuevoStock);

// Actualizar el producto en MongoDB y Redis
								catalogoService.actualizarProducto(producto);

								System.out.println(
										"Cantidad de stock actualizada correctamente. Nuevo stock: " + nuevoStock);

								break;

							case "6":
								System.out.println("Volviendo al menú principal...");
								break;

							default:
								System.out.println("Opción inválida. Intente nuevamente.");
							}
						} while (!opcionActualizar.equals("6"));
						break;

///AGREGAR PRODUCTO

					case "2":

						System.out.println("\nAgregar producto:");

						try {
							String codigo;

							// Validar que el código sea un número entero mayor a 0 y no exista
							do {
								System.out.print("Ingrese el código del producto: ");
								try {
									codigo = scanner.nextLine().trim();

									if (!codigo.matches("\\d+") || Integer.parseInt(codigo) <= 0) {
										System.out.println(
												"El código debe ser un número entero mayor a 0. Intente nuevamente.");
										continue;
									}

									if (catalogoService.existeProductoPorCodigo(codigo)) {
										System.out.println("El código ingresado ya existe. Intente con otro.");
									} else {
										break; // Código válido
									}
								} catch (NumberFormatException e) {
									System.out.println("El código debe ser un número entero. Intente nuevamente.");
								}
							} while (true);

							System.out.print("Ingrese el nombre del producto: ");
							String nombre = scanner.nextLine().trim();

							System.out.print("Ingrese la descripción del producto: ");
							String descripcion = scanner.nextLine().trim();

							System.out.print("Ingrese la categoría del producto: ");
							String categoria = scanner.nextLine().trim();

							double precio;

							// Validar que el precio sea un número decimal mayor a 0
							do {
								System.out.print("Ingrese el precio unitario del producto: ");
								try {
									precio = Double.parseDouble(scanner.nextLine().trim());
									if (precio <= 0) {
										System.out
												.println("El precio debe ser un número mayor a 0. Intente nuevamente.");
									} else {
										break; // Precio válido
									}
								} catch (NumberFormatException e) {
									System.out.println("El precio debe ser un número decimal. Intente nuevamente.");
								}
							} while (true);

							int stock;

							// Validar que el stock sea un número entero (puede ser 0 o mayor)
							do {
								System.out.print("Ingrese la cantidad de stock del producto (entero): ");
								try {
									stock = Integer.parseInt(scanner.nextLine().trim());
									if (stock < 0) {
										System.out.println("El stock no puede ser negativo. Intente nuevamente.");
									} else {
										break; // Stock válido
									}
								} catch (NumberFormatException e) {
									System.out.println("El stock debe ser un número entero. Intente nuevamente.");
								}
							} while (true);

							// Crear el objeto Producto
							Producto nuevoProducto = new Producto();
							nuevoProducto.setCodigo(codigo);
							nuevoProducto.setNombre(nombre);
							nuevoProducto.setDescripcion(descripcion);
							nuevoProducto.setCategoria(categoria);
							nuevoProducto.setPrecioUnitario(precio);
							nuevoProducto.setCantidadStock(stock);

							// Guardar el producto usando CatalogoService
							Producto productoGuardado = catalogoService.guardarProducto(nuevoProducto);

							System.out.println("Producto agregado correctamente: " + productoGuardado.getNombre());
						} catch (Exception e) {
							System.out.println("Error al agregar el producto: " + e.getMessage());
						}

						break;

///ELIMINAR PRODUCTO

					case "3":
						System.out.println("\nEliminar producto:");
						String codigoEliminar;
						boolean eliminado;

						do {
							System.out.print("Ingrese el código del producto a eliminar: ");
							codigoEliminar = scanner.nextLine().trim();

							// Llamar al método de eliminación en CatalogoService
							eliminado = catalogoService.eliminarProductoPorCodigo(codigoEliminar);

							if (!eliminado) {
								System.out.println("No se encontró un producto con ese código. Intente nuevamente.");
							}
						} while (!eliminado);

						System.out.println(" Producto eliminado correctamente.");
						break;

					case "4":
						System.out.println("Volviendo al menú principal...");
						break;
					default:
						System.out.println("Opción inválida. Intente nuevamente.");
					}
				} while (!opcionStock.equals("4"));
				break;

			case "2":
				String opcionVenta;

				do {
					System.out.println("\nGenerar venta - Opciones:");
					System.out.println("1. Listar todos los productos");
					System.out.println("2. Buscar producto por código");
					System.out.println("3. Buscar productos por categoría");
					System.out.println("4. Crear carrito");
					System.out.println("5. Eliminar productos del carrito");
					System.out.println("6. Finalizar venta");
					System.out.println("7. Cancelar venta");
					System.out.print("Seleccione una opción: ");
					opcionVenta = scanner.nextLine();

///LISTADO DE PRODUCTOS EN VENTA
					switch (opcionVenta) {
					case "1":
						List<Producto> todos = catalogoService.obtenerProductosDesdeRedis();
						if (todos.isEmpty()) {
							System.out.println("No hay productos disponibles.");
						} else {
							mostrarTablaProductos(todos);
						}
						break;

					case "2":
						List<Producto> porCodigo;
						do {
							System.out.print("Ingrese el código del producto: ");
							String codigoBuscado = scanner.nextLine().trim();

							porCodigo = catalogoService.obtenerProductosDesdeRedis().stream()
									.filter(p -> p.getCodigo().equalsIgnoreCase(codigoBuscado)).toList();

							if (porCodigo.isEmpty()) {
								System.out
										.println("No se encontró ningún producto con ese código. Intente nuevamente.");
							}
						} while (porCodigo.isEmpty());

						mostrarTablaProductos(porCodigo);
						break;

					case "3":
						List<Producto> porCategoria;
						do {
							System.out.print("Ingrese la categoría a buscar: ");
							String categoria = scanner.nextLine().trim();

							porCategoria = catalogoService.obtenerProductosDesdeRedis().stream().filter(
									p -> p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria))
									.toList();

							if (porCategoria.isEmpty()) {
								System.out
										.println("No se encontraron productos para esa categoría. Intente nuevamente.");
							}
						} while (porCategoria.isEmpty());

						mostrarTablaProductos(porCategoria);

						break;
/// AGREGAR PRODUCTOS AL CARRITO
					case "4":
						System.out.println("\nAgregando productos al carrito...");

						String carritoId = emailUsuario;
						Map<String, Integer> productosCarrito = new HashMap<>();
						String opcionCarrito;

						do {
							String codigoProducto;
							int cantidad;

							// Validar que el código exista
							do {
								System.out.print("Ingrese el código del producto: ");
								codigoProducto = scanner.nextLine().trim();

								if (!catalogoService.existeProductoEnRedis(codigoProducto)) {
									System.out.println("El código ingresado no existe. Intente nuevamente.");
								} else {
									break;
								}
							} while (true);

							// Validar cantidad
							do {
								System.out.print("Ingrese la cantidad de unidades: ");
								try {
									cantidad = Integer.parseInt(scanner.nextLine().trim());
									if (cantidad > 0) {
										break;
									} else {
										System.out.println("La cantidad debe ser mayor a 0.");
									}
								} catch (NumberFormatException e) {
									System.out.println("Entrada inválida. Ingrese un número entero.");
								}
							} while (true);

							// Agregar al carrito local
							productosCarrito.put(codigoProducto,
									productosCarrito.getOrDefault(codigoProducto, 0) + cantidad);
							System.out.println("Producto agregado al carrito.");

							// Preguntar si quiere seguir agregando
							do {
								System.out.println("\n¿Desea agregar más productos al carrito?");
								System.out.println("1. Sí");
								System.out.println("2. No, confirmar y ver el carrito");
								System.out.print("Ingrese una opción: ");
								opcionCarrito = scanner.nextLine().trim();

								if (!opcionCarrito.equals("1") && !opcionCarrito.equals("2")) {
									System.out.println("Opción inválida. Intente nuevamente.");
								} else {
									break;
								}
							} while (true);

						} while (opcionCarrito.equals("1"));

						// Guardar el carrito en Redis
						carritoService.guardarCarrito(carritoId, productosCarrito);
						System.out.println("Carrito guardado exitosamente.");

						// Mostrar tabla del carrito si eligió ver
						if (opcionCarrito.equals("2")) {
							Map<String, String> carritoGuardado = carritoService.obtenerCarrito(carritoId);

							if (carritoGuardado.isEmpty()) {
								System.out.println("El carrito está vacío.");
							} else {
								System.out.println("\nContenido del carrito:");
								System.out.println("┌────────────────────┬──────────────┐");
								System.out.printf("│ %-18s │ %-12s │%n", "Código Producto", "Unidades");
								System.out.println("├────────────────────┼──────────────┤");

								for (Map.Entry<String, String> entry : carritoGuardado.entrySet()) {
									System.out.printf("│ %-18s │ %-12s │%n", entry.getKey(), entry.getValue());
								}

								System.out.println("└────────────────────┴──────────────┘");
							}
						}

						break;

/// ELIMINAR PRODUCTOS AL CARRITO

					case "5":
						System.out.println("Eliminar productos del carrito...");

						String codigoAEliminar;
						boolean productoEncontrado;
						String opcionEliminar;

						do {
							Map<String, String> carritoActual = carritoService.obtenerCarrito(emailUsuario);

							if (carritoActual == null || carritoActual.isEmpty()) {
								System.out.println("El carrito está vacío. No hay productos para eliminar.");
								break;
							}

							// Mostrar productos disponibles en el carrito
							System.out.println("\nProductos en el carrito:");
							System.out.println("┌────────────────────┬──────────────┐");
							System.out.printf("│ %-18s │ %-12s │%n", "Código Producto", "Unidades");
							System.out.println("├────────────────────┼──────────────┤");
							for (Map.Entry<String, String> entry : carritoActual.entrySet()) {
								System.out.printf("│ %-18s │ %-12s │%n", entry.getKey(), entry.getValue());
							}
							System.out.println("└────────────────────┴──────────────┘");

							// Ingresar el código a eliminar
							do {
								System.out.print("Ingrese el código del producto a eliminar del carrito: ");
								codigoAEliminar = scanner.nextLine().trim();

								if (!carritoActual.containsKey(codigoAEliminar)) {
									System.out.println("Ese producto no está en el carrito. Intente con otro código.");
									productoEncontrado = false;
								} else {
									productoEncontrado = true;
								}
							} while (!productoEncontrado);

							// Eliminar del carrito
							carritoService.eliminarProducto(emailUsuario, codigoAEliminar);
							System.out.println("Producto eliminado del carrito.");

							// Preguntar si desea eliminar otro
							do {
								System.out.println("\n¿Desea eliminar otro producto?");
								System.out.println("1. Sí");
								System.out.println("2. No, ver el carrito actualizado");
								System.out.print("Seleccione una opción: ");
								opcionEliminar = scanner.nextLine().trim();

								if (!opcionEliminar.equals("1") && !opcionEliminar.equals("2")) {
									System.out.println("Opción inválida. Intente nuevamente.");
								}
							} while (!opcionEliminar.equals("1") && !opcionEliminar.equals("2"));

						} while (opcionEliminar.equals("1"));

						// Si elige no seguir, mostrar carrito actualizado
						Map<String, String> carritoFinal = carritoService.obtenerCarrito(emailUsuario);
						if (carritoFinal == null || carritoFinal.isEmpty()) {
							System.out.println("El carrito está vacío.");
						} else {
							System.out.println("\nCarrito actualizado:");
							System.out.println("┌────────────────────┬──────────────┐");
							System.out.printf("│ %-18s │ %-12s │%n", "Código Producto", "Unidades");
							System.out.println("├────────────────────┼──────────────┤");
							for (Map.Entry<String, String> entry : carritoFinal.entrySet()) {
								System.out.printf("│ %-18s │ %-12s │%n", entry.getKey(), entry.getValue());
							}
							System.out.println("└────────────────────┴──────────────┘");
						}

						break;

//FINALIZAR VENTA

					case "6":
						System.out.println("Finalizar venta");

						// Verificar si el carrito está vacío
						Map<String, String> carrito = carritoService.obtenerCarrito(emailUsuario);
						if (carrito == null || carrito.isEmpty()) {
							System.out.println("El carrito está vacío. Agregue productos antes de finalizar la venta.");
							break;
						}

						// Solicitar medio de pago con validación
						String medioPago;
						do {
							System.out.print(
									"Ingrese el medio de pago (efectivo, tarjeta de débito o tarjeta de crédito): ");
							medioPago = scanner.nextLine().trim().toLowerCase();

							if (!medioPago.equals("efectivo") && !medioPago.equals("tarjeta de débito")
									&& !medioPago.equals("tarjeta de crédito")) {
								System.out.println("Medio de pago inválido. Intente nuevamente.");
							} else {
								break;
							}
						} while (true);

						try {
							Venta venta = ventaService.procesarVentaDesdeCarrito(emailUsuario, medioPago);
							System.out.println("\n¡Venta realizada con éxito!");
							System.out.println("\nTotal: $" + venta.getTotal());
						} catch (RuntimeException e) {
							String mensaje = e.getMessage();
							if (mensaje.contains("\nNo hay suficiente stock")) {
								System.out.println(mensaje);
								System.out.println("Revise y ajuste las cantidades antes de finalizar la venta.");
							} else {
								System.out.println("Error al procesar la venta: " + mensaje);
							}
							// IMPORTANTE: continuar en el menú de venta
							break;
						}

						break;

					case "7":

//LIMPIA EL CARRITO
						// Eliminar todos los productos del carrito
						carritoService.limpiarCarrito(emailUsuario);

						System.out.println("Todos los productos del carrito fueron eliminados.");
						System.out.println("Venta cancelada. Volviendo al menú principal...");
						break;

					default:
						System.out.println("Opción inválida. Intente nuevamente.");
					}

				} while (!opcionVenta.equals("6") && !opcionVenta.equals("7"));

				break;

//HISTORIAL DE VENTAS
			case "3":
				String opcionHistorial;

				do {
					System.out.println("\nHistorial de ventas:");
					System.out.println("1. Ver todas las ventas");
					System.out.println("2. Ver ventas por vendedor");
					System.out.println("3. Volver al menú principal");
					System.out.print("Seleccione una opción: ");
					opcionHistorial = scanner.nextLine().trim();

					switch (opcionHistorial) {
					case "1":
						List<Venta> todas = new ArrayList<>();
						ventaService.listarVentas().forEach(todas::add);
						mostrarTablaVentas(todas);
						break;

					case "2":
						System.out.print("Ingrese el email del vendedor a buscar: ");
						String emailVendedorBuscado = scanner.nextLine().trim();

						List<Venta> ventasDelVendedor = ventaService.listarVentasPorVendedor(emailVendedorBuscado);

						if (ventasDelVendedor.isEmpty()) {
							System.out.println("No se encontraron ventas registradas para ese vendedor.");
						} else {
							mostrarTablaVentas(ventasDelVendedor);
						}
						break;

					case "3":
						System.out.println("Volviendo al menú principal...");
						break;

					default:
						System.out.println("Opción inválida. Intente nuevamente.");
					}

				} while (!opcionHistorial.equals("3"));
				break;

			case "4":
				// Mostrar catálogo de productos
				List<Producto> catalogo = catalogoService.obtenerProductosDesdeRedis();

				if (catalogo.isEmpty()) {
					System.out.println("No hay productos disponibles en el catálogo.");
				} else {
					System.out.println("\nCatálogo de productos:");
					System.out.println("┌──────────────┬────────────────────────────┬──────────────┐");
					System.out.printf("│ %-12s │ %-26s │ %-12s │%n", "Código", "Nombre", "Precio");
					System.out.println("├──────────────┼────────────────────────────┼──────────────┤");

					for (Producto producto : catalogo) {
						System.out.printf("│ %-12s │ %-26s │ %-12.2f │%n", producto.getCodigo(), producto.getNombre(),
								producto.getPrecioUnitario());
					}

					System.out.println("└──────────────┴────────────────────────────┴──────────────┘");
				}
				break;
			
			case "5":
				// Ver detalle de producto por código
				String codigoProducto;
				Producto productoDetalle;

				do {
					System.out.print("Ingrese el código del producto a consultar: ");
					codigoProducto = scanner.nextLine().trim();

					productoDetalle = catalogoService.obtenerProductoPorCodigo(codigoProducto);

					if (productoDetalle == null) {
						System.out.println("No se encontró un producto con ese código. Intente nuevamente.");
					}
				} while (productoDetalle == null);

				System.out.println("\nDetalle del producto:");
				System.out.println("Código: " + productoDetalle.getCodigo());
				System.out.println("Nombre: " + productoDetalle.getNombre());
				System.out.println("Descripción: " + productoDetalle.getDescripcion());
				System.out.println("Categoría: " + productoDetalle.getCategoria());
				System.out.printf("Precio Unitario: $%.2f%n", productoDetalle.getPrecioUnitario());
				System.out.println("Stock disponible: " + productoDetalle.getCantidadStock());

				break;

			case "6":
				System.out.println("Saliendo de la consola del vendedor...");
				consolaPrincipal.iniciar();
				return;

			default:
				System.out.println("Opción inválida. Intente nuevamente.");
			}
		} while (!opcion.equals("6"));
	}

//AUXILIARES

	private void mostrarTablaProductos(List<Producto> productos) {
		if (productos.isEmpty()) {
			System.out.println("No se encontraron productos.");
			return;
		}

		System.out
				.println("┌──────────────┬────────────────────────────┬───────────────┬──────────────┬──────────────┐");
		System.out.printf("│ %-12s │ %-26s │ %-13s │ %-12s │ %-12s │%n", "Código", "Nombre", "Categoría", "Precio",
				"Stock");
		System.out
				.println("├──────────────┼────────────────────────────┼───────────────┼──────────────┼──────────────┤");

		for (Producto p : productos) {
			System.out.printf("│ %-12s │ %-26s │ %-13s │ %-12.2f │ %-12d │%n", p.getCodigo(), p.getNombre(),
					p.getCategoria(), p.getPrecioUnitario(), p.getCantidadStock());
		}

		System.out
				.println("└──────────────┴────────────────────────────┴───────────────┴──────────────┴──────────────┘");
	}

	private void mostrarTablaVentas(List<Venta> ventas) {
		SimpleDateFormat formatoFecha = new SimpleDateFormat("EEE MMM dd", Locale.ENGLISH); // Ej: "Sun Jun 15"

		System.out.println(
				"------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("%-24s %-25s %-12s %-10s %-10s %-10s %-20s %-20s%n", "ID", "Vendedor", "Código", "Precio",
				"Cantidad", "Total", "Medio de Pago", "Fecha");
		System.out.println(
				"------------------------------------------------------------------------------------------------------------------------");

		for (Venta venta : ventas) {
			String idCompleto = venta.getId() != null ? venta.getId() : "-";
			String vendedor = venta.getVendedor() != null ? venta.getVendedor() : "-";
			String medioPago = venta.getMedioPago() != null ? venta.getMedioPago() : "-";
			String fecha = "-";

			if (venta.getFecha() != null) {
				fecha = formatoFecha.format(venta.getFecha()); // solo la parte deseada
			}

			for (var linea : venta.getLineasVenta()) {
				String codigo = linea.getCodigoProducto() != null ? linea.getCodigoProducto() : "-";
				double precio = linea.getPrecioUnitario();
				int cantidad = linea.getCantidad();
				double total = venta.getTotal();

				System.out.printf("%-24s %-25s %-12s %-10.2f %-10d %-10.2f %-20s %-20s%n", idCompleto, vendedor, codigo,
						precio, cantidad, total, medioPago, fecha);
			}
		}
	}

}
