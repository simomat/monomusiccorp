package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.controller.CustomerInfo;
import de.infonautika.monomusiccorp.app.controller.ResultStatus;
import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.repository.CustomerRepository;
import de.infonautika.monomusiccorp.app.repository.ProductRepository;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import de.infonautika.monomusiccorp.app.security.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static de.infonautika.monomusiccorp.app.controller.ResultStatus.isOk;
import static de.infonautika.streamjoin.Join.join;
import static java.util.Arrays.asList;
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
    private SecurityService securityService;

    ShoppingBasket shoppingBasket = new ShoppingBasket();

    @Override
    public void createDatabase() {

        Product[] products = {
                Product.create("AC/DC", "Back in Black"),
                Product.create("The Byrds", "Fifth Dimension "),
                Product.create("AC/DC", "Let There Be Rock "),
                Product.create("Jefferson Airplane", "Surrealistic Pillow"),
                Product.create("The Easybeats", "Good Friday/Friday On My Mind")
        };

        StockItem[] stocks = {
                StockItem.create(products[0], 20L),
                StockItem.create(products[1], 15L),
                StockItem.create(products[2], 3L)
        };

        productRepo.save(asList(products));
        stockItemRepository.save(asList(stocks));

        addCustomer(new CustomerInfo("hans", "hans"));
    }

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
        shoppingBasket.put(quantity.getItem(), quantity.getQuantity());
    }

    @Override
    public List<Quantity<Product>> getBasketContent(String customerId) {
        List<Position> positions = shoppingBasket.getPositions();
        List<String> ids = positions.stream()
                .map(p -> p.getItemId().getId())
                .collect(toList());
        List<Product> products = productRepo.findByIdIn(ids);

        return join(positions.stream())
                .withKey(Position::getItemId)
                .on(products.stream())
                .withKey(Product::getItemId)
                .combine((pos, prod) -> Quantity.create(prod, pos.getQuantity()))
                .collect(toList());
    }

    @Override
    public void removeFromBasket(String customerId, Quantity<ItemId> quantity) {
        shoppingBasket.remove(quantity.getItem(), quantity.getQuantity());
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
