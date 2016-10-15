package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.business.errors.DoesNotExistException;
import de.infonautika.monomusiccorp.app.domain.*;
import de.infonautika.monomusiccorp.app.repository.*;
import de.infonautika.monomusiccorp.app.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static de.infonautika.monomusiccorp.app.util.Functional.ifPresent;
import static de.infonautika.streamjoin.Join.join;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class BusinessProcessImpl implements BusinessProcess {

    final Logger logger = LoggerFactory.getLogger(BusinessProcessImpl.class);

    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PickingOrderRepository pickingOrderRepository;

    @Autowired
    private ShoppingBasketRepository shoppingBasketRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CustomerLookup customerLookup;

    @Autowired
    private ProductLookup productLookup;

    @Autowired
    private StockNotification stockNotification;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceDelivery invoiceDelivery;

    @Override
    public ResultStatus addItemToStock(Quantity<String> quantity) {
        return stockItemRepository.findByProductId(quantity.getItem())
                .map(stockItem -> {
                    updateStockItemQuantity(stockItem, quantity);
                    return ResultStatus.OK;
                })
                .orElseGet(() -> getProduct(quantity.getItem())
                        .map(product -> {
                            createStockItem(product, quantity);
                            return ResultStatus.OK;
                        }).
                        orElseGet(() -> {
                            logger.debug("no product of {} found to add stock item", quantity.getItem());
                            return ResultStatus.NOT_EXISTENT;
                        }));
    }

    private void createStockItem(Product product, Quantity<String> quantity) {
        logger.debug("new stock item {} with quantity of {}", product.getId(), quantity.getQuantity());
        StockItem stockItem = StockItem.of(product, quantity.getQuantity());
        stockItemRepository.save(stockItem);
    }

    private void updateStockItemQuantity(StockItem stockItem, Quantity<String> quantity) {
        assert Objects.equals(stockItem.getProduct().getId(), quantity.getItem());
        logger.debug("update stock item {} quantity with {}", quantity.getItem(), stockItem.getQuantity());
        stockItem.addQuantity(quantity.getQuantity());
        stockItemRepository.save(stockItem);
    }

    private Optional<Product> getProduct(String productId) {
        return productLookup.findOne(productId);
   }

    @Override
    public void putToBasket(Customer customer, String productId, Long quantity) {
        ifPresent(productLookup.findOne(productId),
                product -> updateShoppingBasket(customer, product, quantity))
        .orElseThrow(() -> {
                logger.debug("tried to put {} to basket of {}, but product does not exist", productId, customer.getUsername());
                return new DoesNotExistException("product " + productId);
            });
    }

    private void updateShoppingBasket(Customer customer, Product product, Long quantity) {
        ShoppingBasket shoppingBasket = customer.getShoppingBasket();
        shoppingBasket.put(product, quantity);
        shoppingBasketRepository.save(shoppingBasket);
        logger.debug("customer {} put {} to basket", customer.getUsername(), quantity);
    }

    @Override
    public void removeFromBasket(Customer customer, String productId, Long quantity) {
        ShoppingBasket shoppingBasket = customer.getShoppingBasket();
        shoppingBasket.remove(productId, quantity);
        shoppingBasketRepository.save(shoppingBasket);
        logger.debug("customer {} removed {} of {} from basket", customer.getUsername(), quantity, productId);
    }

    @Override
    public void addCustomer(CustomerInfo customer)  {
        securityService.addUser(customer);
        createCustomerAndSave(customer);
        logger.info("customer {} added", customer.getUsername());
    }

    @Override
    public void submitOrder(Customer customer) {
        ShoppingBasket shoppingBasket = customer.getShoppingBasket();
        if (shoppingBasket.isEmpty()) {
            logger.debug("rejected submit an order with empty basket for customer {}", customer.getUsername());
            throw new DoesNotExistException("no items in basket");
        }

        Order order = createAndSaveOrder(customer, shoppingBasket);
        clearShoppingBasket(customer);
        processNewOrder(order);
    }

    private Order createAndSaveOrder(Customer customer, ShoppingBasket shoppingBasket) {
        Order order = createOrder(customer);
        orderRepository.save(order);
        logger.debug("created order for customer {} with {}", customer.getUsername(), shoppingBasket.getPositions());
        return order;
    }

    private void processNewOrder(Order order) {
        processShipment(order);
        processBilling(order);
    }

    private void processShipment(Order order) {
        PickingOrder pickingOrder = createPickingOrder(order);
        notifyNewPickingOrder(pickingOrder);
    }

    private void clearShoppingBasket(Customer customer) {
        customer.getShoppingBasket().clear();
        customerLookup.save(customer);
    }

    private void processBilling(Order order) {
        Invoice invoice = createInvoice(order);
        invoiceDelivery.deliver(invoice);
    }

    private Invoice createInvoice(Order order) {
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setDate(LocalDate.now());
        invoiceRepository.save(invoice);
        return invoice;
    }

    private PickingOrder createPickingOrder(Order order) {
        PickingOrder pickingOrder = new PickingOrder();
        pickingOrder.setPickedItems(emptyList());
        pickingOrder.setStatus(PickingOrder.PickingStatus.OPEN);
        pickingOrder.setOrder(order);
        pickingOrderRepository.save(pickingOrder);
        logger.debug("created picking order for customer {} with {}", order.getCustomer().getUsername(), order.getPositions());
        return pickingOrder;
    }

    private void notifyNewPickingOrder(PickingOrder pickingOrder) {
        stockNotification.newPickingOrder(pickingOrder);
    }

    private Order createOrder(Customer customer) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setPositions(toPricedPositions(customer.getShoppingBasket().getPositions()));
        order.setShippingAddress(customer.getAddress());
        order.setSubmitTime(LocalDateTime.now());
        return order;
    }

    private List<PricedPosition> toPricedPositions(List<Position> positions) {
        return productLookup.withProducts(
                positions.stream().map(p -> p.getProduct().getId()),
                products ->
                    join(products)
                        .withKey(Product::getId)
                        .on(positions.stream())
                        .withKey(position -> position.getProduct().getId())
                        .combine(this::toPricedPosition)
                        .asStream()
                        .collect(toList()),
                Collections::emptyList);
    }

    private PricedPosition toPricedPosition(Product prod, Position pos) {
        PricedPosition pricedPosition = new PricedPosition();
        pricedPosition.setProduct(pos.getProduct());
        pricedPosition.setQuantity(pos.getQuantity());
        pricedPosition.setPrice(prod.getPrice());
        return pricedPosition;
    }

    private void createCustomerAndSave(CustomerInfo customerInfo) {
        Customer customer = new Customer();
        customer.setUsername(customerInfo.getUsername());
        customer.setAddress(new Address(customerInfo.getAddress()));
        customerLookup.save(customer);
    }

}
