
package com.example.productos.console;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.context.annotation.Lazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.productos.dto.UsuarioRegistroRequest;
import com.example.productos.dto.UsuarioResponse;
import com.example.productos.service.UsuarioService;

@Component
public class ConsolaAdministrador {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	@Lazy
	private ConsolaPrincipal consolaPrincipal;

	public void iniciar() {
		Scanner scanner = new Scanner(System.in);
		String opcion;

		do {

			System.out.println("\nSeleccione una opción:");
			System.out.println("1. Ver usuarios");
			System.out.println("2. Agregar usuario");
			System.out.println("3. Actualizar usuario");
			System.out.println("4. Eliminar usuario");
			System.out.println("5. Salir");
			System.out.print("Opción: ");
			opcion = scanner.nextLine();

			switch (opcion) {
			case "1":
				System.out.println("\n¿Qué usuarios desea ver?");
				System.out.println("1. Todos");
				System.out.println("2. Solo administradores");
				System.out.println("3. Solo vendedores");
				System.out.print("Opción: ");
				String subOpcion = scanner.nextLine();

				List<UsuarioResponse> usuarios = switch (subOpcion) {
				case "2" -> usuarioService.listarUsuariosPorPerfil("admin");
				case "3" -> usuarioService.listarUsuariosPorPerfil("vendedor");
				default -> usuarioService.listarUsuarios(); // "1" o cualquier otro
				};

				if (usuarios.isEmpty()) {
					System.out.println("No se encontraron usuarios para la opción seleccionada.");
				} else {
					System.out.println("┌──────────────┬───────────────┬────────────────────────────┬───────────────┐");
					System.out.printf("│ %-12s │ %-13s │ %-26s │ %-13s │%n", "Estado", "Nombre", "Email", "Perfil");
					System.out.println("├──────────────┼───────────────┼────────────────────────────┼───────────────┤");

					for (UsuarioResponse u : usuarios) {
						String estado = u.isActivo() ? "Activo" : "Inactivo";
						System.out.printf("│ %-12s │ %-13s │ %-26s │ %-13s │%n", estado, u.getNombre(), u.getEmail(),
								u.getPerfil());
					}

					System.out.println("└──────────────┴───────────────┴────────────────────────────┴───────────────┘");
				}

				break;

			case "2":

				System.out.println("\nAgregar nuevo usuario:");
				boolean usuarioRegistrado = false;

				while (!usuarioRegistrado) {
					try {
						// Ingreso y validación de nombre
						System.out.print("Ingrese nombre: ");
						String nombre = scanner.nextLine().trim();
						if (nombre.length() < 2 || nombre.length() > 100) {
							System.out.println("El nombre debe tener entre 2 y 100 caracteres.");
							continue;
						}

						// Ingreso y validación del perfil
						String perfil;
						do {
							System.out.print("Ingrese perfil (admin/vendedor): ");
							perfil = scanner.nextLine().trim().toLowerCase();
							if (!perfil.equals("admin") && !perfil.equals("vendedor")) {
								System.out.println("Perfil inválido. Solo se permite 'admin' o 'vendedor'.");
							}
						} while (!perfil.equals("admin") && !perfil.equals("vendedor"));

						// Ingreso y validación de email
						System.out.print("Ingrese email: ");
						String email = scanner.nextLine().trim();
						if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
							System.out.println("Formato de email inválido.");
							continue;
						}

						// Ingreso y validación de contraseña
						System.out.print("Ingrese contraseña: ");
						String contraseña = scanner.nextLine().trim();
						if (contraseña.length() < 6) {
							System.out.println("La contraseña debe tener al menos 6 caracteres.");
							continue;
						}

						// Crear el objeto de solicitud
						UsuarioRegistroRequest request = new UsuarioRegistroRequest();
						request.setNombre(nombre);
						request.setPerfil(perfil);
						request.setEmail(email);
						request.setContraseña(contraseña);

						// Registrar el usuario
						usuarioService.registrarUsuario(request);

						// Mensaje de éxito
						System.out.println("\nUsuario creado correctamente =)");
						usuarioRegistrado = true;

					} catch (RuntimeException e) {
						System.out.println("Error: " + e.getMessage());
						System.out.println("Por favor, intente nuevamente.\n");
					}
				}

				break;

			case "3":
				System.out.println("\nActualizar usuario por email:");
				String emailActualizar;
				Optional<UsuarioResponse> usuarioOpt;

				// Repetir hasta que se encuentre un usuario
				do {
					System.out.print("Ingrese el email del usuario a actualizar: ");
					emailActualizar = scanner.nextLine();
					usuarioOpt = usuarioService.obtenerUsuarioPorEmail(emailActualizar);

					if (usuarioOpt.isEmpty()) {
						System.out.println("No se encontró un usuario con ese email. Intente nuevamente.");
					}
				} while (usuarioOpt.isEmpty());

				UsuarioResponse usuario = usuarioOpt.get();

				System.out.println("\n¿Qué desea actualizar?");
				System.out.println("1. Nombre");
				System.out.println("2. Email");
				System.out.println("3. Contraseña");
				System.out.println("4. Perfil");
				System.out.print("Opción: ");
				String opcionActualizacion = scanner.nextLine();

				switch (opcionActualizacion) {
				case "1":
					String nuevoNombre;
					do {
						System.out.print("Nuevo nombre: ");
						nuevoNombre = scanner.nextLine();
						if (nuevoNombre.equals(usuario.getNombre())) {
							System.out.println("El nuevo nombre es igual al actual. Ingrese uno diferente.");
						}
					} while (nuevoNombre.equals(usuario.getNombre()));

					if (usuarioService.actualizarNombre(usuario.getId(), nuevoNombre)) {
						System.out.println("Nombre actualizado correctamente.");
					} else {
						System.out.println("Error al actualizar el nombre.");
					}
					break;

				case "2":
					String nuevoEmail;
					boolean emailValido;
					do {
						System.out.print("Nuevo email: ");
						nuevoEmail = scanner.nextLine();
						emailValido = nuevoEmail.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$");

						if (!emailValido) {
							System.out.println("Email inválido. Intente nuevamente.");
						} else if (nuevoEmail.equals(usuario.getEmail())) {
							System.out.println("El nuevo email es igual al actual. Ingrese uno diferente.");
							emailValido = false;
						}
					} while (!emailValido);

					try {
						if (usuarioService.actualizarEmail(usuario.getId(), nuevoEmail)) {
							usuarioService.actualizarEmailEnRedis(emailActualizar, nuevoEmail);
							System.out.println("Email actualizado correctamente.");
						}
					} catch (RuntimeException e) {
						System.out.println("Error: " + e.getMessage());
					}
					break;

				case "3":

					String nuevaPass;

					do {
						System.out.print("Nueva contraseña: ");
						nuevaPass = scanner.nextLine();

						if (nuevaPass.length() <= 6) {
							System.out.println("La contraseña debe tener más de 6 caracteres. Intente nuevamente.");
							nuevaPass = null;
							continue;
						}

						if (usuarioService.verificarContraseñaActual(emailActualizar, nuevaPass)) {
							System.out.println("La nueva contraseña es igual a la actual. Ingrese una diferente.");
							nuevaPass = null;
						}
					} while (nuevaPass == null);

					if (usuarioService.actualizarContraseña(usuario.getId(), nuevaPass)) {
						usuarioService.actualizarPasswordEnRedis(usuario.getEmail(), nuevaPass);
						System.out.println("Contraseña actualizada correctamente.");
					} else {
						System.out.println("Error al actualizar la contraseña.");
					}

					break;

				case "4":
					String nuevoPerfil;
					do {
						System.out.print("Nuevo perfil (admin/vendedor): ");
						nuevoPerfil = scanner.nextLine().toLowerCase();

						if (!nuevoPerfil.equals("admin") && !nuevoPerfil.equals("vendedor")) {
							System.out.println("Perfil inválido. Debe ser 'admin' o 'vendedor'.");
							nuevoPerfil = "";
						} else if (nuevoPerfil.equals(usuario.getPerfil())) {
							System.out.println("El perfil ingresado es igual al actual. Ingrese uno diferente.");
							nuevoPerfil = "";
						}
					} while (nuevoPerfil.isEmpty());

					if (usuarioService.actualizarPerfil(usuario.getId(), nuevoPerfil)) {
						usuarioService.actualizarPerfilEnRedis(usuario.getEmail(), nuevoPerfil);
						System.out.println("Perfil actualizado correctamente.");
					} else {
						System.out.println("Error al actualizar el perfil.");
					}
					break;

				default:
					System.out.println("Opción inválida.");
				}
				break;

			case "4":
				System.out.print("Ingrese el email del usuario a desactivar: ");
				String emailDesactivar = scanner.nextLine();

				Optional<UsuarioResponse> usuarioAEliminar = usuarioService.obtenerUsuarioPorEmail(emailDesactivar);

				if (usuarioAEliminar.isEmpty()) {
					System.out.println("\nNo se encontró un usuario con ese email.");
					break; // Vuelve al menú principal
				}

				if (!usuarioAEliminar.get().isActivo()) {
					System.out.println("\nEl usuario ya se encuentra inactivo.");
					break; // Vuelve al menú principal
				}

				boolean desactivado = usuarioService.desactivarUsuarioPorEmail(emailDesactivar);
				if (desactivado) {
					System.out.println("\nUsuario desactivado correctamente.");
				} else {
					System.out.println("\nNo se pudo desactivar el usuario. Intente nuevamente.");
				}
				break;

			case "5":
				System.out.println("\nSaliendo de la consola del administrador...");
				consolaPrincipal.iniciar(); // Vuelve a la ConsolaPrincipal
				return;

			default:
				System.out.println("Opción inválida. Intente nuevamente.");
			}

		} while (!opcion.equals("5"));
	}
}
