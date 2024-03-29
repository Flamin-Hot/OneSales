package dgtic.core.springweb.controller.producto;

import dgtic.core.springweb.model.CategoriaEntity;
import dgtic.core.springweb.model.InventarioEntity;
import dgtic.core.springweb.model.ProductoEntity;
import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.service.categoria.CategoriaService;
import dgtic.core.springweb.service.producto.ProductoService;
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

import java.util.List;

@Controller
@RequestMapping("producto")
public class ProductoController {
    @Autowired
    ProductoService productoService;

    @Autowired
    CategoriaService categoriaService;

    //Controlador para dar alta un producto
    @GetMapping("alta-producto")
    public String altaProducto(Model model){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();
        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        ProductoEntity productoEntity = new ProductoEntity();

        List<CategoriaEntity> select = categoriaService.buscarCategoria();

        model.addAttribute("metodo","Registrar Producto");
        model.addAttribute("productoEntity",productoEntity);
        model.addAttribute("selectCategoria",select);

        return "producto/alta-producto";
    }

    //Controlador para guardar el producto
    @PostMapping("formulario")
    public String registrarProducto(@Valid @ModelAttribute("productoEntity")ProductoEntity producto, BindingResult bindingResult, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        List<CategoriaEntity> select = categoriaService.buscarCategoria();
        if (bindingResult.hasErrors()) {
            model.addAttribute("selectCategoria",select);
            model.addAttribute("warning","Hubo un error!");
            return "producto/alta-producto";
        }

        try {
            if (producto.getId() == null){
                System.out.println(producto);
                productoService.guardar(producto);
                return "redirect:/producto/lista-producto";
            }else {
                producto.setInventario(producto.getInventario());
                System.out.println(producto);
                productoService.guardar(producto);
                return "redirect:/producto/lista-producto";
            }
        }catch (Exception e){
            bindingResult.rejectValue("inventario.stock","error.productoEntity","Unicamente debes ingresar numeros positivos");
        }
        model.addAttribute("selectCategoria",select);
        return "producto/alta-producto";
    }


    ///Controlador para mostrar la lista de productos
    @GetMapping("lista-producto")
    public String paginaLista(@RequestParam(name = "page",defaultValue = "0") int page,Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        Pageable pagReq = PageRequest.of(page,5);
        Page<ProductoEntity> producto = productoService.findAll(pagReq);
        RenderPagina<ProductoEntity> render = new RenderPagina<>("lista-producto",producto);

        model.addAttribute("page",render);

        model.addAttribute("producto",producto);

        model.addAttribute("operacion","Productos Resgistrados");

        return "producto/lista-producto";
    }

    //Controlador para modificar un producto
    @GetMapping("modificar-producto/{id}")
    public String saltoModificar(@PathVariable("id") Integer id, Model model,HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        ProductoEntity producto = productoService.buscarProductoId(id);
        model.addAttribute("productoEntity", producto);

        List<CategoriaEntity> select=categoriaService.buscarCategoria();
        model.addAttribute("selectCategoria",select);
        return "producto/alta-producto";
    }

    //Controlador para eliminar un producto
    @GetMapping("borrar-producto/{id}")
    public String borrarCategoria(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes){
        ProductoEntity producto = productoService.buscarProductoId(id);
        if (producto != null){
            productoService.borrar(id);
            redirectAttributes.addAttribute("success","Producto borrado exitosamente!");
        }
        return "redirect:/producto/lista-producto";
    }
}
