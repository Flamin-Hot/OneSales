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

    //Controlador para mostrar la pagina alta-categoria
    @GetMapping("alta-categoria")
    public String altaCategoria(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        CategoriaEntity categoriaEntity = new CategoriaEntity();

        model.addAttribute("metodo","Registrar Categoría");
        model.addAttribute("categoriaEntity",categoriaEntity);

        return "categoria/alta-categoria";
    }

    //Controlador para crear la categoria
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


    //Controlador para mostrar la lista de categorias registradas
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

        model.addAttribute("page",render);

        model.addAttribute("categoria",categoria);

        model.addAttribute("operacion","Categorias Resgistradas");

        return "categoria/lista-categoria";
    }

    //Controlador para manejar la edicion de una categoria
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

    //Controlador para manejar la eliminacion de una categoria
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

