package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity,Integer> {
}
