package de.infonautika.monomusiccorp.app.business;


import de.infonautika.monomusiccorp.app.domain.Product;
import de.infonautika.monomusiccorp.app.domain.StockItem;
import de.infonautika.monomusiccorp.app.repository.CustomerRepository;
import de.infonautika.monomusiccorp.app.repository.ProductRepository;
import de.infonautika.monomusiccorp.app.repository.StockItemRepository;
import de.infonautika.monomusiccorp.app.security.DefaultUsers;
import de.infonautika.monomusiccorp.app.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;

@Service
public class ApplicationState {

    final Logger logger = LoggerFactory.getLogger(ApplicationState.class);

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private StockItemRepository stockItemRepository;

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CustomerRepository cusomerRepository;

    public void dropState() {
        logger.info("dropping application state");
        stockItemRepository.deleteAll();
        productRepo.deleteAll();
        cusomerRepository.deleteAll();
        securityService.deleteUsers();
        securityService.addUser(DefaultUsers.ADMIN);
    }

    public void createState() {
        logger.info("creating application state");
        Product[] products = {
                Product.create("AC/DC", "Back in Black"),
                Product.create("The Byrds", "Fifth Dimension "),
                Product.create("AC/DC", "Let There Be Rock "),
                Product.create("Jefferson Airplane", "Surrealistic Pillow"),
                Product.create("The Easybeats", "Good Friday/Friday On My Mind")
        };

        StockItem[] stocks = {
                StockItem.of(products[0], 20L),
                StockItem.of(products[1], 15L),
                StockItem.of(products[2], 3L)
        };

        productRepo.save(asList(products));
        stockItemRepository.save(asList(stocks));


        businessProcess.addCustomer(new CustomerInfo("hans", "hans", "Hechtstr. 21, 01097 Dresden"));

    }
}
