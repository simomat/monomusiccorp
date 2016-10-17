package de.infonautika.monomusiccorp.app.business;

import de.infonautika.monomusiccorp.app.domain.Customer;

public interface BusinessProcess {

    void addItemToStock(String productId, Long quantity);

    void putToBasket(Customer customer, String productId, Long quantity);

    void removeFromBasket(Customer customer, String productId, Long quantity);

    void addCustomer(CustomerInfo customer);

    void submitOrder(Customer customer);
}
