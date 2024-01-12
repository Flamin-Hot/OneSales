package dgtic.core.springweb.service.factura;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import dgtic.core.springweb.model.*;
import dgtic.core.springweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaServiceImpl implements FacturaService{


    @Autowired
    FacturaRepository facturaRepository;
    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    VentaRepository ventaRepository;
    @Autowired
    DetalleVentaRepository detalleVentaRepository;


    @Override
    public FacturaEntity nuevaFactura(FacturaEntity factura) {
        UsuarioEntity usuario = usuarioRepository.findById(factura.getUsuario().getId())
                .orElseThrow(() -> new IllegalStateException("El usuario que quiere crear la factura no existe!"));

        ClienteEntity cliente = clienteRepository.findClienteByEmail(factura.getCliente().getEmail());
        if (cliente == null) {
            throw new IllegalStateException("El cliente asociado a la factura no existe!");
        }

        VentaEntity venta = ventaRepository.findById(factura.getVenta().getId())
                .orElseThrow(() -> new IllegalStateException("El número de ticket asociado a la factura no existe!"));

        if (venta.getCliente().getId() != cliente.getId()) {
            throw new IllegalStateException("El cliente asignado a la factura no es el cliente registrado en la venta!");
        }

        try {
            FacturaEntity nuevaFactura = new FacturaEntity(usuario, cliente, venta, Date.valueOf(LocalDate.now()));
            return facturaRepository.save(nuevaFactura);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Hubo un error al generar la factura!");
        }
    }


    @Override
    public void generarFactura(FacturaEntity factura) {
        try {
            String nombreArchivo = "Factura_" + factura.getHash() + ".pdf";

            File file = new File("src/main/resources/pdf/" + nombreArchivo);
            FileOutputStream archivo = new FileOutputStream(file);

            Document doc = new Document();
            PdfWriter.getInstance(doc, archivo);
            doc.open();

            Image imagen = Image.getInstance("src/main/resources/static/imagen/logo.png");
            Paragraph fecha = new Paragraph();
            Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);
            fecha.add(Chunk.NEWLINE);
            fecha.add("Factura: " + factura.getId()  + "\nFecha: " + factura.getFecha() + "\n\n");

            PdfPTable Encabezado = new PdfPTable(4);
            Encabezado.setWidthPercentage(100);
            Encabezado.getDefaultCell().setBorder(0);//quitar el borde de la tabla
            //tamaño de las celdas
            float[] ColumnaEncabezado = new float[]{20f, 30f, 70f, 40f};
            Encabezado.setWidths(ColumnaEncabezado);
            Encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);
            //agregar celdas
            Encabezado.addCell(imagen);

            String rfc = "ONE990405MME";
            String nombre = "One Sales";
            String telefono = "56-26-14-01-26";
            String direccion = "Houston Texas";

            Encabezado.addCell("");//celda vacia
            Encabezado.addCell("RFC: " + rfc + "\nNOMBRE: " + nombre + "\nTELEFONO: " + telefono + "\nDIRECCION: " + direccion);
            Encabezado.addCell(fecha);
            doc.add(Encabezado);

            //CUERPO
            Paragraph cliente = new Paragraph();
            cliente.add(Chunk.NEWLINE);//nueva linea
            cliente.add("Datos del cliente: " + "\n\n");
            doc.add(cliente);

            //DATOS DEL CLIENTE
            PdfPTable tablaCliente = new PdfPTable(4);
            tablaCliente.setWidthPercentage(100);
            tablaCliente.getDefaultCell().setBorder(0);//quitar bordes
            //tamaño de las celdas
            float[] columnaCliente = new float[]{45, 45f, 80f, 45f};
            tablaCliente.setWidths(columnaCliente);
            tablaCliente.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell rfcCliente = new PdfPCell(new Phrase("RFC: ", negrita));
            PdfPCell nombreCliente = new PdfPCell(new Phrase("Nombre: ", negrita));
            PdfPCell telefonoCliente = new PdfPCell(new Phrase("Telefono: ", negrita));
            PdfPCell emailCliente = new PdfPCell(new Phrase("Correo: ", negrita));
            //quitar bordes
            rfcCliente.setBorder(0);
            nombreCliente.setBorder(0);
            telefonoCliente.setBorder(0);
            emailCliente.setBorder(0);
            //agg celda a la tabla
            tablaCliente.addCell(rfcCliente);
            tablaCliente.addCell(nombreCliente);
            tablaCliente.addCell(emailCliente);
            tablaCliente.addCell(telefonoCliente);
            tablaCliente.addCell(factura.getCliente().getRfc());
            tablaCliente.addCell(factura.getCliente().getNombre()+" "+factura.getCliente().getApellido());
            tablaCliente.addCell(factura.getCliente().getEmail());
            tablaCliente.addCell(factura.getCliente().getTelefono());
            //agregar al documento
            doc.add(tablaCliente);

            //ESPACIO EN BLANCO
            Paragraph espacio = new Paragraph();
            espacio.add(Chunk.NEWLINE);
            espacio.add("");
            espacio.setAlignment(Element.ALIGN_CENTER);
            doc.add(espacio);

            //AGREGAR LOS PRODUCTOS
            List<DetalleVentaEntity> productos = detalleVentaRepository.findByVentaId(factura.getVenta().getId());
            PdfPTable tablaProducto = new PdfPTable(5);
            tablaProducto.setWidthPercentage(100);
            tablaProducto.getDefaultCell().setBorder(0);
            //tamaño de celdas
            float[] columnaProducto = new float[]{15f, 50f, 15f, 15f, 20f};
            tablaProducto.setWidths(columnaProducto);
            tablaProducto.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell columna1 = new PdfPCell(new Phrase("Cantidad: ", negrita));
            PdfPCell columna2 = new PdfPCell(new Phrase("Descripcion: ", negrita));
            PdfPCell columna3 = new PdfPCell(new Phrase("Precio Unitario: ", negrita));
            PdfPCell columna4 = new PdfPCell(new Phrase("IVA: ",negrita));
            PdfPCell columna5 = new PdfPCell(new Phrase("Precio Total: ", negrita));
            //quitar bordes
            columna1.setBorder(0);
            columna2.setBorder(0);
            columna3.setBorder(0);
            columna4.setBorder(0);
            columna5.setBorder(0);
            //agregar color al encabezado del producto
            columna1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            columna2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            columna3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            columna4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            columna5.setBackgroundColor(BaseColor.LIGHT_GRAY);
            //agg celda a la tabla
            tablaProducto.addCell(columna1);
            tablaProducto.addCell(columna2);
            tablaProducto.addCell(columna3);
            tablaProducto.addCell(columna4);
            tablaProducto.addCell(columna5);

            for (DetalleVentaEntity detalle: productos) {
                tablaProducto.addCell(detalle.getCantidad().toString());
                tablaProducto.addCell(detalle.getProducto().getNombre());
                tablaProducto.addCell(detalle.getProducto().getPrecio().toString());
                tablaProducto.addCell(String.format("%.2f", detalle.getProducto().getPrecio() * .16));
                tablaProducto.addCell(String.valueOf(detalle.getProducto().getPrecio()*detalle.getCantidad()));
            }

            //agregar al documento
            doc.add(tablaProducto);

            //Total pagar
            Paragraph info = new Paragraph();
            info.add(Chunk.NEWLINE);
            info.add("Total a pagar: " + factura.getVenta().getTotal());
            info.setAlignment(Element.ALIGN_RIGHT);
            doc.add(info);

            //HASH
            Paragraph claveHash = new Paragraph();
            claveHash.add(Chunk.NEWLINE);
            claveHash.add("Certificado: " + factura.getHash());
            info.setAlignment(Element.ALIGN_LEFT);
            doc.add(claveHash);

            //QR
            Paragraph qr = new Paragraph();
            qr.add(Chunk.NEWLINE);
            Image imagenqr = Image.getInstance("src/main/resources/static/imagen/qrcode.png");
            qr.add(imagenqr);
            info.setAlignment(Element.ALIGN_LEFT);
            doc.add(qr);

            Paragraph mensaje = new Paragraph();
            mensaje.add(Chunk.NEWLINE);
            mensaje.add("¡Gracias por su compra!");
            mensaje.setAlignment(Element.ALIGN_CENTER);
            doc.add(mensaje);

            //cerrar el documento y el archivo
            doc.close();
            archivo.close();
        } catch (DocumentException | IOException e) {
            System.out.println("Error en: " + e);
        }
    }
}
