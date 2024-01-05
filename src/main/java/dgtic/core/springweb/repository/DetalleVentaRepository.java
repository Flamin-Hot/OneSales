package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.DetalleVentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVentaEntity,Integer> {

    List<DetalleVentaEntity> findByVentaId(Integer idVenta);
}
