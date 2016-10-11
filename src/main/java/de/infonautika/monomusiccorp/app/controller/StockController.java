package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.Quantity;
import de.infonautika.monomusiccorp.app.controller.utils.SelfLinkSupplier;
import de.infonautika.monomusiccorp.app.domain.StockItem;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.infonautika.monomusiccorp.app.controller.utils.Results.noContent;
import static de.infonautika.monomusiccorp.app.controller.utils.Results.notFound;
import static de.infonautika.monomusiccorp.app.security.UserRole.ADMIN;
import static de.infonautika.monomusiccorp.app.security.UserRole.STOCK_MANAGER;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/stock")
public class StockController implements SelfLinkSupplier {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private StockItemRepository stockItemRepository;

    @RequestMapping(value = "/item/{id}", method = RequestMethod.POST)
    @Secured({STOCK_MANAGER, ADMIN})
    public ResponseEntity<Void> addItemsToStock(@PathVariable("id") String productId, @RequestBody StockItemResource hasQuantity) {
        businessProcess.addItemToStock(Quantity.of(productId, hasQuantity.getQuantity()));
        return noContent();
    }

    @RequestMapping(value = "/item/{id}", method = RequestMethod.GET)
    @Secured({STOCK_MANAGER, ADMIN})
    public HttpEntity<StockItemResource> getStockItem(@PathVariable(value="id") String productId) {
        return stockItemRepository.findByProductId(productId)
                .map(this::toResource)
                .map(ResponseEntity::ok)
                .orElseGet(notFound());
    }

    private StockItemResource toResource(StockItem stockItem) {
        StockItemResource resource = new StockItemResourceAssembler(getClass()).toResource(stockItem);
        addLinkToStockItem(resource);
        return resource;
    }

    @RequestMapping
    @GetMapping
    @Secured({STOCK_MANAGER, ADMIN})
    public Resources<StockItemResource> getStockItems() {
        List<StockItemResource> stockItems = getStockItemResources();
        stockItems.forEach(stockItemResource -> {
            addLinkToStockItem(stockItemResource);
            addLinkToProduct(stockItemResource);
        });

        Resources<StockItemResource> stockItemResources = new Resources<>(stockItems);
        addSelfLink(stockItemResources);

        return stockItemResources;
    }

    private List<StockItemResource> getStockItemResources() {
        return new StockItemResourceAssembler(getClass()).toResources(stockItemRepository.findAll());
    }

    private void addLinkToStockItem(StockItemResource stockItemResource) {
        stockItemResource.add(linkTo(methodOn(getClass()).getStockItem(stockItemResource.getProductId())).withSelfRel());
    }

    private void addLinkToProduct(StockItemResource stockItemResource) {
        stockItemResource.add(
                linkTo(methodOn(CatalogController.class).getProduct(stockItemResource.getProductId()))
                        .withRel("product"));
    }


}
