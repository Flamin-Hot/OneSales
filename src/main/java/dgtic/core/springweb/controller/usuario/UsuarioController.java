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
//Establecemos el prefijo de las rutas para este controlador
@RequestMapping("usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    PasswordEncoder passwordEncoder;

    //El boton register nos trajo a este controlador pues en su ref indicamos que el boton debia buscar este controlador
    //Hacemos un get para mostrar el formulario de alta usuario
    @GetMapping("alta-usuario")
    public String altaUsuario(Model model){
        //A la pagina de alta usuario le mandamos un nuevo usuario pues es lo que queremos construir con este formulario
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        //Con model.addAttribute mandamos datos desde el controlador hacia la vista
        //En este caso mandamos a la nueva vista (alta-usuario) una cadena que Thyme puede usar en la vista
        model.addAttribute("metodo","Alta de Usuarios");
        model.addAttribute("usuarioEntity",usuarioEntity);
        return "usuario/alta-usuario"; //inidcamos a donde se debe dirigir
    }

    //Este es el controlador que debera de encargarse de manejar el formulario de registro
    @PostMapping("formulario")
    //Recibe el usuarioEntity que se esta tratando de mapear con los datos ingresados en el formulario
    public String registrarUsuario(@Valid @ModelAttribute("usuarioEntity")UsuarioEntity usuario, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            // Si hay errores de validación, regresa a la página de formulario con los errores y el metodo
            model.addAttribute("metodo", "Alta de Usuarios");
            return "usuario/alta-usuario";
        }
        try {
            //Intentamos almacenar el objeto que se fue construyendo en el formulario
            if (usuario.getId()==null){
                usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
                usuarioService.guardar(usuario);
            }
            //Si el guardado fue correcto nos redirigimos a la pagina de inicio para ello spring busca el controalador que peuda manejar el redirect
            return "redirect:/";
        }catch (Exception e) {
            //Agregamos el error a mostrar
            bindingResult.rejectValue("email", "error.usuarioEntity", "Correo ya existe!");
            //Le volvemos a mandar a la pagina el atributo
            model.addAttribute("metodo","Alta de Usuarios");
        }
        return "usuario/alta-usuario";
    }
}
