package dgtic.core.springweb.service.producto;

import dgtic.core.springweb.model.ProductoEntity;
import dgtic.core.springweb.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService{

    @Autowired
    ProductoRepository productoRepository;

    @Override
    public Page<ProductoEntity> findAll(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    @Override
    public void guardar(ProductoEntity producto) {
        productoRepository.save(producto);
    }

    @Override
    public void borrar(Integer id) {
        productoRepository.deleteById(id);
    }

    @Override
    public ProductoEntity buscarProductoId(Integer id) {
        return productoRepository.findById(id).get();
    }

    @Override
    public List<ProductoEntity> buscarProductoPatron(String patron) {
        return productoRepository.findByNombrePatron(patron);
    }
}
