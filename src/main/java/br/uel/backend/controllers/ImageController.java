package br.uel.backend.controllers;

import br.uel.backend.config.JwtTokenUtil;
import br.uel.backend.models.User;
import br.uel.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @GetMapping(produces = "image/*",path = "/products/{id}")
    public @ResponseBody ResponseEntity<?> getImageProduct(@PathVariable String id) throws IOException {
        InputStream in;
        try{
            in = getClass()
                    .getResourceAsStream("/images/products/"+id);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(in.readAllBytes());
    }

    @GetMapping(produces = "image/*",path = "/receipts/{id}")
    public @ResponseBody ResponseEntity<?> getImageReceipt(@PathVariable String id,@RequestHeader("Authorization")String token) throws IOException {
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user.getPermission()==0)
            return ResponseEntity.status(401).build();

        InputStream in = getClass()
                .getResourceAsStream("/images/receipts/"+id);
        return ResponseEntity.ok(in.readAllBytes());
    }
}
