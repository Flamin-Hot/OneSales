package dgtic.core.springweb.controller;

import dgtic.core.springweb.model.*;
import dgtic.core.springweb.repository.DetalleVentaRepository;
import dgtic.core.springweb.repository.VentaRepository;
import dgtic.core.springweb.service.usuario.UsuarioService;
import dgtic.core.springweb.service.venta.VentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    @Autowired
    VentaService ventaService;

    //Cuando se despliega la aplicacion hacemos un get para obtener la pagina de inicio
    @GetMapping("")
    public String paginaInicio(Model model) {
        //La pagina de inicio muestra un formulario para iniciar sesion o registrar un usuario
        return "inicio";
    }

    //Es es el controlador encargado de mostrar la pagina aplicacion
    //Es un get pues indicamos que debe mostrar la pagina y algunos atributos de la session
    @GetMapping("aplicacion")
    public String aplicacion(@RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (authentication.getPrincipal() instanceof UsuarioEntity) ? (UsuarioEntity) authentication.getPrincipal() : null;

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }

        if (fecha == null) {
            fecha = LocalDate.now();
        }

        List<VentaEntity> venta = (usuarioEntity != null) ? ventaRepository.findAllVentaByUsuarioIdAndFecha(usuarioEntity.getId(), Date.valueOf(fecha)) : Collections.emptyList();
        List<BalanceMetodoPagoDTO> detalle = (usuarioEntity != null) ? ventaService.obtenerBalanceMetodoPago(Date.valueOf(fecha),usuarioEntity.getId()) : Collections.emptyList();


        if (!venta.isEmpty()) {
            // Cálculo del total usando Streams
            Double total = venta.stream().mapToDouble(VentaEntity::getTotal).sum();

            BalanceDTO balance = new BalanceDTO(usuarioEntity.getNombre(), venta.size(), total, Date.valueOf(fecha));
            model.addAttribute("detalle", detalle);
            model.addAttribute("venta", balance);

        } else {
            detalle.add(new BalanceMetodoPagoDTO(" ",0.0,0L));
            BalanceDTO balance = new BalanceDTO(usuarioEntity.getNombre(), 0, 0.0, Date.valueOf(fecha));
            model.addAttribute("venta", balance);
            model.addAttribute("detalle", detalle);
        }

        // Mostramos el Dashboard
        List<ProductoCantidadDTO> pc = detalleVentaRepository.obtenerTopProductos();
        List<ProductoCantidadDTO> primeros5 = pc.subList(0, Math.min(5, pc.size()));
        List<ProductoCantidadDTO> ultimos5 = pc.subList(Math.max(0, pc.size() - 5), pc.size());
        model.addAttribute("labels", primeros5.stream().map(ProductoCantidadDTO::getNombre).collect(Collectors.toList()));
        model.addAttribute("cantidades", primeros5.stream().map(ProductoCantidadDTO::getCantidad).collect(Collectors.toList()));
        model.addAttribute("labelsmenos", ultimos5.stream().map(ProductoCantidadDTO::getNombre).collect(Collectors.toList()));
        model.addAttribute("cantidadesmenos", ultimos5.stream().map(ProductoCantidadDTO::getCantidad).collect(Collectors.toList()));

        List<FechaTotalDTO> ft = ventaRepository.obtenerFechaTotal();
        model.addAttribute("fechas", ft.stream().map(FechaTotalDTO::getFecha).collect(Collectors.toList()));
        model.addAttribute("totales", ft.stream().map(FechaTotalDTO::getTotal).collect(Collectors.toList()));

        return "aplicacion";
    }



}

