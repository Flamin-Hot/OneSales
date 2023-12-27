package dgtic.core.springweb.service.producto;

import dgtic.core.springweb.model.ProductoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoService {
    Page<ProductoEntity> findAll(Pageable pageable);
    void guardar(ProductoEntity producto);
    void borrar(Integer id);
    ProductoEntity buscarProductoId(Integer id);
}
