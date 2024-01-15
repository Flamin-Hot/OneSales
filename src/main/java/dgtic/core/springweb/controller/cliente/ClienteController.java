package dgtic.core.springweb.controller.cliente;

import dgtic.core.springweb.model.CategoriaEntity;
import dgtic.core.springweb.model.ClienteEntity;
import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.service.cliente.ClienteService;
import dgtic.core.springweb.util.RenderPagina;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("cliente")
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    //Controlador para mostrar el formulario de agregar cliente
    @GetMapping("alta-cliente")
    public String altaCliente(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        ClienteEntity clienteEntity = new ClienteEntity();

        model.addAttribute("metodo", "Registrar Cliente");
        model.addAttribute("clienteEntity", clienteEntity);

        return "cliente/alta-cliente";
    }


    //Controlador para insertar un cliente
    @PostMapping("formulario")
    public String registrarCliente(@Valid @ModelAttribute("clienteEntity") ClienteEntity cliente, BindingResult bindingResult, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        if (bindingResult.hasErrors()) {
            return "cliente/alta-cliente";
        }

        try {
            if (cliente.getId() == null) {
                clienteService.guardar(cliente);
                return "redirect:/cliente/lista-cliente";
            } else {
                clienteService.guardar(cliente);
                return "redirect:/cliente/lista-cliente";
            }
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {

                if (e.getMessage().contains("cliente.email")) {
                    bindingResult.rejectValue("email", "error.clienteEntity", "El correo ya se encuentra registrado!");
                } else if (e.getMessage().contains("cliente.telefono")) {
                    bindingResult.rejectValue("telefono", "error.clienteEntity", "El teléfono ya se encuentra registrado!");
                }
            }
            return "cliente/alta-cliente";
        }
    }

    //Controlador para maostrar la lista de clientes
    @GetMapping("lista-cliente")
    public String paginaLista(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        Pageable pagReq = PageRequest.of(page, 5);
        Page<ClienteEntity> cliente = clienteService.findAll(pagReq);
        RenderPagina<ClienteEntity> render = new RenderPagina<>("lista-cliente", cliente);

        model.addAttribute("page", render);

        model.addAttribute("cliente", cliente);

        model.addAttribute("operacion", "Clientes Resgistrados");

        return "cliente/lista-cliente";
    }

    //Controlador para modificar un cliente
    @GetMapping("modificar-cliente/{id}")
    public String saltoModificar(@PathVariable("id") Integer id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }
        ClienteEntity cliente = clienteService.buscarClienteId(id);
        model.addAttribute("clienteEntity", cliente);
        return "cliente/alta-cliente";
    }

    //Controaldor para eliminar un cliente
    @GetMapping("borrar-cliente/{id}")
    public String borrarCliente(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        ClienteEntity cliente = clienteService.buscarClienteId(id);
        if (cliente != null) {
            try {
                clienteService.borrar(id);
                redirectAttributes.addFlashAttribute("success", "Cliente borrado exitosamente!");
            } catch (DataIntegrityViolationException e) {
                redirectAttributes.addFlashAttribute("error", "No se puede borrar el cliente debido a ventas asociadas");
            }
        }
        return "redirect:/cliente/lista-cliente";
    }

}
