package dgtic.core.springweb.controller.venta;

import dgtic.core.springweb.model.*;
import dgtic.core.springweb.repository.*;
import dgtic.core.springweb.service.cliente.ClienteService;
import dgtic.core.springweb.service.producto.ProductoService;
import dgtic.core.springweb.service.venta.VentaService;
import dgtic.core.springweb.util.RenderPagina;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

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
    VentaRepository ventaRepository;

    @Autowired
    DetalleVentaRepository detalleVentaRepository;


    @GetMapping("nueva-venta")
    public String nuevaVenta(Model model, HttpSession session){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        VentaEntity venta = new VentaEntity();
        venta.setUsuario(usuarioEntity);
        venta.setTotal(0.0);
        venta.setFecha(java.sql.Date.valueOf(LocalDate.now()));

        session.setAttribute("venta", venta);

        model.addAttribute("usuarioEntity", usuarioEntity);
        model.addAttribute("ventaEntity",venta);

        return "venta/nueva-venta";
    }

    @PostMapping("nueva-venta")
    public String nuevaVenta(@ModelAttribute("ventaEntity") VentaEntity venta, HttpSession session){
        VentaEntity ventaSession = (VentaEntity) session.getAttribute("venta");

        ClienteEntity cliente = clienteRepository.findClienteByEmail(venta.getCliente().getEmail());
        ventaSession.setCliente(cliente);

        session.setAttribute("ventaNueva", ventaService.nuevaVenta(ventaSession));
        session.setAttribute("ventaIniciada", true);
        return "redirect:/venta/detalle-venta";
    }

    @GetMapping("detalle-venta")
    public String detalleVenta(Model model, HttpSession session) {

        if (session.getAttribute("ventaIniciada") == null) {
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

        VentaEntity ventaNueva = (VentaEntity) session.getAttribute("ventaNueva");
        List<DetalleVentaEntity> detallesTemporales = (List<DetalleVentaEntity>) session.getAttribute("detallesTemporales");

        if (ventaNueva == null || !session.getAttribute("ventaIniciada").equals(true)) {
            return "redirect:/venta/nueva-venta";
        }

        try {
            ventaService.agregarProductoAlDetalle(ventaNueva, detallesTemporales, detalleVenta);
            return "redirect:/venta/detalle-venta";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/venta/detalle-venta";
        }
    }

    @PostMapping("detalle-venta/finalizar-venta")
    public String finalizarVenta(HttpSession session, RedirectAttributes redirectAttributes) {
        VentaEntity ventaNueva = (VentaEntity) session.getAttribute("ventaNueva");
        List<DetalleVentaEntity> detallesTemporales = (List<DetalleVentaEntity>) session.getAttribute("detallesTemporales");

        if (ventaNueva == null || !session.getAttribute("ventaIniciada").equals(true)) {
            return "redirect:/venta/nueva-venta";
        }

        try {
            ventaService.finalizarVenta(ventaNueva, detallesTemporales);
            detallesTemporales.clear();
            session.removeAttribute("ventaNueva");
            session.removeAttribute("ventaIniciada");
            return "redirect:/venta/lista-venta";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
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

        return "redirect:/aplicacion";
    }

    @GetMapping(value = "buscar-cliente/{dato}",produces = "application/json")
    public @ResponseBody List<ClienteEntity> buscarCliente(@PathVariable String dato){
        return clienteService.buscarClientePatron(dato);
    }

    @GetMapping(value = "buscar-producto/{nombre}",produces = "application/json")
    public @ResponseBody List<ProductoEntity> buscarProducto(@PathVariable String nombre){
        return productoService.buscarProductoPatron(nombre);
    }

    @GetMapping("lista-venta")
    public String paginaLista(@RequestParam(name = "page",defaultValue = "0") int page,Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }
        //Logica para mostar los productos
        Pageable pagReq = PageRequest.of(page,5);
        Page<VentaEntity> venta = ventaService.findAll(pagReq);
        RenderPagina<VentaEntity> render = new RenderPagina<>("lista-venta",venta);
        //Mandamos al front lo que queremos que se muestre
        model.addAttribute("page",render);
        //Y las entidades que debe mostrar
        model.addAttribute("venta",venta);
        //Ademas de un titulo de lo que se esta mostrando
        model.addAttribute("operacion","Ventas Realizadas");
        //Mandamos a la pagina correspondiente cuando se apriete lista-cliente
        return "venta/lista-venta";
    }

    @GetMapping("detalle-venta/{id}")
    public ResponseEntity<?> showDetalleVenta(@PathVariable("id") Integer id) {
        List<DetalleVentaEntity> detalleVenta = detalleVentaRepository.findByVentaId(id);
        if (detalleVenta != null) {
            return new ResponseEntity<>(detalleVenta, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Venta no encontrada", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("enviar-mail/{id}")
    public String enviarMail(@PathVariable(name = "id") Integer id,Model model, RedirectAttributes redirectAttributes){

        Optional<VentaEntity> venta = ventaRepository.findById(id);
        List<DetalleVentaEntity> detalleVenta = detalleVentaRepository.findByVentaId(venta.get().getId());
        if (venta.isPresent()){
            String correo = venta.get().getCliente().getEmail();
            gmail(venta.get(),detalleVenta);
            redirectAttributes.addFlashAttribute("success","Correo enviado correctamente!");
            return "redirect:/venta/lista-venta";
        }else {
            redirectAttributes.addFlashAttribute("error","No se encontro la venta");
            return "redirect:/venta/lista-venta";
        }
    }

    private void gmail(VentaEntity venta,List<DetalleVentaEntity> detalleVenta){
        String gmail="haloemiliano93@gmail.com";
        String pswd="ggej jmbg zqyw ckwp";
        Properties p=System.getProperties();
        p.setProperty("mail.smtps.host","smpt.gmail.com");
        p.setProperty("mail.smtps.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        p.setProperty("mail.smtps.socketFactory.fallback","false");
        p.setProperty("mail.smtp.port","465");
        p.setProperty("mail.smtp.socketFactory.port","465");
        p.setProperty("mail.smtps.auth","true");
        p.setProperty("mail.smtp.ssl.trust","smtp.gmail.com");
        p.setProperty("mail.smtps.ssl.trust","smtp.gmail.com");
        p.setProperty("mail.smtp.ssl.quitwait","false");

        //construcción del html
        StringBuilder cuerpoCorreo = new StringBuilder();
        cuerpoCorreo.append("<h2>¡Gracias por tu compra en Ones Sales!</h2>");
        cuerpoCorreo.append("<p>Número de Ticket: ").append(venta.getId()).append("</p>");
        cuerpoCorreo.append("<p>Nombre del Vendedor: ").append(venta.getUsuario().getNombre()).append("</p>");
        cuerpoCorreo.append("<p>Fecha de Compra: ").append(new SimpleDateFormat("dd/MM/yyyy").format(venta.getFecha())).append("</p>");

        // Lista de productos
        cuerpoCorreo.append("<h3>Detalle de la Compra:</h3>");
        cuerpoCorreo.append("<ul>");
        for (DetalleVentaEntity detalle : detalleVenta) {
            cuerpoCorreo.append("<li>").append(detalle.getProducto().getNombre()).append(" - Cantidad: ").append(detalle.getCantidad()).append("</li>");
        }
        cuerpoCorreo.append("</ul>");

        // Total de la venta
        cuerpoCorreo.append("<p>Total de la Compra: ").append(venta.getTotal()).append("</p>");

        try{
            Session session=Session.getInstance(p,null);
            MimeMessage message=new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(venta.getCliente().getEmail(),false));
            message.setSubject("Ticket de Compra");
            message.setContent(cuerpoCorreo.toString(),"text/html");
            message.setSentDate(new Date());
            Transport transport=(Transport)session.getTransport("smtps");
            transport.connect("smtp.gmail.com",gmail,pswd);
            transport.sendMessage(message,message.getAllRecipients());
            transport.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
