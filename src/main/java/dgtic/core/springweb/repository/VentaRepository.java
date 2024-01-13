package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.BalanceMetodoPagoDTO;
import dgtic.core.springweb.model.FechaTotalDTO;
import dgtic.core.springweb.model.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.sql.Date;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<VentaEntity,Integer> {
    List<VentaEntity> findAllVentaByUsuarioIdAndFecha(Integer id, Date date);
    @Query("SELECT NEW dgtic.core.springweb.model.FechaTotalDTO(v.fecha, SUM(v.total)) FROM venta v GROUP BY v.fecha")
    List<FechaTotalDTO> obtenerFechaTotal();
    /*@Query("SELECT NEW dgtic.core.springweb.model.BalanceMetodoPagoDTO(mp.metodo, SUM(v.total) as total, COUNT(*) as cantidad) " +
            "FROM venta v " +
            "LEFT JOIN metodo_pago mp ON v.metodoPago.id = mp.id " +
            "WHERE v.fecha = :fecha AND v.id = :idUsuario " +
            "GROUP BY v.metodoPago.id")
    List<BalanceMetodoPagoDTO> obtenerBalanceMetodoPago(@Param("fecha") Date fecha, @Param("idUsuario") Integer idUsuario);*/
    /*@Query(value = "SELECT mp.metodo, SUM(v.total) as total, COUNT(*) as total_ventas " +
            "FROM venta v " +
            "LEFT JOIN metodo_pago mp ON v.id_metodo = mp.id " +
            "WHERE v.fecha = current_date() AND v.id_usuario = :idUsuario " +
            "GROUP BY v.id_metodo", nativeQuery = true)
    List<Object[]> obtenerDatosBalanceMetodoPago(@Param("idUsuario") Integer idUsuario);*/
    @Query(value = "SELECT mp.metodo, SUM(v.total) as total, COUNT(*) as total_ventas " +
            "FROM venta v " +
            "LEFT JOIN metodo_pago mp ON v.id_metodo = mp.id " +
            "WHERE v.fecha = :fecha AND v.id_usuario = :idUsuario " +
            "GROUP BY v.id_metodo", nativeQuery = true)
    List<Object[]> obtenerDatosBalanceMetodoPago(@Param("fecha") Date fecha, @Param("idUsuario") Integer idUsuario);

}
