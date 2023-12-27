package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<ProductoEntity,Integer> {
}
