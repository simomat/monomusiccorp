package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedLinkBuilder;
import de.infonautika.monomusiccorp.app.controller.utils.SelfLinkSupplier;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.repository.ProductLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.infonautika.monomusiccorp.app.controller.utils.Results.notFound;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController implements SelfLinkSupplier {

    @Autowired
    private ProductLookup productLookup;

    @Autowired
    private AuthorizedLinkBuilder authorizedLinkBuilder;

    @GetMapping
    public Resources<ProductResource> products() {

        List<ProductResource> productResources = new ProductResourceAssembler(getClass()).toResources(productLookup.findAll());
        productResources.forEach(this::addProductLinks);

        Resources<ProductResource> resources = new Resources<>(productResources);
        addSelfLink(resources);

        return resources;
    }


    private void addProductSelfLink(ProductResource productResource) {
        productResource.add(
                linkTo(methodOn(getClass()).getProduct(productResource.getProductId())).withSelfRel());
    }

    @RequestMapping("/{id}")
    @GetMapping
    public HttpEntity<ProductResource> getProduct(@PathVariable(value="id") String productId) {
        return productLookup.findOne(productId)
                .map(this::toResource)
                .map(ResponseEntity::ok)
                .orElseGet(notFound());
    }

    private ProductResource toResource(Product product) {
        ProductResource productResource = new ProductResourceAssembler(getClass()).toResource(product);
        addProductLinks(productResource);
        return productResource;
    }

    private void addProductLinks(ProductResource productResource) {
        addProductSelfLink(productResource);
        addStockAddItemLink(productResource);
    }

    private void addStockAddItemLink(ProductResource productResource) {
        authorizedLinkBuilder.withRightsOn(
                methodOn(StockController.class).addItemsToStock(productResource.getProductId(), null),
                linkBuilder -> productResource.add(linkBuilder.withRel("stock")));
    }

}
