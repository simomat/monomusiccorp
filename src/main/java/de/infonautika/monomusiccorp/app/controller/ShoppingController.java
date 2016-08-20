package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @Autowired
    private BusinessProcess businessProcess;

    @RequestMapping("/toBasket")
    @PutMapping
    public void putToBasket(@RequestBody IdQuantity quantity) {
        businessProcess.putToBasket(new ItemId(quantity.getId()), quantity.getQuantity());
    }
}
