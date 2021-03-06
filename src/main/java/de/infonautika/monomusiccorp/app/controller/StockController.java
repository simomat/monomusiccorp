package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.controller.resources.StockItemResource;
import de.infonautika.monomusiccorp.app.controller.resources.StockItemResourceAssembler;
import de.infonautika.monomusiccorp.app.controller.utils.links.Relation;
import de.infonautika.monomusiccorp.app.domain.StockItem;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.createSelfLink;
import static de.infonautika.monomusiccorp.app.controller.utils.Results.noContent;
import static de.infonautika.monomusiccorp.app.controller.utils.Results.notFound;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.linkOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;
import static de.infonautika.monomusiccorp.app.security.UserRole.ADMIN;
import static de.infonautika.monomusiccorp.app.security.UserRole.STOCK_MANAGER;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private StockItemRepository stockItemRepository;

    @RequestMapping(value = "/item/{id}", method = RequestMethod.POST)
    @Secured({STOCK_MANAGER, ADMIN})
    @Relation("addstock")
    public ResponseEntity addItemsToStock(
            @PathVariable("id") String productId,
            @RequestParam Long quantity) {
        businessProcess.addItemToStock(productId, quantity);
        return noContent();
    }

    @RequestMapping(value = "/item/{id}", method = RequestMethod.GET)
    @Secured({STOCK_MANAGER, ADMIN})
    @Relation("stockitem")
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

    @RequestMapping(method = RequestMethod.GET)
    @GetMapping
    @Secured({STOCK_MANAGER, ADMIN})
    @Relation("stockitems")
    public Resources<StockItemResource> getStockItems() {
        List<StockItemResource> stockItems = getStockItemResources();
        stockItems.forEach(stockItemResource -> {
            addLinkToStockItem(stockItemResource);
            addLinkToProduct(stockItemResource);
        });

        Resources<StockItemResource> stockItemResources = new Resources<>(stockItems);
        stockItemResources.add(createSelfLink(getClass()));
        stockItemResources.add(linkOn(methodOn(getClass()).addItemsToStock(null, null)).withGivenRel());

        return stockItemResources;
    }

    private List<StockItemResource> getStockItemResources() {
        return new StockItemResourceAssembler(getClass()).toResources(stockItemRepository.findAll());
    }

    private void addLinkToStockItem(StockItemResource stockItemResource) {
        stockItemResource.add(
                linkOn(methodOn(getClass()).getStockItem(stockItemResource.getProductId()))
                        .withRelSelf());
    }

    private void addLinkToProduct(StockItemResource stockItemResource) {
        stockItemResource.add(
                linkOn(methodOn(CatalogController.class).getProduct(stockItemResource.getProductId())).withGivenRel());
    }
}
