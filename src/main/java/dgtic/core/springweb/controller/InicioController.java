package dgtic.core.springweb.controller;

import dgtic.core.springweb.model.BalanceDTO;
import dgtic.core.springweb.model.ProductoCantidadDTO;
import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.model.VentaEntity;
import dgtic.core.springweb.repository.DetalleVentaRepository;
import dgtic.core.springweb.repository.VentaRepository;
import dgtic.core.springweb.service.usuario.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class InicioController {

    //Inyectamos el servicio para realizar operaciones
    @Autowired
    DetalleVentaRepository detalleVentaRepository;

    @Autowired
    VentaRepository ventaRepository;


    //Cuando se despliega la aplicacion hacemos un get para obtener la pagina de inicio
    @GetMapping("")
    public String paginaInicio(Model model) {
        //La pagina de inicio muestra un formulario para iniciar sesion o registrar un usuario
        return "inicio";
    }

    //Es es el controlador encargado de mostrar la pagina aplicacion
    //Es un get pues indicamos que debe mostrar la pagina y algunos atributos de la session
    @GetMapping("aplicacion")
    public String aplicacion(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (authentication.getPrincipal() instanceof UsuarioEntity) ? (UsuarioEntity) authentication.getPrincipal() : null;

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        // Mostramos el Balance
        Date fechaActual = Date.valueOf(LocalDate.now());
        List<VentaEntity> venta = (usuarioEntity != null) ? ventaRepository.findAllVentaByUsuarioIdAndFecha(usuarioEntity.getId(), fechaActual) : Collections.emptyList();

        if (!venta.isEmpty()) {
            // Cálculo del total usando Streams
            Double total = venta.stream().mapToDouble(VentaEntity::getTotal).sum();

            BalanceDTO balance = new BalanceDTO(usuarioEntity.getNombre(), venta.size(), total, fechaActual);
            model.addAttribute("venta", balance);

        } else {
            BalanceDTO balance = new BalanceDTO(usuarioEntity.getNombre(),0,0.0,fechaActual);
            model.addAttribute("venta", balance);
        }

        // Mostramos el Dashboard
        List<ProductoCantidadDTO> pc = detalleVentaRepository.obtenerTopProductos();
        List<ProductoCantidadDTO> primeros5 = pc.subList(0, Math.min(5, pc.size()));
        List<ProductoCantidadDTO> ultimos5 = pc.subList(Math.max(0, pc.size() - 5), pc.size());

        model.addAttribute("labels", primeros5.stream().map(ProductoCantidadDTO::getNombre).collect(Collectors.toList()));
        model.addAttribute("cantidades", primeros5.stream().map(ProductoCantidadDTO::getCantidad).collect(Collectors.toList()));
        model.addAttribute("labelsmenos", ultimos5.stream().map(ProductoCantidadDTO::getNombre).collect(Collectors.toList()));
        model.addAttribute("cantidadesmenos", ultimos5.stream().map(ProductoCantidadDTO::getCantidad).collect(Collectors.toList()));

        return "aplicacion";
    }



}

