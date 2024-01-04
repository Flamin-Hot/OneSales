package dgtic.core.springweb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity(name = "producto")
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @DecimalMin(value = "0", message = "El precio no puede ser negativo")
    private Double precio;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inventario_id")
    private InventarioEntity inventario;

    @ManyToOne
    @JoinColumn(name = "categoria_id",referencedColumnName = "id")
    @JsonIgnore
    private CategoriaEntity categoria;
}
