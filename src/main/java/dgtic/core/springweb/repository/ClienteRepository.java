package dgtic.core.springweb.repository;

import dgtic.core.springweb.model.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity,Integer> {
    @Query("select c from cliente c where c.email like %?1%")
    public List<ClienteEntity> findByEmail(String email);

    public ClienteEntity findClienteByEmail(String email);

}
