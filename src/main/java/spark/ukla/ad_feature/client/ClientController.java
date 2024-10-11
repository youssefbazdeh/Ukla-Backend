package spark.ukla.ad_feature.client;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/Client")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientController {
    private final ClientService clientService;
    public ClientController(ClientService clientService){
        this.clientService=clientService;
    }

    @GetMapping("/getClients/{pageNo}/{pageSize}")
    public ResponseEntity<List<Client>> getClients(@PathVariable int pageNo,
                                             @PathVariable int pageSize) {
        List<Client> clientsWithPagination = clientService.getClientsWithPagination(pageNo,pageSize);
        if (clientsWithPagination!= null &&!clientsWithPagination.isEmpty()) {
            return new ResponseEntity<>(clientsWithPagination, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getAllClients")
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getClients();
        if (clients!= null &&!clients.isEmpty()) {
            return new ResponseEntity<>(clients, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> retrieveById(@PathVariable Long id){
        Client client = clientService.retrieveById(id);
        if(client != null)
            return new ResponseEntity<>(client, HttpStatus.OK);
        else
            return new ResponseEntity<>("client does not exist", HttpStatus.NOT_FOUND);
    }
    @GetMapping("/countClient")
    public ResponseEntity<Long> getClientCount() {
        long count = clientService.getClientCount();
        return ResponseEntity.ok(count);
    }
    @PostMapping(value = "/addClient")
    public ResponseEntity<String> addClient(@Valid @RequestPart("client") Client client ){
        if(clientService.existsByName(client.getCompanyName())){
            return new ResponseEntity<>("company name exists", HttpStatus.NOT_ACCEPTABLE);
        }
        clientService.addClient(client);
        return new ResponseEntity<>("Created", HttpStatus.CREATED);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<String> updateclient(@Valid @RequestPart("client") Client client,
                                               @PathVariable Long id) {
        if (clientService.updateClient(client,id)) {
            return new ResponseEntity<>("updated", HttpStatus.OK);

        } else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);

    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        clientService.deleteClient(id);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }
}
