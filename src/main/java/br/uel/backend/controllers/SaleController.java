package br.uel.backend.controllers;

import br.uel.backend.config.JwtTokenUtil;
import br.uel.backend.models.Client;
import br.uel.backend.models.Sale;
import br.uel.backend.models.User;
import br.uel.backend.repositories.ClientRepository;
import br.uel.backend.repositories.SaleRepository;
import br.uel.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
public class SaleController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    SaleRepository saleRepository;

    @GetMapping
    public ResponseEntity<?> list(@RequestHeader("Authorization") String token){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);
        return clientRepository.findByUser(user)
                .map(client -> ResponseEntity.ok(saleRepository.findByClient(client)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token){

        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        return clientRepository.findByUser(user)
                .map(client -> {
                    Sale newSale = new Sale();
                    newSale.setClient(client);
                    newSale.setStatus(0);
                    newSale.setTotal(0);
                    Sale s=saleRepository.save(newSale);
                    return ResponseEntity.ok(s);
                })
                .orElse(ResponseEntity.notFound().build());

    }

    @PostMapping(path = "/finish")
    public ResponseEntity<?> finish(@RequestHeader("Authorization") String token, @RequestHeader("sale_id") Long saleId){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        return saleRepository.findById(saleId)
                .map(sale -> {
                    String emailSale = sale.getClient().getUser().getEmail();
                    if(email.compareTo(emailSale)!=0)
                        return ResponseEntity.status(401).build();
                    sale.setStatus(1);
                    return ResponseEntity.ok(sale);
                })
                .orElse(ResponseEntity.notFound().build());
    }


}
