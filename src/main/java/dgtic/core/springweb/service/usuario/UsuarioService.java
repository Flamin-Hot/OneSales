package dgtic.core.springweb.service.usuario;

import dgtic.core.springweb.model.UsuarioEntity;

public interface UsuarioService {
    void guardar(UsuarioEntity usuario);
    void borrar(Integer id);
    UsuarioEntity login(String email,String contraseña);
}
