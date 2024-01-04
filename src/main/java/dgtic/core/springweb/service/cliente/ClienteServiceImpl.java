package dgtic.core.springweb.service.cliente;

import dgtic.core.springweb.model.ClienteEntity;
import dgtic.core.springweb.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class ClienteServiceImpl implements ClienteService{

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public Page<ClienteEntity> findAll(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public void guardar(ClienteEntity cliente) {
        if (cliente!=null){
            clienteRepository.save(cliente);
        }
    }

    @Override
    public void borrar(Integer id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public ClienteEntity buscarClienteId(Integer id) {
        return clienteRepository.findById(id).get();
    }

    @Override
    public List<ClienteEntity> buscarClientes() {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteEntity> buscarClientePatron(String patron) {
        return clienteRepository.findByEmail(patron);
    }
}
