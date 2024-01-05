package dgtic.core.springweb.service.venta;

import dgtic.core.springweb.model.DetalleVentaEntity;
import dgtic.core.springweb.model.ProductoEntity;
import dgtic.core.springweb.model.VentaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VentaService {
    VentaEntity obtenerVentaPorId(Integer id);
    VentaEntity nuevaVenta(VentaEntity venta);
    void eliminarVenta(Integer id);
    void finalizarVenta(VentaEntity ventaNueva, List<DetalleVentaEntity> detallesTemporales);
    void agregarProductoAlDetalle(VentaEntity ventaNueva, List<DetalleVentaEntity> detallesTemporales, DetalleVentaEntity nuevoDetalle);
    Page<VentaEntity> findAll(Pageable pageable);
}
