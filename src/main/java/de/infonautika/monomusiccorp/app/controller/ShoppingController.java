package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.Quantity;
import de.infonautika.monomusiccorp.app.business.ResultStatus;
import de.infonautika.monomusiccorp.app.domain.ItemId;
import de.infonautika.monomusiccorp.app.domain.PricedPosition;
import de.infonautika.monomusiccorp.app.intermediate.CustomerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
        return withCustomerId(id ->
                businessProcess.putToBasket(
                    id,
                    Quantity.of(quantity.getItem(), quantity.getQuantity())));
    }

    @RequestMapping("/basket")
    @GetMapping
    public List<PricedPosition> getBasket() {
        return withCustomerIdOrElse(
                id -> businessProcess.getBasketContent(id),
                Collections::emptyList);
    }


    @RequestMapping("/basket/remove")
    @DeleteMapping
    public ResultStatus removeFromBasket(@RequestBody Quantity<ItemId> quantity) {
        return withCustomerId(id -> {
            businessProcess.removeFromBasket(id, quantity);
            return ResultStatus.OK;
        });
    }

    @RequestMapping("/submitorder")
    public ResultStatus submitOrder() {
        return withCustomerId(id -> businessProcess.submitOrder(id));
    }

    @RequestMapping("/orders")
    @GetMapping
    public List<OrderStatus> getOrders() {


        return withCustomerIdOrElse(
                id -> businessProcess.getPickingOrders(id).stream()
                        .map(order -> OrderStatus.from(order))
                        .collect(Collectors.toList()),
                Collections::emptyList);
    }


    private ResultStatus withCustomerId(Function<String, ResultStatus> consumer) {
        return withCustomerIdOrElse(
                consumer,
                () -> ResultStatus.NO_CUSTOMER);
    }

    private <T> T withCustomerIdOrElse(Function<String, T> function, Supplier<T> defaultResult) {
        return customerProvider.getCustomerId()
                .map(function)
                .orElseGet(defaultResult);
    }

}
