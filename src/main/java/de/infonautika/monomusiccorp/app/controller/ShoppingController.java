package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.Quantity;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import de.infonautika.monomusiccorp.app.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
@RequestMapping("/shopping")
public class ShoppingController {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private CustomerProvider customerProvider;

    @RequestMapping("/basket/put")
    @PutMapping
    public ResultStatus putToBasket(@RequestBody Quantity<ItemId> quantity) {
        return withCustomerId((id) ->
                businessProcess.putToBasket(
                    id,
                    Quantity.create(quantity.getItem(), quantity.getQuantity())));
    }

    @RequestMapping("/basket")
    @GetMapping
    public List<Quantity<Product>> getBasket() {
        return withCustomerId(
                (id) -> businessProcess.getBasketContent(id),
                Collections::emptyList);
    }


    @RequestMapping("/basket/remove")
    @DeleteMapping
    public ResultStatus removeFromBasket(@RequestBody Quantity<ItemId> quantity) {
        return withCustomerId((id) ->
                businessProcess.removeFromBasket(id, quantity));
    }

    @RequestMapping("/sendorder")
    public ResultStatus submitOrder() {
        return withCustomerId((id) -> businessProcess.submitOrder(id));
    }


    private ResultStatus withCustomerId(Consumer<String> consumer) {
        return customerProvider.getCustomerId()
                .map((id) -> {
                    consumer.accept(id);
                    return ResultStatus.OK;})
                .orElse(ResultStatus.NO_CUSTOMER);
    }

    private <T> T withCustomerId(Function<String, T> function, Supplier<T> defaultResult) {
        return customerProvider.getCustomerId()
                .map(function)
                .orElseGet(defaultResult);
    }

}
