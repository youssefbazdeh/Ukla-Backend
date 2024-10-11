package spark.ukla.ad_feature.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client,Long> {
    Page<Client> findAll(Pageable pageable);
    long count();
    boolean existsByCompanyName(String name);
    Client findByCompanyName(String name);
}
