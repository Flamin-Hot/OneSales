package dgtic.core.springweb.service.categoria;

import dgtic.core.springweb.model.CategoriaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface CategoriaService {
    Page<CategoriaEntity> findAll(Pageable pageable);
    void guardar(CategoriaEntity categoria);
    void borrar(Integer id);
    CategoriaEntity buscarCategoriaId(Integer id);

    List<CategoriaEntity> buscarCategoria();
}
