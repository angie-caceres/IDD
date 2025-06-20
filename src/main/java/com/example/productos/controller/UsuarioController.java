package com.example.productos.controller;

import com.example.productos.dto.UsuarioRegistroRequest;
import com.example.productos.dto.UsuarioResponse;
import com.example.productos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRegistroRequest request) {
        try {
            UsuarioResponse usuario = usuarioService.registrarUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuario(@PathVariable String id) {
        Optional<UsuarioResponse> usuario = usuarioService.obtenerUsuarioPorId(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorEmail(@PathVariable String email) {
        Optional<UsuarioResponse> usuario = usuarioService.obtenerUsuarioPorEmail(email);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.listarUsuariosActivos();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable String id, 
                                               @RequestBody UsuarioRegistroRequest request) {
        try {
            UsuarioResponse usuario = usuarioService.actualizarUsuario(id, request);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> desactivarUsuario(@PathVariable String id) {
        boolean desactivado = usuarioService.desactivarUsuario(id);
        if (desactivado) {
            return ResponseEntity.ok("Usuario desactivado correctamente");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UsuarioRegistroRequest loginRequest) {
        boolean credencialesValidas = usuarioService.validarCredenciales(
            loginRequest.getEmail(), 
            loginRequest.getContraseña()
        );
        
        if (credencialesValidas) {
            return ResponseEntity.ok("Login exitoso");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }
}
