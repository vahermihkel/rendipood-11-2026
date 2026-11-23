package ee.magnus.veebipood.repository;

import ee.magnus.veebipood.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ProductRepository extends JpaRepository<Product, Long> {


}
