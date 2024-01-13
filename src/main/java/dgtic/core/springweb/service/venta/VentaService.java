package dgtic.core.springweb.service.venta;

import dgtic.core.springweb.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.List;

public interface VentaService {
    VentaEntity obtenerVentaPorId(Integer id);
    VentaEntity nuevaVenta(VentaEntity venta);
    void eliminarVenta(Integer id);
    void finalizarVenta(VentaEntity ventaNueva, List<DetalleVentaEntity> detallesTemporales,Integer idVenta);
    void agregarProductoAlDetalle(VentaEntity ventaNueva, List<DetalleVentaEntity> detallesTemporales, DetalleVentaEntity nuevoDetalle);
    Page<VentaEntity> findAll(Pageable pageable);
    VentaEntity iniciarNuevaVenta(UsuarioEntity usuario);
    List<BalanceMetodoPagoDTO> obtenerBalanceMetodoPago(Date fecha,Integer id);
}
