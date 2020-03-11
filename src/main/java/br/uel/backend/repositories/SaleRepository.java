package br.uel.backend.repositories;

import br.uel.backend.models.Client;
import br.uel.backend.models.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale,Long> {
    List<Sale> findByStatus(int status);
    List<Sale> findByClient(Client client);
}
