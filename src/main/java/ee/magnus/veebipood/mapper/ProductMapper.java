package ee.magnus.veebipood.mapper;

import ee.magnus.veebipood.entity.Product;
import ee.magnus.veebipood.model.ProductAddDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product getProductFromProductAddDto(ProductAddDto productAddDto);
}

