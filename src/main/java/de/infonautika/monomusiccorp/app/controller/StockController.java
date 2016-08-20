package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import de.infonautika.monomusiccorp.app.domain.StockItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private BusinessProcess businessProcess;

    @RequestMapping("/newstockitem")
    @PostMapping
    public void newStockItem(@RequestBody StockItemSupply supply) {
        businessProcess.addItemToStock(new ItemId(supply.getId()), supply.getQuantity());
    }

    @RequestMapping("/stock")
    public Collection<StockItem> getStockItems() {
        return businessProcess.getStocks();

    }


}
