package spark.ukla.ad_feature.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ClientService {
    @Autowired
    ClientRepository clientRepository;

    public void addClient(Client client) {
        clientRepository.save(client);
    }
    public boolean updateClient(Client client, long id) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("client not found ! "));
        if (!existingClient.getCompanyName().matches(client.getCompanyName())){
            if (existsByName(client.getCompanyName())){
                return false;
            }
            existingClient.setCompanyName(client.getCompanyName());

        }
        existingClient.setPhoneNumber(client.getPhoneNumber());
        existingClient.setEmail(client.getEmail());
        existingClient.setTaxRegistrationNumber(client.getTaxRegistrationNumber());
        existingClient.setAddress(client.getAddress());
        clientRepository.save(existingClient);
        return true;

    }
    public void deleteClient(Long idClient) {
        clientRepository.deleteById(idClient);
    }

    public List<Client> getClientsWithPagination(int page, int size){
        Pageable pageable;
        if (page == 0 && size == 0) {
            return clientRepository.findAll();

        }

        pageable = PageRequest.of(page - 1, size);

        Page<Client> firstPage = clientRepository.findAll(pageable);
        return  firstPage.getContent();
    }
    public List<Client> getClients(){
        return clientRepository.findAll();
    }
    public long getClientCount() {
        return clientRepository.count();
    }

    public Client retrieveById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isPresent()){
            return client.get();
        }else
            return null;
    }
    public Boolean existsByName(String name) {
        return clientRepository.existsByCompanyName(name) ;
    }
}
