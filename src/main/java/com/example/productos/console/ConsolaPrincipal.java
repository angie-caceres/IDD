package com.example.productos.console;
import com.example.productos.service.UsuarioService;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsolaPrincipal {
	@Autowired
    private UsuarioService usuarioService;
	
	@Autowired
    private ConsolaAdministrador consolaAdministrador;



public void iniciar() {
    Scanner scanner = new Scanner(System.in);
    String opcion;

    System.out.println("\n╔═══════════════════════════════════════╗");
    System.out.println("║       ¡Bienvenid@ a Uade Store!       ║");
    System.out.println("╚═══════════════════════════════════════╝\n");

    do {
        System.out.println("1 - Iniciar sesión");
        System.out.println("2 - Salir");
        System.out.print("\nSeleccione una opción: ");
        opcion = scanner.nextLine();

        if (!opcion.equals("1") && !opcion.equals("2")) {
            System.out.println(" Opción inválida. Por favor, intente nuevamente.\n");
        }
    } while (!opcion.equals("1") && !opcion.equals("2"));

    switch (opcion) {
        case "1":
            boolean credencialesValidas = false;
            while (!credencialesValidas) {
                System.out.print("Ingrese usuario: ");
                String usuarioIngresado = scanner.nextLine();
                System.out.print("Ingrese contraseña: ");
                String contrasenaIngresada = scanner.nextLine();
                System.out.println("\nIniciando sesión...");
                System.out.println("Usuario: " + usuarioIngresado);
                System.out.println("Contraseña: " + contrasenaIngresada);

                if (usuarioService.validarCredenciales(usuarioIngresado, contrasenaIngresada)) {
                    credencialesValidas = true;
                    System.out.println("\nInicio de sesión exitoso =)");
                    switch (usuarioService.obtenerPerfilDesdeRedis(usuarioIngresado)) {
                        case "admin":
                            System.out.println("\n========================================================  BIENVENIDO ADMIN  ========================================================");
                            consolaAdministrador.iniciar();
                            break;
                        case "vendedor":
                        	System.out.println("\n========================================================  BIENVENIDO VENDEDOR  ========================================================");
                            break;
                        default:
                            System.out.println("Perfil desconocido");
                    }
                } else {
                    System.out.println(" Credenciales inválidas. Por favor, intente nuevamente.\n");
                }
            }
            break;

        case "2":
            System.out.println("Saliendo...");
            System.exit(0);
            break;
    }

    scanner.close();
}

}
