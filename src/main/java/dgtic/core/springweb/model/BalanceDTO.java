package dgtic.core.springweb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDTO {
    String nombre;
    Integer cantidad;
    Double total;
    Date fecha;
}
