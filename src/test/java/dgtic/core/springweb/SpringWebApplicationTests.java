package dgtic.core.springweb;

import dgtic.core.springweb.model.*;
import dgtic.core.springweb.repository.*;
import dgtic.core.springweb.service.factura.FacturaService;
import dgtic.core.springweb.service.factura.FacturaServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
class SpringWebApplicationTests {

	@Autowired
	DetalleVentaRepository detalleVentaRepository;

	@Autowired
	ClienteRepository clienteRepository;

	@Autowired
	UsuarioRepository usuarioRepository;

	@Autowired
	VentaRepository ventaRepository;

	@Autowired
	FacturaServiceImpl facturaService;

	@Autowired
	FacturaRepository facturaRepository;

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

	@Test
	void z(){
		System.out.println(ventaRepository.obtenerFechaTotal());
	}

	@Test
	void w(){
		Optional<FacturaEntity> factura = facturaRepository.findById(1);
		facturaService.generarFactura(factura.get());
	}

	@Test
	void a() throws NoSuchAlgorithmException {
		FacturaEntity factura = new FacturaEntity(usuarioRepository.findById(1).get(),clienteRepository.findById(3).get(),ventaRepository.findById(1).get(),Date.valueOf("2024-01-10"));
		facturaService.generarFactura(facturaRepository.save(factura));
	}

	@Test
	void b() {
		List<Object[]> resultados = ventaRepository.obtenerDatosBalanceMetodoPago(Date.valueOf(LocalDate.now()),1);
		List<BalanceMetodoPagoDTO> x = resultados.stream()
				.map(resultado -> new BalanceMetodoPagoDTO(
						(String) resultado[0],
						((Number) resultado[1]).doubleValue(),
						((Number) resultado[2]).longValue()))
				.collect(Collectors.toList());
		x.forEach(System.out::println);
	}
}
