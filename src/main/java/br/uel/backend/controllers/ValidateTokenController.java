package br.uel.backend.controllers;

import br.uel.backend.config.JwtTokenUtil;
import br.uel.backend.services.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validate")
public class ValidateTokenController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    JwtUserDetailsService jwtUserDetailsService;

    @GetMapping
    public ResponseEntity<?> valid(@RequestHeader("Authorization")String token){

        if(token==null || !token.startsWith("Bearer"))
            return ResponseEntity.status(401).build();

        String jwt = token.substring(7);

        String email = jwtTokenUtil.getUsernameFromToken(jwt);
        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email);
        if(jwtTokenUtil.validateToken(jwt,userDetails))
            return ResponseEntity.ok().build();
        else return ResponseEntity.status(401).build();
    }
}
