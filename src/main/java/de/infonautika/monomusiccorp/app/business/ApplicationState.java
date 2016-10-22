package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.domain.Money;
import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.domain.StockItem;
import de.infonautika.monomusiccorp.app.repository.CustomerRepository;
import de.infonautika.monomusiccorp.app.repository.ProductRepository;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import de.infonautika.monomusiccorp.app.security.DefaultUsers;
import de.infonautika.monomusiccorp.app.security.ModifiableUserDetailsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static de.infonautika.monomusiccorp.app.domain.Currencies.EUR;
import static java.util.Arrays.asList;

@Service
public class ApplicationState {

    private final Logger logger = LoggerFactory.getLogger(ApplicationState.class);

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private CustomerRepository cusomerRepository;

    @Autowired
    private ModifiableUserDetailsManager userManager;

    public void dropState() {
        logger.info("dropping application state");
        stockItemRepository.deleteAll();
        productRepo.deleteAll();
        cusomerRepository.deleteAll();
        userManager.deleteUsers();
    }

    public void createState() {
        logger.info("creating application state");
        Product[] products = {
                Product.create("AC/DC", "Back in Black", Money.of(12.99, EUR)),
                Product.create("The Byrds", "Fifth Dimension ", Money.of(9.99, EUR)),
                Product.create("AC/DC", "Let There Be Rock ", Money.of(15.99, EUR)),
                Product.create("Jefferson Airplane", "Surrealistic Pillow", Money.of(6.95, EUR)),
                Product.create("The Easybeats", "Good Friday/Friday On My Mind", Money.of(7.98, EUR))
        };

        StockItem[] stocks = {
                StockItem.of(products[0], 20L),
                StockItem.of(products[1], 15L),
                StockItem.of(products[2], 3L)
        };

        productRepo.save(asList(products));
        stockItemRepository.save(asList(stocks));

        userManager.createUser(DefaultUsers.ADMIN);
        businessProcess.addCustomer(new CustomerInfo("hans", "hans", "Hechtstr. 21, 01097 Dresden"));
    }
}
