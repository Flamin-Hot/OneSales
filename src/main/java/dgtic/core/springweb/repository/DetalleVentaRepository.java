package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.DetalleVentaEntity;
import dgtic.core.springweb.model.ProductoCantidadDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVentaEntity,Integer> {

    List<DetalleVentaEntity> findByVentaId(Integer idVenta);

    @Query("SELECT new dgtic.core.springweb.model.ProductoCantidadDTO(dv.producto.nombre, SUM(dv.cantidad)) FROM detalle_venta dv GROUP BY dv.producto.nombre ORDER BY SUM(dv.cantidad) DESC")
    List<ProductoCantidadDTO> obtenerTopProductos();

}
