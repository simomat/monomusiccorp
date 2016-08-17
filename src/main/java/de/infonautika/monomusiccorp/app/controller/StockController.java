package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.domain.StockItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class StockController {

    @Autowired
    private BusinessProcess businessProcess;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/createdb")
    public void createDB() {
        businessProcess.createDatabase();
    }

    @RequestMapping("/products")
    public Collection<Product> products() {
        return businessProcess.getAllProducts();
    }

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
