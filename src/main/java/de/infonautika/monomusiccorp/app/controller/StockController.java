package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.Quantity;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/stock")
public class StockController implements SelfLinkSupplier{

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private StockItemRepository stockItemRepository;

    @RequestMapping("/newstockitem")
    @PostMapping
    public void newStockItem(@RequestBody Quantity<String> supply) {
        businessProcess.addItemToStock(supply);
    }

    @RequestMapping
    @GetMapping
    public Resources<StockItemResource> getStockItems() {
        List<StockItemResource> stockItems = new StockItemResourceAssembler(getClass()).toResources(stockItemRepository.findAll());

        stockItems.forEach(stockItemResource -> {
            stockItemResource.add(
                    linkTo(methodOn(CatalogController.class).getProduct(stockItemResource.getProductId()))
                            .withRel("prod"));
        });

        Resources<StockItemResource> stockItemResources = new Resources<>(stockItems);
        addSelfLink(stockItemResources);

        return stockItemResources;
    }


}
