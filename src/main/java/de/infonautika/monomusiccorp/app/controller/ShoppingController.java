package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.Quantity;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import de.infonautika.monomusiccorp.app.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @Autowired
    private BusinessProcess businessProcess;

    @RequestMapping("/basket/put")
    @PutMapping
    public void putToBasket(@RequestBody Quantity<ItemId> quantity) {
        businessProcess.putToBasket(Quantity.create(quantity.getItem(), quantity.getQuantity()));
    }

    @RequestMapping("/basket")
    @GetMapping
    public List<Quantity<Product>> getBasket() {
        return businessProcess.getBasketContent();
    }


    @RequestMapping("/basket/remove")
    @DeleteMapping
    public void removeFromBasket(@RequestBody Quantity<ItemId> quantity) {
        businessProcess.removeFromBasket(quantity);
    }


}
