package ee.magnus.veebipood.model;

import ee.magnus.veebipood.entity.ProductType;
import lombok.Data;

@Data
public class ProductAddDto {
    private String name;
    private ProductType type;
}
