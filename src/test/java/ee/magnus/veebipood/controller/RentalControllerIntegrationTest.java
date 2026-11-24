package ee.magnus.veebipood.controller;

import ee.magnus.veebipood.entity.Product;
import ee.magnus.veebipood.entity.ProductType;
import ee.magnus.veebipood.entity.Rental;
import ee.magnus.veebipood.repository.ProductRepository;
import ee.magnus.veebipood.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
// no @Transactional here: RentalService must commit/roll back its own transaction like it does in production,
// so the database is cleaned in setUp() instead
class RentalControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RentalRepository rentalRepository;

    Product newProduct;
    Product regularProduct;
    Product oldProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        rentalRepository.deleteAll();
        newProduct = productRepository.save(new Product(null, "Matrix 11", ProductType.NEW, null, null));
        regularProduct = productRepository.save(new Product(null, "Spider Man", ProductType.REGULAR, null, null));
        oldProduct = productRepository.save(new Product(null, "Out of Africa", ProductType.OLD, null, null));
    }

    @Test
    void givenNewProductFor3Days_whenRentalIsStarted_thenFeeIs12AndProductIsRented() throws Exception {
        mockMvc.perform(post("/start-rental")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"id": %d, "days": 3}]
                                """.formatted(newProduct.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("12.0"));

        Product dbProduct = productRepository.findById(newProduct.getId()).orElseThrow();
        assertNotNull(dbProduct.getRental());
        assertEquals(3, dbProduct.getRentedDays());
        assertEquals(12.0, dbProduct.getRental().getInitialFee());
        assertEquals(1, rentalRepository.count());
    }

    @Test
    void givenThreeProducts_whenRentalIsStarted_thenFeeIsSumOfAllProducts() throws Exception {
        // NEW 1 day = 4, REGULAR 5 days = 3 + 2*3 = 9, OLD 7 days = 3 + 2*3 = 9
        mockMvc.perform(post("/start-rental")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"id": %d, "days": 1}, {"id": %d, "days": 5}, {"id": %d, "days": 7}]
                                """.formatted(newProduct.getId(), regularProduct.getId(), oldProduct.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("22.0"));

        assertEquals(1, rentalRepository.count());
    }

    @Test
    void givenRentalProductWith0Days_whenRentalIsStarted_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(post("/start-rental")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"id": %d, "days": 0}]
                                """.formatted(newProduct.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot rent when days are smaller than 1"));

        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenAlreadyRentedProduct_whenRentalIsStarted_thenBadRequestIsReturned() throws Exception {
        String body = """
                [{"id": %d, "days": 2}]
                """.formatted(newProduct.getId());

        mockMvc.perform(post("/start-rental").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        mockMvc.perform(post("/start-rental").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot rent already rented product"));

        assertEquals(1, rentalRepository.count());
    }

    @Test
    void givenNonExistingProduct_whenRentalIsStarted_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(post("/start-rental")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"id": 999999, "days": 2}]
                                """))
                .andExpect(status().isBadRequest());

        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenRentalInDb_whenGetRentals_thenListWithRentalIsReturned() throws Exception {
        rentalRepository.save(new Rental(null, new Date(), 15.0, null));

        mockMvc.perform(get("/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].initialFee").value(15.0));
    }
}
