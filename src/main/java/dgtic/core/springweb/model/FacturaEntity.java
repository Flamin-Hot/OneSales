package dgtic.core.springweb.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;

@Data
@NoArgsConstructor
@Entity(name = "factura")
public class FacturaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente")
    @Valid
    private ClienteEntity cliente;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_venta")
    @Valid
    private VentaEntity venta;

    @NotNull
    private Date fecha;

    @Column(name = "clave_hash")
    @NotNull
    private String hash;

    public String createSHAHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public FacturaEntity(UsuarioEntity usuario,ClienteEntity cliente,VentaEntity venta,Date fecha) throws NoSuchAlgorithmException {
        this.usuario = usuario;
        this.cliente = cliente;
        this.venta = venta;
        this.fecha = fecha;
        this.hash = createSHAHash(cliente.getEmail()+"-"+venta.getId());
    }

}
