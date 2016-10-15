package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.resources.MessageResource;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.addLink;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;

@RestController
@RequestMapping("/api")
public class InfoController {

    @Autowired
    private AuthorizedInvocationFilter authorizedInvocationFilter;

    @RequestMapping(method = RequestMethod.GET)
    public MessageResource getApi() {

        MessageResource messageResource = new MessageResource();
        messageResource.setMessage("Welcome to MonoMusicCorp API");


        addCatalogControllerLinks(messageResource);
        addStockControllerLinks(messageResource);
        addCustomerControllerLinks(messageResource);


        return messageResource;
    }

    private void addStockControllerLinks(MessageResource messageResource) {
        authorizedInvocationFilter.withRightsOn(
                methodOn(StockController.class).getStockItems(),
                addLink(messageResource, "stock")
        );

        authorizedInvocationFilter.withRightsOn(
                methodOn(StockController.class).getStockItem(null),
                addLink(messageResource, "stockitem")
        );

        authorizedInvocationFilter.withRightsOn(
                methodOn(StockController.class).addItemsToStock(null, null),
                addLink(messageResource, "addstockitem")
        );
    }

    private void addCustomerControllerLinks(MessageResource messageResource) {
        authorizedInvocationFilter.withRightsOn(
                methodOn(CustomerController.class).getCustomers(),
                addLink(messageResource, "customers")
        );

        authorizedInvocationFilter.withRightsOn(
                methodOn(CustomerController.class).register(null),
                addLink(messageResource, "register")
        );

        authorizedInvocationFilter.withRightsOn(
                methodOn(CustomerController.class).getCustomer(null),
                addLink(messageResource, "customer")
        );
    }

    private void addCatalogControllerLinks(MessageResource messageResource) {
        authorizedInvocationFilter.withRightsOn(
                methodOn(CatalogController.class).products(),
                addLink(messageResource, "products")
        );

        authorizedInvocationFilter.withRightsOn(
                methodOn(CatalogController.class).getProduct(null),
                addLink(messageResource, "product")
        );
    }

}
