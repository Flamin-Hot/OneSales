package dgtic.core.springweb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity(name = "categoria")
public class CategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "categoria",cascade = CascadeType.REMOVE)
    private List<ProductoEntity> productos;

}
