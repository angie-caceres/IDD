package com.example.productos.service;

import com.example.productos.dto.UsuarioRegistroRequest;
import com.example.productos.dto.UsuarioResponse;
import com.example.productos.model.Usuario;
import com.example.productos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		usuario.setPerfil(request.getPerfil());

		// Guardar en MongoDB
		Usuario usuarioGuardado = usuarioRepository.save(usuario);
		// 🔐 Guardar automáticamente en Redis
		guardarUsuarioEnRedis(
		    usuarioGuardado.getEmail(),
		    usuarioGuardado.getContraseña(),
		    usuarioGuardado.getPerfil()
		);

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
		return usuarioRepository.findById(id).map(UsuarioResponse::new);
	}

	public Optional<UsuarioResponse> obtenerUsuarioPorEmail(String email) {
		return usuarioRepository.findByEmail(email).map(UsuarioResponse::new);
	}

	
	public List<UsuarioResponse> obtenerUsuariosPorPerfil(String perfil) {
	    return usuarioRepository.findByPerfil(perfil)
	            .stream()
	            .map(UsuarioResponse::new)
	            .collect(Collectors.toList());
	}


	public List<UsuarioResponse> listarUsuariosActivos() {
		return usuarioRepository.findByActivoTrue().stream().map(UsuarioResponse::new).collect(Collectors.toList());
	}

	public List<UsuarioResponse> listarUsuarios() {
		return usuarioRepository.findAll().stream().map(UsuarioResponse::new).collect(Collectors.toList());
	}

	/*public boolean validarCredenciales(String email, String contraseña) {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
		if (usuario.isPresent() && usuario.get().isActivo()) {
			return passwordEncoder.matches(contraseña, usuario.get().getContraseña());
		}
		return false;
	}*/
	public boolean validarCredenciales(String email, String contraseña) {
	    try {
	        String key = REDIS_USER_PREFIX + email;
	        Object hashEnRedis = redisTemplate.opsForHash().get(key, "password");

	        if (hashEnRedis == null) {
	            return false; // Usuario no existe en Redis
	        }

	        return passwordEncoder.matches(contraseña, hashEnRedis.toString());
	    } catch (Exception e) {
	        System.err.println("Error validando credenciales en Redis: " + e.getMessage());
	        return false;
	    }
	}
	
	public List<UsuarioResponse> listarUsuariosPorPerfil(String perfil) {
		return usuarioRepository.findByPerfil(perfil)
                .stream()
                .map(UsuarioResponse::new)
                .collect(Collectors.toList());
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

public boolean desactivarUsuarioPorEmail(String email) {
    Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
    if (usuarioOpt.isPresent()) {
        Usuario usuario = usuarioOpt.get();
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
     // Eliminar el usuario de Redis
        eliminarUsuarioDeRedis(email);
        return true;
    }
    return false;
}


	
	public boolean actualizarNombre(String id, String nuevoNombre) {
	    Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
	    if (usuarioOpt.isPresent() && nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
	        Usuario usuario = usuarioOpt.get();
	        usuario.setNombre(nuevoNombre.trim());
	        usuarioRepository.save(usuario);
	        return true;
	    }
	    return false;
	}
	
	public boolean actualizarEmail(String id, String nuevoEmail) {
	    if (nuevoEmail == null || !isValidEmail(nuevoEmail)) {
	        throw new RuntimeException("Email inválido");
	    }

	    Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
	    if (usuarioOpt.isPresent()) {
	        Usuario usuario = usuarioOpt.get();

	        // Si es el mismo email que ya tenía
	        if (usuario.getEmail().equalsIgnoreCase(nuevoEmail.trim())) {
	            throw new RuntimeException("El nuevo email es igual al actual");
	        }

	        // Verifica si el nuevo email ya lo tiene otro usuario
	        if (usuarioRepository.existsByEmail(nuevoEmail)) {
	            throw new RuntimeException("El email ya está registrado por otro usuario");
	        }

	        usuario.setEmail(nuevoEmail.trim());
	        usuarioRepository.save(usuario);
	        return true;
	    }

	    return false;
	}

	public boolean actualizarContraseña(String id, String nuevaContraseña) {
	    if (nuevaContraseña == null || nuevaContraseña.length() < 6) {
	        throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
	    }

	    Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
	    if (usuarioOpt.isPresent()) {
	        Usuario usuario = usuarioOpt.get();
	        usuario.setContraseña(passwordEncoder.encode(nuevaContraseña));
	        usuarioRepository.save(usuario);
	        return true;
	    }
	    return false;
	}
	
	public boolean actualizarPerfil(String id, String nuevoPerfil) {
	    if (nuevoPerfil == null || nuevoPerfil.trim().isEmpty()) {
	        throw new RuntimeException("El perfil no puede estar vacío");
	    }

	    Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
	    if (usuarioOpt.isPresent()) {
	        Usuario usuario = usuarioOpt.get();
	        usuario.setPerfil(nuevoPerfil.trim());
	        usuarioRepository.save(usuario);
	        return true;
	    }
	    return false;
	}


	public boolean eliminarUsuarioPorEmail(String email) {
	    Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
	    if (usuarioOpt.isPresent()) {
	        Usuario usuario = usuarioOpt.get();
	        
	        // Eliminar de MongoDB
	        usuarioRepository.delete(usuario);
	        
	        // Eliminar de Redis
	        eliminarUsuarioDeRedis(email);
	        
	        return true;
	    }
	    return false;
	}
	
	public boolean verificarContraseñaActual(String email, String contraseñaPlana) {
	    String key = REDIS_USER_PREFIX + email;
	    Object hash = redisTemplate.opsForHash().get(key, "password");
	    if (hash != null) {
	        return passwordEncoder.matches(contraseñaPlana, hash.toString());
	    }
	    return false;
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
				guardarUsuarioEnRedis(usuario.getEmail(), usuario.getContraseña(), usuario.getPerfil());
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

	
	private void guardarUsuarioEnRedis(String email, String contraseñaEncriptada, String perfil) {
		try {
			String key = REDIS_USER_PREFIX + email;
			Map<String, String> usuarioMap = new HashMap<>();
			usuarioMap.put("password", contraseñaEncriptada);
			usuarioMap.put("perfil", perfil);
			redisTemplate.opsForHash().putAll(key, usuarioMap);
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
			Object password = redisTemplate.opsForHash().get(key, "password");
			return password != null ? password.toString() : null;
		} catch (Exception e) {
			System.err.println("Error al obtener contraseña de Redis: " + e.getMessage());
			return null;
		}
	}

	public String obtenerPerfilDesdeRedis(String email) {
		try {
			String key = REDIS_USER_PREFIX + email;
			Object perfil = redisTemplate.opsForHash().get(key, "perfil");
			return perfil != null ? perfil.toString() : null;
		} catch (Exception e) {
			System.err.println("Error al obtener perfil de Redis: " + e.getMessage());
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
	
	
	public void actualizarEmailEnRedis(String emailViejo, String emailNuevo) {
	    try {
	        String oldKey = REDIS_USER_PREFIX + emailViejo;
	        Map<Object, Object> datos = redisTemplate.opsForHash().entries(oldKey);
	        redisTemplate.delete(oldKey);

	        String newKey = REDIS_USER_PREFIX + emailNuevo;
	        redisTemplate.opsForHash().putAll(newKey, datos);
	    } catch (Exception e) {
	        System.err.println("Error al actualizar el email en Redis: " + e.getMessage());
	    }
	}

	public void actualizarPasswordEnRedis(String email, String nuevaPassword) {
	    try {
	        String key = REDIS_USER_PREFIX + email;
	        String hash = passwordEncoder.encode(nuevaPassword);
	        redisTemplate.opsForHash().put(key, "password", hash);
	    } catch (Exception e) {
	        System.err.println("Error al actualizar la contraseña en Redis: " + e.getMessage());
	    }
	}

	public void actualizarPerfilEnRedis(String email, String nuevoPerfil) {
	    try {
	        String key = REDIS_USER_PREFIX + email;
	        redisTemplate.opsForHash().put(key, "perfil", nuevoPerfil);
	    } catch (Exception e) {
	        System.err.println("Error al actualizar el perfil en Redis: " + e.getMessage());
	    }
	}
}