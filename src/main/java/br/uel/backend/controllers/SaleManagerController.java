package br.uel.backend.controllers;

import br.uel.backend.config.JwtTokenUtil;
import br.uel.backend.models.Sale;
import br.uel.backend.models.User;
import br.uel.backend.repositories.SaleRepository;
import br.uel.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/sales")
public class SaleManagerController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    SaleRepository saleRepository;

    @GetMapping
    public ResponseEntity<?> list(@RequestHeader("Authorization") String token,@RequestParam(name="status",required = false) Integer status ){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user.getPermission()==0)
            return ResponseEntity.status(401).build();

        if(status==null)
            return ResponseEntity.ok(saleRepository.findAll());

        return ResponseEntity.ok(saleRepository.findByStatus(status));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> index(@RequestHeader("Authorization") String token,@PathVariable Long id){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user.getPermission()==0)
            return ResponseEntity.status(401).build();

        return saleRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Sale sale){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user.getPermission()==0)
            return ResponseEntity.status(401).build();

        return saleRepository.findById(id)
                .map(updatedSale -> {
                    updatedSale.setStatus(sale.getStatus());
                    return ResponseEntity.ok(saleRepository.save(updatedSale));
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token, @PathVariable Long id){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user.getPermission()==0)
            return ResponseEntity.status(401).build();

        return saleRepository.findById(id)
                .map(sale -> {
                    saleRepository.delete(sale);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
