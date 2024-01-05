package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioEntity,Integer> {
}
