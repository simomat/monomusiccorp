package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.resources.ProductResource;
import de.infonautika.monomusiccorp.app.controller.resources.ProductResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.LinkSupport;
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

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.*;
import static de.infonautika.monomusiccorp.app.controller.utils.Results.notFound;
import static de.infonautika.monomusiccorp.app.controller.utils.links.InvocationProxy.methodOn;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController implements SelfLinkSupplier {

    @Autowired
    private ProductLookup productLookup;

    @Autowired
    private AuthorizedInvocationFilter authorizedInvocationFilter;

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
                createSelfLink(invocationOf(methodOn(getClass()).getProduct(productResource.getProductId()))));
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
        authorizedInvocationFilter.withRightsOn(
                LinkSupport.invocationOf(methodOn(StockController.class).addItemsToStock(productResource.getProductId(), null)),
                addLink(productResource, "stock"));
    }
}
