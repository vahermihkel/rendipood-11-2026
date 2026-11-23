package ee.magnus.veebipood.service;

import ee.magnus.veebipood.entity.Product;
import ee.magnus.veebipood.entity.ProductType;
import ee.magnus.veebipood.entity.Rental;
import ee.magnus.veebipood.model.RentalProduct;
import ee.magnus.veebipood.repository.ProductRepository;
import ee.magnus.veebipood.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ProductRepository productRepository;
    private final Double PREMIUM_PRICE = 4.0;
    private final Double BASIC_PRICE = 3.0;

    public Rental saveRental(List<RentalProduct> rentalProducts) {
        Rental rental = new Rental();
        //rental.setId();
        rental.setCreated(new Date());
        //rental.setLateFee();

        Double initialFee = calculateInitialFee(rentalProducts, rental);
        rental.setInitialFee(initialFee);
        return rentalRepository.save(rental);
    }

    private Double calculateInitialFee(List<RentalProduct> rentalProducts, Rental rental) {
        Double initialFee = 0.0;
        for (RentalProduct rentalProduct: rentalProducts) {
            if (rentalProduct.getDays() < 1) {
                throw new RuntimeException("Cannot rent when days are smaller than 1");
            }
            Product dbProduct = productRepository.findById(rentalProduct.getId()).orElseThrow();
            if (dbProduct.getRental() != null) {
                throw new RuntimeException("Cannot rent already rented product");
            }
            dbProduct.setRental(rental);
            dbProduct.setRentedDays(rentalProduct.getDays());
            productRepository.save(dbProduct);
            initialFee += determineProductFee(dbProduct.getProductType(), rentalProduct.getDays());
        }
        return initialFee;
    }

    private Double determineProductFee(ProductType type, int days) {
        switch (type) {
            case NEW -> {
                return days * PREMIUM_PRICE;
            }
            case REGULAR -> {
                if (days <= 3) {
                    return BASIC_PRICE;
                }
                return BASIC_PRICE + (days-3) * BASIC_PRICE;
            }
            case OLD -> {
                if (days <= 5) {
                    return BASIC_PRICE;
                }
                return BASIC_PRICE + (days-5) * BASIC_PRICE;
            }
            case null, default -> {
                return 0.0;
            }
        }
    }
}
