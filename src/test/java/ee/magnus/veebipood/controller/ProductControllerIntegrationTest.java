package ee.magnus.veebipood.controller;

import ee.magnus.veebipood.entity.Product;
import ee.magnus.veebipood.entity.ProductType;
import ee.magnus.veebipood.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // every test is rolled back, so tests don't affect each other
class ProductControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductRepository productRepository;

    Product product;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        product = productRepository.save(new Product(null, "Matrix", ProductType.NEW, null, null));
    }

    @Test
    void givenOneProductInDb_whenGetProducts_thenListWithOneProductIsReturned() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Matrix"))
                .andExpect(jsonPath("$[0].type").value("NEW"));
    }

    @Test
    void givenValidProduct_whenProductIsAdded_thenProductIsCreatedAndSavedToDb() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Titanic", "type": "OLD"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Titanic"))
                .andExpect(jsonPath("$.type").value("OLD"));

        assertEquals(2, productRepository.count());
    }

    @Test
    void givenProductNameShorterThan3Letters_whenProductIsAdded_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "AB", "type": "OLD"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product must have at least 3 letters in name"));

        assertEquals(1, productRepository.count());
    }

    @Test
    void givenProductWithoutName_whenProductIsAdded_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"type": "OLD"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product must have at least 3 letters in name"));
    }

    @Test
    void givenProductInDb_whenProductIsDeleted_thenEmptyListIsReturned() throws Exception {
        mockMvc.perform(delete("/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        assertFalse(productRepository.existsById(product.getId()));
    }

    @Test
    void givenProductWithType_whenProductIsEdited_thenUpdatedProductIsReturned() throws Exception {
        mockMvc.perform(put("/products/{id}/type", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": %d, "name": "Matrix Reloaded", "type": "REGULAR"}
                                """.formatted(product.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Matrix Reloaded"))
                .andExpect(jsonPath("$.type").value("REGULAR"));

        Product dbProduct = productRepository.findById(product.getId()).orElseThrow();
        assertEquals("Matrix Reloaded", dbProduct.getName());
        assertEquals(ProductType.REGULAR, dbProduct.getType());
    }

    @Test
    void givenProductWithoutType_whenProductIsEdited_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(put("/products/{id}/type", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"id": %d, "name": "Matrix Reloaded"}
                                """.formatted(product.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product must have ID when editing"));
    }

    @Test
    void givenProductInDb_whenTypeIsChanged_thenProductHasNewType() throws Exception {
        mockMvc.perform(patch("/products/{id}/type/{productType}", product.getId(), "OLD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("OLD"));

        assertEquals(ProductType.OLD, productRepository.findById(product.getId()).orElseThrow().getType());
    }

    @Test
    void givenNonExistingProduct_whenTypeIsChanged_thenBadRequestIsReturned() throws Exception {
        mockMvc.perform(patch("/products/{id}/type/{productType}", 999999, "OLD"))
                .andExpect(status().isBadRequest());
    }
}
