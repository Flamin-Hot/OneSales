package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.FacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<FacturaEntity,Integer> {
    FacturaEntity findByVentaId(Integer id);
}
