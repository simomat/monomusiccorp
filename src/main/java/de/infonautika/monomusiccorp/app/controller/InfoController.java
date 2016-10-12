package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.resources.MessageResource;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.Invocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.*;

@RestController
@RequestMapping("/api")
public class InfoController {

    @Autowired
    AuthorizedInvocationFilter authorizedInvocationFilter;

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
                Invocation.invocationOf(Invocation.methodOn(StockController.class).getStockItems()),
                addLink(messageResource, "stock")
        );

        authorizedInvocationFilter.withRightsOn(
                Invocation.invocationOf(Invocation.methodOn(StockController.class).getStockItem("id")),
                addTemplateLink(messageResource, "stockitem", pathTemplateVariable("id"))
        );
    }

    private void addCustomerControllerLinks(MessageResource messageResource) {
        authorizedInvocationFilter.withRightsOn(
                Invocation.invocationOf(Invocation.methodOn(CustomerController.class).getCustomers()),
                addLink(messageResource, "customers")
        );

        authorizedInvocationFilter.withRightsOn(
                Invocation.invocationOf(Invocation.methodOn(CustomerController.class).register(null)),
                addLink(messageResource, "register")
        );

        authorizedInvocationFilter.withRightsOn(
                Invocation.invocationOf(Invocation.methodOn(CustomerController.class).getCustomer("userName")),
                addTemplateLink(messageResource, "customer", pathTemplateVariable("userName"))
        );
    }

    private void addCatalogControllerLinks(MessageResource messageResource) {
        authorizedInvocationFilter.withRightsOn(
                Invocation.invocationOf(Invocation.methodOn(CatalogController.class).products()),
                addLink(messageResource, "products")
        );

        authorizedInvocationFilter.withRightsOn(
                Invocation.invocationOf(Invocation.methodOn(CatalogController.class).getProduct("id")),
                addTemplateLink(messageResource, "product", pathTemplateVariable("id"))
        );
    }

}
