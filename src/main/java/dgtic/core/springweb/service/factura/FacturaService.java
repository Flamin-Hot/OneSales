package dgtic.core.springweb.service.factura;

import dgtic.core.springweb.model.ClienteEntity;
import dgtic.core.springweb.model.FacturaEntity;
import dgtic.core.springweb.model.UsuarioEntity;
import dgtic.core.springweb.model.VentaEntity;

public interface FacturaService {

    FacturaEntity nuevaFactura(FacturaEntity factura);
    void generarFactura(FacturaEntity factura);
}
