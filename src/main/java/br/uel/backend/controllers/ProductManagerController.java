package br.uel.backend.controllers;

import br.uel.backend.config.JwtTokenUtil;
import br.uel.backend.models.Product;
import br.uel.backend.models.UploadImage;
import br.uel.backend.models.User;
import br.uel.backend.repositories.ProductRepository;
import br.uel.backend.repositories.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/manage/products")
public class ProductManagerController {

    @Autowired
    ProductRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    RestTemplate restTemplate;

    private String BASE_DIR=System.getProperty("user.dir");

    @PostMapping
    public ResponseEntity<?> create(@RequestHeader("Authorization") String token,@RequestPart("codProd")String codProd, @RequestPart("description") String description,
                                    @RequestParam("amount") int amount, @RequestParam("price") float price, @RequestPart("image")MultipartFile image) throws IOException {

        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user.getPermission()==0)
            return ResponseEntity.status(401).build();

        /*String IMAGE_NAME=codProd+"-"+image.getOriginalFilename();

        try {
            image.transferTo(new File(this.BASE_DIR+"/src/main/resources/images/products/"+IMAGE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.imgur.com/3/upload");
        LinkedMultiValueMap<String,Object>params = new LinkedMultiValueMap<>();
        params.add("image",new ByteArrayResource(image.getBytes()));
        HttpHeaders headers=new HttpHeaders();
        headers.add("Authorization","Client-ID df52e300ee70c91");
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String,Object>> requestEntity = new HttpEntity<>(params,headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().encode().toUri(),
                                                HttpMethod.POST,requestEntity,String.class);

        String imgUrl;
        String body =responseEntity.getBody();
        JsonParser parser = new JsonParser();
        assert body != null;
        JsonObject o = parser.parse(body).getAsJsonObject();
        imgUrl=o.getAsJsonObject("data").get("link").getAsString();

        Product newProduct = new Product();
        newProduct.setCodProd(codProd);
        newProduct.setDescription(description);
        newProduct.setAmount(amount);
        newProduct.setPrice(price);
        newProduct.setImage(imgUrl);
        newProduct.setActive(true);
        return ResponseEntity.ok(repository.save(newProduct));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> update(@RequestHeader("Authorization") String token,@PathVariable String id,@RequestPart("codProd")String codProd, @RequestPart("description") String description,
                                    @RequestParam("amount") int amount, @RequestParam("price") float price, @RequestPart(name = "image",required = false)MultipartFile image){
        if(token==null || !token.startsWith("Bearer"))
            return ResponseEntity.status(401).build();

        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user.getPermission()==0)
            return ResponseEntity.status(401).build();

        return repository.findByCodProd(id).map(p -> {
            p.setDescription(description);
            p.setPrice(price);
            return ResponseEntity.ok(repository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token,@PathVariable String id){

        if(token==null || !token.startsWith("Bearer"))
            return ResponseEntity.status(401).build();

        String jwt = token.substring(7);
        String email = jwtTokenUtil.getUsernameFromToken(jwt);

        User user = userRepository.findByEmail(email);

        if(user.getPermission()==0)
            return ResponseEntity.status(401).build();

        return repository.findByCodProd(id)
                .map(product ->{
                  product.setActive(false);
                  repository.save(product);
                  return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
