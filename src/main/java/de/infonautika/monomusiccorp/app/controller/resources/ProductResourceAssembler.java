package de.infonautika.monomusiccorp.app.controller.resources;

import de.infonautika.monomusiccorp.app.domain.Product;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class ProductResourceAssembler extends ResourceAssemblerSupport<Product, ProductResource> {


    public ProductResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, ProductResource.class);
    }

    @Override
    public ProductResource toResource(Product product) {
        ProductResource productResource = new ProductResource();
        productResource.setProductId(product.getId());
        productResource.setTitle(product.getTitle());
        productResource.setArtist(product.getArtist());
        productResource.setPrice(product.getPrice());
        return productResource;
    }
}
