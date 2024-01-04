package dgtic.core.springweb.controller.venta;

import dgtic.core.springweb.model.*;
import dgtic.core.springweb.repository.ClienteRepository;
import dgtic.core.springweb.repository.ProductoRepository;
import dgtic.core.springweb.service.cliente.ClienteService;
import dgtic.core.springweb.service.producto.ProductoService;
import dgtic.core.springweb.service.venta.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("venta")
public class VentaController {
    @Autowired
    VentaService ventaService;

    @Autowired
    ClienteService clienteService;

    @Autowired
    ProductoService productoService;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ProductoRepository productoRepository;

    @GetMapping("nueva-venta")
    public String nuevaVenta(Model model, HttpSession session){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        VentaEntity venta = new VentaEntity();
        venta.setUsuario(usuarioEntity);
        venta.setTotal(0.0);
        venta.setFecha(new Date());

        // Almacenar la venta en la sesión
        session.setAttribute("venta", venta);

        model.addAttribute("usuarioEntity", usuarioEntity);
        model.addAttribute("ventaEntity",venta);

        return "venta/nueva-venta";
    }

    @PostMapping("nueva-venta")
    public String nuevaVenta(@ModelAttribute("ventaEntity") VentaEntity venta, HttpSession session){
        VentaEntity ventaSession = (VentaEntity) session.getAttribute("venta");
        // Tomamos el correo ingresado en el formulario para asignarlo al objeto que ya teníamos en sesión
        ClienteEntity cliente = clienteRepository.findClienteByEmail(venta.getCliente().getEmail());
        ventaSession.setCliente(cliente);
        // Guardamos la venta en la sesión antes de redirigir
        session.setAttribute("ventaNueva", ventaService.nuevaVenta(ventaSession));
        session.setAttribute("ventaIniciada", true);
        return "redirect:/venta/detalle-venta";
    }

    @GetMapping("detalle-venta")
    public String detalleVenta(Model model, HttpSession session) {

        // Comprueba si la venta existe en la sesión
        if (session.getAttribute("ventaIniciada") == null) {
            // Redirige a nueva-venta si no existe
            return "redirect:/venta/nueva-venta";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();
        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        List<DetalleVentaEntity> detallesTemporales = (List<DetalleVentaEntity>) session.getAttribute("detallesTemporales");

        if (detallesTemporales == null) {
            detallesTemporales = new ArrayList<>();
            session.setAttribute("detallesTemporales", detallesTemporales);
        }

        DetalleVentaEntity detalleVenta = new DetalleVentaEntity();
        ProductoEntity producto = new ProductoEntity();
        detalleVenta.setProducto(producto);

        model.addAttribute("detalleVentaEntity", detalleVenta);
        model.addAttribute("detallesTemporales", detallesTemporales);


        return "venta/detalle-venta";
    }

    @PostMapping("detalle-venta/agregar-producto")
    public String agregarProductoAlDetalle(@ModelAttribute("detalleVentaEntity") DetalleVentaEntity detalleVenta,
                                           HttpSession session, RedirectAttributes redirectAttributes) {

        // Obtener la venta recién creada de la sesión
        VentaEntity ventaNueva = (VentaEntity) session.getAttribute("ventaNueva");
        List<DetalleVentaEntity> detallesTemporales = (List<DetalleVentaEntity>) session.getAttribute("detallesTemporales");

        // Verificar si la venta existe en la sesión y si el usuario está autenticado
        if (ventaNueva == null || !session.getAttribute("ventaIniciada").equals(true)) {
            // Redirigir a la creación de una nueva venta si no existe
            return "redirect:/venta/nueva-venta";
        }

        // Buscar el producto por nombre
        ProductoEntity productoEncontrado = productoRepository.findByNombre(detalleVenta.getProducto().getNombre());

        if (productoEncontrado != null) {
            // Verificar si hay suficiente stock para la cantidad ingresada
            if (productoEncontrado.getInventario().getStock() >= detalleVenta.getCantidad()) {
                // Remover el detalle existente para el mismo producto, si hay uno
                detallesTemporales.removeIf(detalle -> Objects.equals(detalle.getProducto().getId(), productoEncontrado.getId()));

                // Crear un nuevo detalle y agregarlo a la lista temporal
                detalleVenta.setVenta(ventaNueva);
                detalleVenta.setProducto(productoEncontrado);
                detallesTemporales.add(detalleVenta);
                System.out.println("----------");
                for (DetalleVentaEntity detalle: detallesTemporales) {
                    System.out.println(detalle.getVenta()+" "+ detalle.getProducto().getId()+" "+detalle.getCantidad());
                }
                System.out.println("----------");

                // Redirigir a la página para agregar otro producto
                return "redirect:/venta/detalle-venta";
            } else {
                // No hay suficiente stock, agregar mensaje de error y redirigir al formulario
                redirectAttributes.addFlashAttribute("error", "No hay suficiente stock para el producto.");
                return "redirect:/venta/detalle-venta";
            }
        } else {
            // Producto no encontrado, agregar mensaje de error y redirigir al formulario
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/venta/detalle-venta";
        }
    }

    @PostMapping("detalle-venta/eliminar-detalle")
    public String eliminarDetalle(@RequestParam("detalleIndex") int detalleIndex, RedirectAttributes redirectAttributes,HttpSession session) {
        List<DetalleVentaEntity> detallesTemporales = (List<DetalleVentaEntity>) session.getAttribute("detallesTemporales");
        if (detalleIndex >= 0 && detalleIndex < detallesTemporales.size()) {
            detallesTemporales.remove(detalleIndex);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente de la venta.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Índice de detalle no válido.");
        }
        return "redirect:/venta/detalle-venta";
    }

    @GetMapping("eliminar-venta")
    public String eliminarVenta(HttpSession session) {
        VentaEntity ventaNueva = (VentaEntity) session.getAttribute("ventaNueva");

        if (ventaNueva != null) {
            List<DetalleVentaEntity> detallesTemporales = (List<DetalleVentaEntity>) session.getAttribute("detallesTemporales");
            detallesTemporales.clear();
            ventaService.eliminarVenta(ventaNueva.getId());
            session.removeAttribute("ventaNueva");
            session.removeAttribute("ventaIniciada");
        }

        return "redirect:/";
    }

    @GetMapping(value = "buscar-cliente/{dato}",produces = "application/json")
    public @ResponseBody List<ClienteEntity> buscarCliente(@PathVariable String dato){
        return clienteService.buscarClientePatron(dato);
    }

    @GetMapping(value = "buscar-producto/{nombre}",produces = "application/json")
    public @ResponseBody List<ProductoEntity> buscarProducto(@PathVariable String nombre){
        return productoService.buscarProductoPatron(nombre);
    }

}
