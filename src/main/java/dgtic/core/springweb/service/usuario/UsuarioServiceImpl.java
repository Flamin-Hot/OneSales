package dgtic.core.springweb.service.usuario;

import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public void guardar(UsuarioEntity usuario) {
        usuarioRepository.save(usuario);
    }

    @Override
    public void borrar(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public UsuarioEntity login(String email, String contraseña) {
        return usuarioRepository.findUsuarioByEmailAndContraseña(email,contraseña);
    }
}
