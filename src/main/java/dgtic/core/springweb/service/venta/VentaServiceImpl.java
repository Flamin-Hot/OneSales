package dgtic.core.springweb.service.venta;

import dgtic.core.springweb.model.*;
import dgtic.core.springweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaServiceImpl implements VentaService{

    @Autowired
    VentaRepository ventaRepository;

    @Autowired
    DetalleVentaRepository detalleVentaRepository;

    @Autowired
    ProductoRepository productoRepository;

    @Autowired
    MetodoPagoRepository metodoPagoRepository;

    @Autowired
    FacturaRepository facturaRepository;

    @Override
    public VentaEntity obtenerVentaPorId(Integer id) {
        Optional<VentaEntity> venta = ventaRepository.findById(id);
        if (venta!=null){
            return venta.get();
        }else {
            return null;
        }
    }

    @Override
    public VentaEntity nuevaVenta(VentaEntity venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public void eliminarVenta(Integer id) {
        List<DetalleVentaEntity> detalles = detalleVentaRepository.findByVentaId(id);
        if (detalles.isEmpty()){
            ventaRepository.deleteById(id);
            return;
        }else {
            if (facturaRepository.findByVentaId(id)!=null){
                throw new IllegalStateException("La venta no puede ser eliminada porque ya ha sido facturada.");
            }else {
                for (DetalleVentaEntity detalle: detalles) {
                    Optional<ProductoEntity> producto = productoRepository.findById(detalle.getProducto().getId());
                    producto.get().getInventario().setStock(producto.get().getInventario().getStock()+detalle.getCantidad());
                    productoRepository.save(producto.get());
                    detalleVentaRepository.delete(detalle);
                }
                ventaRepository.deleteById(id);
            }
        }
    }

    @Override
    public void finalizarVenta(VentaEntity ventaNueva, List<DetalleVentaEntity> detallesTemporales,Integer idVenta) {
        if (idVenta!=null){
            if (!detallesTemporales.isEmpty()) {
                Double totalVenta = 0.0;
                for (DetalleVentaEntity detalle : detallesTemporales) {
                    DetalleVentaEntity detalleVenta = detalleVentaRepository.save(detalle);
                    if (detalleVenta != null) {
                        Optional<ProductoEntity> producto = productoRepository.findById(detalleVenta.getProducto().getId());
                        if (producto.isPresent()) {
                            producto.get().getInventario().setStock(
                                    producto.get().getInventario().getStock() - detalle.getCantidad());
                            productoRepository.save(producto.get());
                        }
                    }
                    totalVenta += detalle.getProducto().getPrecio() * detalle.getCantidad();
                }
                totalVenta = Math.round(totalVenta * 100.0) / 100.0;
                ventaNueva.setTotal(totalVenta);
                ventaNueva.setMetodoPago(metodoPagoRepository.findById(idVenta).get());
                ventaRepository.save(ventaNueva);
            } else {
                throw new IllegalStateException("La lista de productos está vacía.");
            }
        }else {
            throw new IllegalStateException("No existe ese metodo de pago.");
        }
    }

    @Override
    public void agregarProductoAlDetalle(VentaEntity ventaNueva, List<DetalleVentaEntity> detallesTemporales, DetalleVentaEntity nuevoDetalle) {
        ProductoEntity productoEncontrado = productoRepository.findByNombre(nuevoDetalle.getProducto().getNombre());

        if (productoEncontrado != null) {
            if (productoEncontrado.getInventario().getStock() >= nuevoDetalle.getCantidad()) {
                detallesTemporales.removeIf(detalle -> Objects.equals(detalle.getProducto().getId(), productoEncontrado.getId()));
                nuevoDetalle.setVenta(ventaNueva);
                nuevoDetalle.setProducto(productoEncontrado);
                detallesTemporales.add(nuevoDetalle);
            } else {
                throw new IllegalStateException("No hay suficiente stock para el producto.");
            }
        } else {
            throw new IllegalStateException("Producto no encontrado.");
        }
    }

    @Override
    public Page<VentaEntity> findAll(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    @Override
    public VentaEntity iniciarNuevaVenta(UsuarioEntity usuario) {
        VentaEntity venta = new VentaEntity();
        venta.setUsuario(usuario);
        venta.setTotal(0.0);
        venta.setFecha(java.sql.Date.valueOf(LocalDate.now()));
        return venta;
    }

    @Override
    public List<BalanceMetodoPagoDTO> obtenerBalanceMetodoPago(Date fecha,Integer id) {
        List<Object[]> resultados = ventaRepository.obtenerDatosBalanceMetodoPago(fecha,id);
        return resultados.stream()
                .map(resultado -> new BalanceMetodoPagoDTO(
                        (String) resultado[0],
                        ((Number) resultado[1]).doubleValue(),
                        ((Number) resultado[2]).longValue()))
                .collect(Collectors.toList());
    }
}
