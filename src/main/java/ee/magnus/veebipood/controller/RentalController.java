package ee.magnus.veebipood.controller;

import ee.magnus.veebipood.entity.Rental;
import ee.magnus.veebipood.model.RentalProduct;
import ee.magnus.veebipood.repository.RentalRepository;
import ee.magnus.veebipood.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RentalController {

    private final RentalRepository rentalRepository;
    private final RentalService rentalService;

    @GetMapping("rentals")
    public List<Rental> getRentals() {
        return rentalRepository.findAll();
    }

    @PostMapping("start-rental")
    public double startRental(@RequestBody List<RentalProduct> rentalProducts) {
        Rental rental = rentalService.saveRental(rentalProducts);
        return rental.getInitialFee();
    }
}
