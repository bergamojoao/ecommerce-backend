package br.uel.backend.controllers;

import br.uel.backend.config.JwtTokenUtil;
import br.uel.backend.repositories.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    SaleRepository saleRepository;

    private String BASE_DIR=System.getProperty("user.dir");

    @PostMapping
    public ResponseEntity<?> pay(@RequestHeader("Authorization") String token,@RequestParam("sale_id") Long saleId, @RequestPart("receipt") MultipartFile receipt){
        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        return saleRepository.findById(saleId)
                .map(sale -> {
                    String emailSale = sale.getClient().getUser().getEmail();
                    if(email.compareTo(emailSale)!=0)
                        return ResponseEntity.status(401).build();
                    if(sale.getReceipt()!=null)
                        return ResponseEntity.status(401).build();

                    String IMAGE_NAME="receipts/"+sale.getId()+"-"+sale.getClient().getId();

                    try {
                        receipt.transferTo(new File(this.BASE_DIR+"/src/main/resources/images/receipts"+IMAGE_NAME));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    sale.setReceipt(IMAGE_NAME);
                    sale.setStatus(2);
                    return ResponseEntity.ok(saleRepository.save(sale));

                })
                .orElse(ResponseEntity.notFound().build());
    }
}
