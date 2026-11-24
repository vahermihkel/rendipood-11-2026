package ee.magnus.veebipood.controller;

import ee.magnus.veebipood.entity.Product;
import ee.magnus.veebipood.entity.ProductType;
import ee.magnus.veebipood.mapper.ProductMapper;
import ee.magnus.veebipood.model.ProductAddDto;
import ee.magnus.veebipood.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    //localhost:4444/products
    @GetMapping("products")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }


    //Add a film
    @PostMapping("products")
    public ResponseEntity<Product> saveProduct(@RequestBody ProductAddDto productAddDto){
        if (productAddDto.getName() == null || productAddDto.getName().length() <= 2) {
            throw new RuntimeException("Product must have at least 3 letters in name");
        }
        Product product = productMapper.getProductFromProductAddDto(productAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
    }

    //Remove a film
    @DeleteMapping("products/{id}")
    public ResponseEntity<List<Product>> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok(productRepository.findAll());
    }


    @PutMapping("products/{id}/type")
    public ResponseEntity<Product> editProduct(@RequestBody Product product) {
        if (product.getType() == null) {
            throw new RuntimeException("Product must have ID when editing");
        }
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(product));
    }

    //change movies type
    @PatchMapping("products/{id}/type/{productType}")
    public ResponseEntity<Product> changeType(@PathVariable Long id,@PathVariable ProductType productType){
        Product product = productRepository.findById(id).orElseThrow();
        product.setType(productType);
        productRepository.save(product);
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(product));
    }

}
