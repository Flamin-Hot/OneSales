package dgtic.core.springweb.service.factura;

import dgtic.core.springweb.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FacturaService {
    Page<FacturaEntity> findAll(Pageable pageable);
    FacturaEntity nuevaFactura(FacturaEntity factura);
    void generarFactura(FacturaEntity factura);
}
