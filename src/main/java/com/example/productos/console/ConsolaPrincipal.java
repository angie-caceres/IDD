package com.example.productos.console;
import com.example.productos.service.UsuarioService;
import java.util.Scanner;

public class ConsolaPrincipal {

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("¡Bienvenid@ a Uade Store!");
        System.out.println("1 - Iniciar sesión");
        System.out.println("2 - Salir");
        UsuarioService usuarioService = new UsuarioService();

        System.out.print("Seleccione una opción: ");
        String opcion = scanner.nextLine();

        switch (opcion) {
            case "1":
            	
            	System.out.print("Ingrese usuario: ");
				Scanner usuario = new Scanner(System.in);
				String usuarioIngresado = usuario.nextLine();
				System.out.print("Ingrese contraseña: ");
				Scanner contrasena = new Scanner(System.in);
				String contrasenaIngresada = contrasena.nextLine();
				System.out.println("➡ Iniciando sesión...");
				System.out.println("➡ Usuario: " + usuarioIngresado);
				System.out.println("➡ Contraseña: " + contrasenaIngresada);
		
				if (usuarioService.validarCredenciales(usuarioIngresado, contrasenaIngresada)) {
					System.out.println("✅ Inicio de sesión exitoso");
					switch (usuarioService.obtenerPerfilDesdeRedis(usuarioIngresado)) {
                        case "administrador":
                            System.out.println(" Bienvenido Administrador");
                            // Aquí podrías llamar a la consola del administrador
                            break;
                        case "vendedor":
                            System.out.println(" Bienvenido Vendedor");
                            // Aquí podrías llamar a la consola del vendedor
                            break;
                        default:
                            System.out.println(" Perfil desconocido");
                    }
				} else {
					System.out.println("❌ Credenciales inválidas");
				}
				
				
                // Acá llamás a lógica de login
                break;
            case "2":
                System.out.println("Saliendo...");
                // Que finalice la aplicación
                System.exit(0);
                break;
            default:
                System.out.println("❌ Opción inválida");
                break;
        }

        scanner.close();
    }
}
