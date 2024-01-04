package dgtic.core.springweb.service.venta;

import dgtic.core.springweb.model.VentaEntity;

public interface VentaService {
    VentaEntity obtenerVentaPorId(Integer id);
    VentaEntity nuevaVenta(VentaEntity venta);
    void eliminarVenta(Integer id);
}
