package dgtic.core.springweb.controller.factura;

import dgtic.core.springweb.model.FacturaEntity;
import dgtic.core.springweb.model.ProductoEntity;
import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.service.factura.FacturaService;
import dgtic.core.springweb.util.RenderPagina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("factura")
public class FacturaController {

    @Autowired
    FacturaService facturaService;

    @GetMapping("alta-factura")
    public String altaFactura(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        FacturaEntity factura = new FacturaEntity();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }
        model.addAttribute("facturaEntity",factura);
        return "factura/alta-factura";
    }

    @PostMapping("nueva-factura")
    public String nuevaVenta(@ModelAttribute("facturaEntity") FacturaEntity factura, RedirectAttributes redirectAttributes){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();
        factura.setUsuario(usuarioEntity);
        try {
            FacturaEntity facturaGenerada = facturaService.nuevaFactura(factura);
            facturaService.generarFactura(facturaGenerada);
            redirectAttributes.addFlashAttribute("success", "Factura generada satisfactoriamente!");
            return "redirect:/factura/mostrar/Factura_" + facturaGenerada.getHash() + ".pdf";
        }catch (IllegalStateException e){
            redirectAttributes.addFlashAttribute("warning", e.getMessage());
            return "redirect:/factura/alta-factura";
        }
    }

    @GetMapping("/mostrar/{fileName:.+}")
    public ResponseEntity<Resource> mostrarFactura(@PathVariable String fileName) throws IOException {
        Path path = Paths.get("src/main/resources/pdf/" + fileName);
        Resource resource = new org.springframework.core.io.UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    @GetMapping("lista-factura")
    public String paginaLista(@RequestParam(name = "page",defaultValue = "0") int page,Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioEntity usuarioEntity = (UsuarioEntity) authentication.getPrincipal();

        if (usuarioEntity != null) {
            model.addAttribute("usuarioEntity", usuarioEntity);
        }
        //Logica para mostar los productos
        Pageable pagReq = PageRequest.of(page,10);
        Page<FacturaEntity> factura = facturaService.findAll(pagReq);
        RenderPagina<FacturaEntity> render = new RenderPagina<>("lista-factura",factura);
        //Mandamos al front lo que queremos que se muestre
        model.addAttribute("page",render);
        //Y las entidades que debe mostrar
        model.addAttribute("factura",factura);
        //Ademas de un titulo de lo que se esta mostrando
        model.addAttribute("operacion","Factruas Generadas");
        //Mandamos a la pagina correspondiente cuando se apriete lista-factura
        return "factura/lista-factura";
    }

}
