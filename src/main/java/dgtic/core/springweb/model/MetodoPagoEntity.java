package dgtic.core.springweb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "metodo_pago")
public class MetodoPagoEntity {
    @Id
    private Integer id;
    private String metodo;
}
