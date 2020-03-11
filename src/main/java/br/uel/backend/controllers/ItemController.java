package br.uel.backend.controllers;

import br.uel.backend.config.JwtTokenUtil;
import br.uel.backend.models.Sale;
import br.uel.backend.repositories.ProductRepository;
import br.uel.backend.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    SaleRepository saleRepository;

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/{saleId}")
    public ResponseEntity<?> index(@RequestHeader("Authorization") String token, @PathVariable Long saleId){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        return saleRepository.findById(saleId)
                .map(sale -> {
                    String emailSale = sale.getClient().getUser().getEmail();
                    if(email.compareTo(emailSale)!=0)
                        return ResponseEntity.status(401).build();

                    return ResponseEntity.ok(sale);
                })
                .orElse(ResponseEntity.notFound().build());

    }


    @PostMapping("/{codProd}")
    public ResponseEntity<?> add(@RequestHeader("Authorization") String token, @RequestHeader("sale_id") Long saleId, @PathVariable String codProd){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        return saleRepository.findById(saleId)
                .map(sale -> {
                    String emailSale = sale.getClient().getUser().getEmail();
                    if(email.compareTo(emailSale)!=0)
                        return ResponseEntity.status(401).build();
                    if(sale.getStatus()!=0)
                        return ResponseEntity.status(401).build();

                    return productRepository.findByCodProd(codProd).map(product -> {
                        sale.addProduct(product);
                        Sale s = saleRepository.save(sale);
                        return ResponseEntity.ok(s);
                    }).orElse(ResponseEntity.notFound().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{codProd}")
    public ResponseEntity<?> remove(@RequestHeader("Authorization") String token, @RequestHeader("sale_id") Long saleId, @PathVariable String codProd){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        return saleRepository.findById(saleId)
                .map(sale -> {
                    String emailSale = sale.getClient().getUser().getEmail();
                    if(email.compareTo(emailSale)!=0)
                        return ResponseEntity.status(401).build();
                    if(sale.getStatus()!=0)
                        return ResponseEntity.status(401).build();
                    sale.removeProduct(codProd);
                    Sale s= saleRepository.save(sale);
                    return ResponseEntity.ok(s);
                })
                .orElse(ResponseEntity.notFound().build());
    }




}

