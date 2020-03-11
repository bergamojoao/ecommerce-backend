package br.uel.backend.repositories;

import br.uel.backend.models.Client;
import br.uel.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client,Long> {
    Optional<Client> findByUser(User user);
}
