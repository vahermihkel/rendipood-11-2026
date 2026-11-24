package ee.magnus.veebipood.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private ProductType type;

    @ManyToOne(cascade = CascadeType.ALL)
    private Rental rental;
    private Integer rentedDays;
    //private String image;
    //@Column(length = 1000)
    //private String description;
    //private Integer stock;

//    public String getName() {
//        return name;
//    }
//
//    public BigDecimal getPrice() {
//        // tee € -> $
//        return price;
//    }

}
