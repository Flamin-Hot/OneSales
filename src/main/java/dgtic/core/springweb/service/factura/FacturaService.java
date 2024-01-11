package dgtic.core.springweb.service.factura;

import dgtic.core.springweb.model.ClienteEntity;
import dgtic.core.springweb.model.FacturaEntity;
import dgtic.core.springweb.model.VentaEntity;

public interface FacturaService {
    void generarFactura(FacturaEntity factura);
}
