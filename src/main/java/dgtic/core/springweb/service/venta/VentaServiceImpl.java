package dgtic.core.springweb.service.venta;

import dgtic.core.springweb.model.VentaEntity;
import dgtic.core.springweb.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService{

    @Autowired
    VentaRepository ventaRepository;

    @Override
    public VentaEntity obtenerVentaPorId(Integer id) {
        Optional<VentaEntity> venta = ventaRepository.findById(id);
        if (venta!=null){
            return venta.get();
        }else {
            return null;
        }
    }

    @Override
    public VentaEntity nuevaVenta(VentaEntity venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public void eliminarVenta(Integer id) {
        ventaRepository.deleteById(id);
    }
}
