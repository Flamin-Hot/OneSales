package dgtic.core.springweb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceMetodoPagoDTO {
    private String metodo;
    private Double total;
    private Long cantidad;
}
