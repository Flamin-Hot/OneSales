package dgtic.core.springweb.controller.cliente;

import dgtic.core.springweb.model.CategoriaEntity;
import dgtic.core.springweb.model.ClienteEntity;
import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.service.cliente.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cliente")
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    //Cuando se presione el boton para dar de alta un nuevo cliente este controlador
    //debe tomar la solicitud, la solicitud es de tipo get, pues con el boton estamos
    //inidicando que se debe mostrar y dirigir a la pagina para dar de alta a un cliente
    @GetMapping("alta-cliente")
    public String altaCliente(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }
        //Debemos declarar un objeto de tipo CategoriaEntity pues esta pagina contiene un
        //formulario que va a ser llenando y mapeado al objeto que estamos pasando
        ClienteEntity clienteEntity = new ClienteEntity();
        //Mediante el model enviamos los objetos a usar en esta pagina
        model.addAttribute("metodo","Registrar Cliente");
        model.addAttribute("clienteEntity",clienteEntity);
        //Nos vamos a la pagina correspondiente
        return "cliente/alta-cliente";
    }

    //Este controlador se hara cargo de mapear los datos del formulario al objeto
    //ademas de guardarlo en la bd
    @PostMapping("formulario")
    public String registrarCliente(@Valid @ModelAttribute("clienteEntity")ClienteEntity cliente, BindingResult bindingResult, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        if (bindingResult.hasErrors()) {
            return "cliente/alta-cliente";
        }

        try {
            if (cliente.getId()==null){
                clienteService.guardar(cliente);
                return "redirect:/cliente/lista-cliente";
            }else{
                clienteService.guardar(cliente);
                return "redirect:/cliente/lista-cliente";
            }
        }catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                // Aquí puedes verificar si el correo electrónico o el teléfono están duplicados
                if (e.getMessage().contains("cliente.email")) {
                    bindingResult.rejectValue("email", "error.clienteEntity", "El correo ya se encuentra registrado!");
                } else if (e.getMessage().contains("cliente.telefono")) {
                    bindingResult.rejectValue("telefono", "error.clienteEntity", "El teléfono ya se encuentra registrado!");
                }
            }
            return "cliente/alta-cliente";
        }
    }
    
}
