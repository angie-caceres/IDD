package com.example.productos.service;

import com.example.productos.dto.UsuarioRegistroRequest;
import com.example.productos.dto.UsuarioResponse;
import com.example.productos.model.Usuario;
import com.example.productos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String REDIS_USER_PREFIX = "user:";


    public UsuarioResponse registrarUsuario(UsuarioRegistroRequest request) {
        // Validaciones manuales
        validarDatosUsuario(request);

        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setContraseña(passwordEncoder.encode(request.getContraseña()));

        // Guardar en MongoDB
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return new UsuarioResponse(usuarioGuardado);
    }

    private void validarDatosUsuario(UsuarioRegistroRequest request) {
        // Validar nombre
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (request.getNombre().trim().length() < 2 || request.getNombre().trim().length() > 100) {
            throw new RuntimeException("El nombre debe tener entre 2 y 100 caracteres");
        }

        // Validar email
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (!isValidEmail(request.getEmail())) {
            throw new RuntimeException("Debe ser un email válido");
        }

        // Validar contraseña
        if (request.getContraseña() == null || request.getContraseña().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }
        if (request.getContraseña().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    public Optional<UsuarioResponse> obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponse::new);
    }

    public Optional<UsuarioResponse> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(UsuarioResponse::new);
    }

    public List<UsuarioResponse> listarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(UsuarioResponse::new)
                .collect(Collectors.toList());
    }

    public boolean validarCredenciales(String email, String contraseña) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent() && usuario.get().isActivo()) {
            return passwordEncoder.matches(contraseña, usuario.get().getContraseña());
        }
        return false;
    }

    public boolean desactivarUsuario(String id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            usuario.get().setActivo(false);
            usuarioRepository.save(usuario.get());
            return true;
        }
        return false;
    }

    public UsuarioResponse actualizarUsuario(String id, UsuarioRegistroRequest request) {
        // Validaciones manuales
        validarDatosUsuario(request);

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar si el nuevo email ya existe (si es diferente al actual)
            if (!usuario.getEmail().equals(request.getEmail()) && 
                usuarioRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El email ya está registrado");
            }

            usuario.setNombre(request.getNombre());
            usuario.setEmail(request.getEmail());
            
            // Solo actualizar contraseña si se proporciona una nueva
            if (request.getContraseña() != null && !request.getContraseña().isEmpty()) {
                usuario.setContraseña(passwordEncoder.encode(request.getContraseña()));
            }

            Usuario usuarioActualizado = usuarioRepository.save(usuario);
            return new UsuarioResponse(usuarioActualizado);
        }
        return null;
    }
    
    
// Métodos para Redis
    
    /**
     * Carga todos los usuarios activos de MongoDB en Redis
     */
    public void cargarTodosLosUsuariosEnRedis() {
        try {
            List<Usuario> usuariosActivos = usuarioRepository.findByActivoTrue();
            int contador = 0;
            
            for (Usuario usuario : usuariosActivos) {
                guardarUsuarioEnRedis(usuario.getEmail(), usuario.getContraseña());
                contador++;
            }
            
            System.out.println("Se cargaron " + contador + " usuarios en Redis");
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios en Redis: " + e.getMessage());
        }
    }

    /**
     * Limpia todos los usuarios de Redis
     */
    public void limpiarUsuariosDeRedis() {
        try {
            Set<String> keys = redisTemplate.keys(REDIS_USER_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                System.out.println("Se eliminaron " + keys.size() + " usuarios de Redis");
            } else {
                System.out.println("No hay usuarios en Redis para eliminar");
            }
        } catch (Exception e) {
            System.err.println("Error al limpiar usuarios de Redis: " + e.getMessage());
        }
    }

    /**
     * Guarda un usuario en Redis con email como clave y contraseña como valor
     */
    private void guardarUsuarioEnRedis(String email, String contraseñaEncriptada) {
        try {
            String key = REDIS_USER_PREFIX + email;
            redisTemplate.opsForValue().set(key, contraseñaEncriptada);
        } catch (Exception e) {
            System.err.println("Error al guardar usuario en Redis: " + e.getMessage());
        }
    }

    /**
     * Obtiene la contraseña de un usuario desde Redis
     */
    private String obtenerContraseñaDeRedis(String email) {
        try {
            String key = REDIS_USER_PREFIX + email;
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            System.err.println("Error al obtener usuario de Redis: " + e.getMessage());
            return null;
        }
    }

    /**
     * Elimina un usuario de Redis
     */
    private void eliminarUsuarioDeRedis(String email) {
        try {
            String key = REDIS_USER_PREFIX + email;
            redisTemplate.delete(key);
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario de Redis: " + e.getMessage());
        }
    }

    /**
     * Verifica si un usuario existe en Redis
     */
    public boolean existeUsuarioEnRedis(String email) {
        try {
            String key = REDIS_USER_PREFIX + email;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            System.err.println("Error al verificar usuario en Redis: " + e.getMessage());
            return false;
        }
    }
}