package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity,Integer> {
    public UsuarioEntity findUsuarioByEmailAndContraseña(String email, String contraseña);
    public Optional<UsuarioEntity> findUsuarioByEmail(String email);
}
