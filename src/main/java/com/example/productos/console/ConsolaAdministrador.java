package com.example.productos.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.productos.dto.UsuarioResponse;
import com.example.productos.model.Usuario;
import com.example.productos.service.UsuarioService;

public class ConsolaAdministrador {

	public void iniciar() {
		
		
		System.out.println("Bienvenido al sistema de administración de productos.");
		System.out.print("Seleccione una opción: ");
		System.out.println("1. Ver usuarios");
		System.out.println("2. Agregar usuario");
		System.out.println("3. Actualizar usuario");
		System.out.println("4. Eliminar usuario");
		System.out.println("5. Salir");
        Scanner scanner = new Scanner(System.in);
        String opcion = scanner.nextLine();
        UsuarioService usuarioService = new UsuarioService();
        UsuarioResponse usuario = new UsuarioResponse();
        List<UsuarioResponse> usuarios = new ArrayList<UsuarioResponse>();
        
        //esto va dentro de un while, cuando apreto 5 sale

        switch (opcion) {
        case "1":
        	
            System.out.println("Listando usuarios...");
            usuarios = usuarioService.listarUsuarios();
			if (usuarios.isEmpty()) {
				System.out.println("No hay usuarios registrados.");
			} else {
				for (UsuarioResponse u : usuarios) {
					System.out.println("ID: " + u.getId() + ", Nombre: " + u.getNombre() + ", Email: " + u.getEmail());
				}
			}
			

        	break;
        	
		case "2":
			System.out.println("Agregando usuario...");
			
			
			// Aquí se llamaría al servicio para agregar un usuario
			break;
		// Aquí se implementaría la lógica para manejar las opciones del administrador
		// como listar, agregar, actualizar y eliminar productos.
	}
}
}
