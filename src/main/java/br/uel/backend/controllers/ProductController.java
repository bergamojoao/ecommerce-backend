package br.uel.backend.controllers;

import br.uel.backend.models.Product;
import br.uel.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepository repository;

    @GetMapping
    public List<Product> list(){
        return repository.findByActiveTrue();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> index(@PathVariable String id){
        return repository.findByCodProd(id)
                .map(product -> ResponseEntity.ok().body(product))
                .orElse(ResponseEntity.notFound().build());
    }
}
