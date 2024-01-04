package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.DetalleVentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVentaEntity,Integer> {
}
