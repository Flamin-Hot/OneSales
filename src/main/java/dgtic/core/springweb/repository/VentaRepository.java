package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.FechaTotalDTO;
import dgtic.core.springweb.model.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.sql.Date;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<VentaEntity,Integer> {
    List<VentaEntity> findAllVentaByUsuarioIdAndFecha(Integer id, Date date);
    @Query("SELECT NEW dgtic.core.springweb.model.FechaTotalDTO(v.fecha, SUM(v.total)) FROM venta v GROUP BY v.fecha")
    List<FechaTotalDTO> obtenerFechaTotal();
}
