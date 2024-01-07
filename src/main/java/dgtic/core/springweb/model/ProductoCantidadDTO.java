package dgtic.core.springweb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoCantidadDTO {
    String nombre;
    Long cantidad;
}
