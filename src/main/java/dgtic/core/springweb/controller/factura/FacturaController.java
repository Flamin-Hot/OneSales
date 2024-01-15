package dgtic.core.springweb.controller.factura;

import dgtic.core.springweb.model.*;
import dgtic.core.springweb.repository.FacturaRepository;
import dgtic.core.springweb.service.factura.FacturaService;
import dgtic.core.springweb.util.RenderPagina;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Controller
@RequestMapping("factura")
public class FacturaController {

    @Autowired
    FacturaService facturaService;

    @Autowired
    FacturaRepository facturaRepository;

    //Controaldor para dar de alta una factura
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

    //Controlador para crear la factura
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

    //Controaldor para mostrar la factura
    @GetMapping("/mostrar/{fileName:.+}")
    public ResponseEntity<Resource> mostrarFactura(@PathVariable String fileName) throws IOException {
        Path path = Paths.get("src/main/resources/pdf/" + fileName);
        Resource resource = new org.springframework.core.io.UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    //Controaldor para mostrar la lista de facturas generadas
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
        model.addAttribute("operacion","Facturas Generadas");
        //Mandamos a la pagina correspondiente cuando se apriete lista-factura
        return "factura/lista-factura";
    }

    //Controaldor para enviar por correo la factura
    @GetMapping("enviar-factura/{id}")
    public String enviarMail(@PathVariable(name = "id") Integer id,Model model, RedirectAttributes redirectAttributes){
        Optional<FacturaEntity> factura = facturaRepository.findById(id);
        if (factura.isPresent()){
            gmail(factura.get());
            redirectAttributes.addFlashAttribute("success","Factura enviada por correo correctamente!");
        }else {
            redirectAttributes.addFlashAttribute("error","No se encontro la factura");
        }
        return "redirect:/factura/lista-factura";
    }

    //Metodo para enviar el correo
    private void gmail(FacturaEntity factura){
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

        try{
            Session session=Session.getInstance(p,null);
            MimeMessage message=new MimeMessage(session);
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(factura.getCliente().getEmail(),false));
            message.setSubject("Factura Generada One Sales");

            //Tratamos de obtener el pdf de la ruta especificada
            String path = "src/main/resources/pdf/Factura_"+factura.getHash()+".pdf";

            File pdf = new File(path);
            if (!pdf.exists()){
                System.out.println("No existe la factura en la ruta");
                return;
            }

            //Si existe el pdf lo adjuntamos al correo
            BodyPart archivo = new MimeBodyPart();
            archivo.setDataHandler(new DataHandler(new FileDataSource(path)));
            archivo.setFileName("Factura_" + factura.getHash() + ".pdf");

            // Crear el cuerpo del mensaje
            StringBuilder cuerpoCorreo = new StringBuilder();
            cuerpoCorreo.append("<h2>¡Gracias por tu compra en Ones Sales!</h2>");
            cuerpoCorreo.append("<p>Número de Factura: ").append(factura.getId()).append("</p>");
            cuerpoCorreo.append("<p>Nombre del Vendedor: ").append(factura.getUsuario().getNombre()).append("</p>");
            cuerpoCorreo.append("<p>Fecha de Factura: ").append(new SimpleDateFormat("dd/MM/yyyy").format(factura.getFecha())).append("</p>");


            BodyPart texto = new MimeBodyPart();
            texto.setContent(cuerpoCorreo.toString(), "text/html");

            // Crear el cuerpo del mensaje
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(texto);
            multipart.addBodyPart(archivo);

            // Asignar el contenido al mensaje
            message.setContent(multipart);

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
