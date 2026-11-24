package ee.magnus.veebipood;

import ee.magnus.veebipood.entity.ProductType;
import ee.magnus.veebipood.entity.Rental;
import ee.magnus.veebipood.model.RentalProduct;
import ee.magnus.veebipood.service.RentalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class VeebipoodApplicationTests {

	@Autowired
	RentalService rentalService;

	@Test
	void givenProductIsNewAndRentedFor3Days_whenFeeIsCalculated_thenFeeIs12() {
		Double fee = rentalService.determineProductFee(ProductType.NEW, 3);
		assertEquals(12,fee);
	}

	@Test
	void givenProductIsOldAndRentedFor7Days_whenFeeIsCalculated_thenFeeIs9() {
		Double fee = rentalService.determineProductFee(ProductType.OLD, 7);
		assertEquals(9,fee);
	}

	@Test
	void givenNoRentalProducts_whenRentalIsStarted_thenExceptionIsThrown() {
		List<RentalProduct> rentalProducts = new ArrayList<>();
		rentalProducts.add(new RentalProduct()); // {id: null, days: 0}
		Rental rental = new Rental();
		assertThrows(RuntimeException.class, () -> rentalService.calculateInitialFee(rentalProducts, rental));
		//assertEquals(9,fee);
	}

}
