package br.uel.backend.repositories;

import br.uel.backend.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByCodProd(String codProd);
    List<Product> findByActiveTrue();
}
