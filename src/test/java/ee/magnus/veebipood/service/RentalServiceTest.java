package ee.magnus.veebipood.service;

import ee.magnus.veebipood.entity.Product;
import ee.magnus.veebipood.entity.ProductType;
import ee.magnus.veebipood.entity.Rental;
import ee.magnus.veebipood.model.RentalProduct;
import ee.magnus.veebipood.repository.ProductRepository;
import ee.magnus.veebipood.repository.RentalRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    RentalRepository rentalRepository;

    @InjectMocks
    RentalService rentalService;

    Product product = new Product();

    List<RentalProduct> rentalProducts = new ArrayList<>();
    RentalProduct rentalProduct = new RentalProduct();

    Rental rental = new Rental();


    @BeforeEach
    void setUp() {
        product.setId(1L);
        product.setName("Bla");
        product.setType(ProductType.NEW);

        rentalProduct.setId(1L);
        rentalProducts.add(rentalProduct);
    }

    @Test
    void givenRentalProductWith0Days_whenRentalIsStarted_thenExceptionIsThrown() {

        String error = assertThrows(RuntimeException.class, () -> rentalService.calculateInitialFee(rentalProducts, rental)).getMessage();
        assertEquals("Cannot rent when days are smaller than 1",error);
    }

    @Test
    void test() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        rentalProduct.setDays(6);

        Double initialFee = rentalService.calculateInitialFee(rentalProducts, rental);
        assertEquals(24, initialFee);
    }

    @Test
    void test2() {

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        rentalProduct.setDays(6);

        Rental dbRental = rentalService.saveRental(rentalProducts);
        assertEquals(24, dbRental.getInitialFee());
    }
}