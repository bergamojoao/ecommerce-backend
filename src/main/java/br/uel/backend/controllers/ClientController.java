package br.uel.backend.controllers;

import br.uel.backend.config.JwtTokenUtil;
import br.uel.backend.models.Client;
import br.uel.backend.models.User;
import br.uel.backend.repositories.ClientRepository;
import br.uel.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    ClientRepository clientRepository;

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token, @RequestBody Client client){
        if(token==null || !token.startsWith("Bearer"))
            return ResponseEntity.status(401).build();

        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);
        client.setUser(user);

        return ResponseEntity.ok(clientRepository.save(client));

    }

    @GetMapping
    public ResponseEntity<?> list(@RequestHeader("Authorization") String token){

        if(token==null || !token.startsWith("Bearer"))
            return ResponseEntity.status(401).build();

        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user.getPermission()==0)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(clientRepository.findAll());
    }
}
