package dgtic.core.springweb.controller.categoria;

import dgtic.core.springweb.model.CategoriaEntity;
import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.service.categoria.CategoriaService;
import dgtic.core.springweb.util.RenderPagina;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("categoria")
public class CategoriaController {
    @Autowired
    CategoriaService categoriaService;


    //Cuando se presione el boton para agregar una nueva categoria este controlador
    //debe tomar la solicitud, la solicitud es de tipo get, pues con el boton estamos
    //inidicando que se debe mostrar y dirigir a la pagina para dar de alta una categoria
    @GetMapping("alta-categoria")
    public String altaCategoria(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }
        //Debemos declarar un objeto de tipo CategoriaEntity pues esta pagina contiene un
        //formulario que va a ser llenando y mapeado al objeto que estamos pasando
        CategoriaEntity categoriaEntity = new CategoriaEntity();
        //Mediante el model enviamos los objetos a usar en esta pagina
        model.addAttribute("metodo","Registrar Categoría");
        model.addAttribute("categoriaEntity",categoriaEntity);
        //Nos vamos a la pagina correspondiente
        return "categoria/alta-categoria";
    }

    //Este controlador se hara cargo de mapear los datos del formulario al objeto
    //ademas de guardarlo en la bd
    @PostMapping("formulario")
    public String registrarCategoria(@Valid @ModelAttribute("categoriaEntity")CategoriaEntity categoria, BindingResult bindingResult,Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }
        if (bindingResult.hasErrors()) {
            return "categoria/alta-categoria";
        }
        try {
            if (categoria.getId()==null){
                categoriaService.guardar(categoria);
                return "redirect:/categoria/lista-categoria";
            }else{
                categoriaService.guardar(categoria);
                return "redirect:/categoria/lista-categoria";
            }
        }catch (Exception e){
            bindingResult.rejectValue("nombre","error.categoriaEntity","El nombre de la categoría ya existe!");
        }
        return "categoria/alta-categoria";
    }



    @GetMapping("lista-categoria")
    //Recibe la session del controlador post de login
    public String paginaLista(@RequestParam(name = "page",defaultValue = "0") int page,HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        Pageable pagReq = PageRequest.of(page,5);
        Page<CategoriaEntity> categoria = categoriaService.findAll(pagReq);
        RenderPagina<CategoriaEntity> render = new RenderPagina<>("lista-categoria",categoria);
        //Mandamos al front lo que queremos que se muestre
        model.addAttribute("page",render);
        //Y las entidades que debe mostrar
        model.addAttribute("categoria",categoria);
        //Ademas de un titulo de lo que se esta mostrando
        model.addAttribute("operacion","Categorias Resgistradas");
        //Mandamos a la pagina correspondiente cuando se apriete lista-cliente
        return "categoria/lista-categoria";
    }

    @GetMapping("modificar-categoria/{id}")
    public String saltoModificar(@PathVariable("id") Integer id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }
        CategoriaEntity categoria = categoriaService.buscarCategoriaId(id);
        model.addAttribute("categoriaEntity", categoria);
        return "categoria/alta-categoria";
    }

    @GetMapping("borrar-categoria/{id}")
    public String borrarCategoria(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        CategoriaEntity categoria = categoriaService.buscarCategoriaId(id);
        if (categoria != null) {
            categoriaService.borrar(id);
            redirectAttributes.addFlashAttribute("success", "Categoria borrada exitosamente!");
        }
        return "redirect:/categoria/lista-categoria";
    }




}

