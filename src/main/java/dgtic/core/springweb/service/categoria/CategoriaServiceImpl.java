package dgtic.core.springweb.service.categoria;

import dgtic.core.springweb.model.CategoriaEntity;
import dgtic.core.springweb.model.ProductoEntity;
import dgtic.core.springweb.repository.CategoriaRepository;
import dgtic.core.springweb.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService{

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    ProductoRepository productoRepository;

    @Override
    public Page<CategoriaEntity> findAll(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }

    @Override
    public void guardar(CategoriaEntity categoria) {
        categoriaRepository.save(categoria);
    }

    @Override
    public void borrar(Integer id) {
        CategoriaEntity categoriaReemplazo = categoriaRepository.findById(1).orElse(null);

        if (categoriaReemplazo != null) {
            List<ProductoEntity> productos = productoRepository.findAllByCategoriaId(id);

            for (ProductoEntity prod : productos) {
                prod.setCategoria(categoriaReemplazo);
                productoRepository.save(prod);
            }
        }

        categoriaRepository.deleteById(id);
    }


    @Override
    public CategoriaEntity buscarCategoriaId(Integer id) {
        return categoriaRepository.findById(id).get();
    }

    @Override
    public List<CategoriaEntity> buscarCategoria() {
        return categoriaRepository.findAll();
    }
}
