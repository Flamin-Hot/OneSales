package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<VentaEntity,Integer> {
}
