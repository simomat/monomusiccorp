package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.controller.ResultStatus;
import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.repository.CustomerRepository;
import de.infonautika.monomusiccorp.app.repository.ProductRepository;
import de.infonautika.monomusiccorp.app.repository.ShoppingBasketRepository;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import de.infonautika.monomusiccorp.app.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.infonautika.monomusiccorp.app.controller.ResultStatus.isOk;
import static de.infonautika.streamjoin.Join.join;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class BusinessProcessImpl implements BusinessProcess {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private CustomerRepository cusomerRepository;

    @Autowired
    private ShoppingBasketRepository shoppingBasketRepository;

    @Autowired
    private SecurityService securityService;

    @Override
    public Collection<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public void addItemToStock(Quantity<ItemId> quantity) {
        StockItem stockItem = stockItemRepository.findByProductId(quantity.getItem().getId());
        stockItem.setQuantity(stockItem.getQuantity()+quantity.getQuantity());
        stockItemRepository.save(stockItem);
    }

    @Override
    public Collection<StockItem> getStocks() {
        return stockItemRepository.findAll();
    }

    @Override
    public void putToBasket(String customerId, Quantity<ItemId> quantity) {
        getShoppingBasketOfCustomer(customerId)
                .ifPresent(shoppingBasket -> {
                    shoppingBasket.put(quantity.getItem(), quantity.getQuantity());
                    shoppingBasketRepository.save(shoppingBasket);
                });
    }

    @Override
    public List<Quantity<Product>> getBasketContent(String customerId) {
        return getShoppingBasketOfCustomer(customerId)
                .map(ShoppingBasket::getPositions)
                .map(positions ->
                        join(positions.stream())
                        .withKey(Position::getItemId)
                        .on(productRepo.findByIdIn(positions.stream()
                                .map(p -> p.getItemId().getId())
                                .collect(toList())).stream())
                        .withKey(Product::getItemId)
                        .combine((pos, prod) -> Quantity.create(prod, pos.getQuantity()))
                        .collect(toList()))
                .orElse(emptyList());
    }

    private Optional<ShoppingBasket> getShoppingBasketOfCustomer(String customerId) {
        return getCustomer(customerId)
                .map(Customer::getShoppingBasket);
    }

    private Optional<Customer> getCustomer(String customerId) {
        return Optional.of(cusomerRepository.findById(customerId));
    }

    @Override
    public void removeFromBasket(String customerId, Quantity<ItemId> quantity) {
        getShoppingBasketOfCustomer(customerId)
                .ifPresent(shoppingBasket -> {
                    shoppingBasket.remove(quantity.getItem(), quantity.getQuantity());
                    shoppingBasketRepository.save(shoppingBasket);
                });
    }

    @Override
    public ResultStatus addCustomer(CustomerInfo customer) {
        ResultStatus resultStatus = securityService.addUser(customer);
        if (!isOk(resultStatus)) {
            return resultStatus;
        }

        createCustomer(customer);
        return ResultStatus.OK;
    }

    @Override
    public void submitOrder(String customerId) {

    }

    private void createCustomer(CustomerInfo customerInfo) {
        Customer customer = new Customer();
        customer.setUsername(customerInfo.getUsername());
        cusomerRepository.save(customer);
    }

}
