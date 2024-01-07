package dgtic.core.springweb;

import dgtic.core.springweb.model.ProductoCantidadDTO;
import dgtic.core.springweb.model.VentaEntity;
import dgtic.core.springweb.repository.DetalleVentaRepository;
import dgtic.core.springweb.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class SpringWebApplicationTests {

	@Autowired
	DetalleVentaRepository detalleVentaRepository;

	@Autowired
	VentaRepository ventaRepository;

	@Test
	void contextLoads() {
		List<ProductoCantidadDTO> detalles = detalleVentaRepository.obtenerTopProductos();
		System.out.println(detalles.subList(0,2));
	}

	@Test
	void x(){
		List<VentaEntity> ventas = ventaRepository.findAll();
		for (VentaEntity venta : ventas) {
			System.out.println(venta);
		}
	}

	@Test
	void y(){
		// Obtén la fecha actual
		LocalDate fechaActual = LocalDate.now();

		// Conviértelo a java.sql.Date
		Date fechaSql = Date.valueOf(fechaActual);

		System.out.println(ventaRepository.findAllVentaByUsuarioIdAndFecha(6, fechaSql));
	}
}
