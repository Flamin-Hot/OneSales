package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.ClienteEntity;
import dgtic.core.springweb.model.ProductoEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity,Integer> {
    @Query("select p from producto p where p.nombre like %?1%")
    public List<ProductoEntity> findByNombrePatron(String nombre);

    @EntityGraph(attributePaths = "inventario")
    ProductoEntity findByNombre(String nombre);

}
