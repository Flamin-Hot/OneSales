package dgtic.core.springweb.controller.usuario;

import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.service.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    PasswordEncoder passwordEncoder;

    //Controlador para dar alta un usuario
    @GetMapping("alta-usuario")
    public String altaUsuario(Model model){
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        model.addAttribute("metodo","Alta de Usuarios");
        model.addAttribute("usuarioEntity",usuarioEntity);
        return "usuario/alta-usuario";
    }

    //Controlador para guardar un usuario
    @PostMapping("formulario")
    public String registrarUsuario(@Valid @ModelAttribute("usuarioEntity")UsuarioEntity usuario, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("metodo", "Alta de Usuarios");
            return "usuario/alta-usuario";
        }
        try {
            if (usuario.getId()==null){
                usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
                usuarioService.guardar(usuario);
            }
            return "redirect:/";
        }catch (Exception e) {
            bindingResult.rejectValue("email", "error.usuarioEntity", "Correo ya existe!");
            model.addAttribute("metodo","Alta de Usuarios");
        }
        return "usuario/alta-usuario";
    }
}
