package de.infonautika.monomusiccorp.app.controller.resources;

import de.infonautika.monomusiccorp.app.domain.StockItem;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

public class StockItemResourceAssembler extends ResourceAssemblerSupport<StockItem, StockItemResource> {
    public StockItemResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, StockItemResource.class);
    }

    @Override
    public StockItemResource toResource(StockItem stockItem) {
        StockItemResource stockItemResource = new StockItemResource();
        stockItemResource.setProductId(stockItem.getProduct().getId());
        stockItemResource.setQuantity(stockItem.getQuantity());
        return stockItemResource;
    }
}
