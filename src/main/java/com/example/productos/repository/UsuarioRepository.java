package com.example.productos.repository;

import com.example.productos.dto.UsuarioResponse;
import com.example.productos.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    
    /*Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByActivoTrue();
    
    boolean existsByEmail(String email);

    List<Usuario> findByPerfil(String perfil); // ✅ Correcto*/
    
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByPerfil(String perfil); // si filtrás por perfil
    List<Usuario> findByActivoTrue(); // si filtrás por activos
 
}
