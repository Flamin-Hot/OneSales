package dgtic.core.springweb.controller;

import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.service.usuario.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class InicioController {

    //Inyectamos el servicio para realizar operaciones
    @Autowired
    UsuarioService usuarioService;

    //Cuando se despliega la aplicacion hacemos un get para obtener la pagina de inicio
    @GetMapping("")
    public String paginaInicio(Model model) {
        //La pagina de inicio muestra un formulario para iniciar sesion o registrar un usuario
        return "inicio";
    }

    //Este controlador es de tipo post pues con la informacion insertada en los campos del login intentamos hacer una validacion -->
    /*
    @PostMapping("login")
    //Los parametros recibidos es la informacion colocada en las cajas de texto
    public String login(@RequestParam("email") String email, @RequestParam("contraseña") String contraseña, HttpSession session, Model model) {
        try {
            UsuarioEntity usuarioEntity = usuarioService.login(email, contraseña);
            if (usuarioEntity != null) {
                // Dado que si se encontro un usuario con los parametros indicados, guardamos en la session al usuario y el mensaje de bienvenida
                session.setAttribute("usuarioEntity", usuarioEntity);
                session.setAttribute("inicio", "Bienvenido");
                //Ademas re dirigimos al controlador encargado de mostrar la pagina aplicacion
                return "redirect:/aplicacion";
            } else {
                // Usuario no encontrado
                model.addAttribute("warning", "Correo o contraseña incorrectos!");
                return "inicio";
            }
        } catch (Exception e) {
            return "inicio";
        }
    }*/

    //Es es el controlador encargado de mostrar la pagina aplicacion
    //Es un get pues indicamos que debe mostrar la pagina y algunos atributos de la session
    @GetMapping("aplicacion")
    public String aplicacion(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        return "aplicacion";
    }

}

