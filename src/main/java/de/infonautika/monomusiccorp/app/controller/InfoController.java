package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.resources.MessageResource;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedLinkBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.addLink;
import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.addTemplateLink;
import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.pathTemplateVariable;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class InfoController {

    @Autowired
    AuthorizedLinkBuilder authorizedLinkBuilder;

    @RequestMapping(method = RequestMethod.GET)
    public MessageResource getApi() {

        MessageResource messageResource = new MessageResource();
        messageResource.setMessage("Welcome to MonoMusicCorp API");


        addCatalogControllerLinks(messageResource);
        addCustomerControllerLinks(messageResource);
        addStockControllerLinks(messageResource);


        return messageResource;
    }

    private void addStockControllerLinks(MessageResource messageResource) {
        authorizedLinkBuilder.withRightsOn(
                methodOn(StockController.class).getStockItems(),
                addLink(messageResource, "stock")
        );

        authorizedLinkBuilder.withRightsOn(
                methodOn(StockController.class).getStockItem("id"),
                addTemplateLink(messageResource, "stockitem", pathTemplateVariable("id"))
        );
    }

    private void addCustomerControllerLinks(MessageResource messageResource) {
        authorizedLinkBuilder.withRightsOn(
                methodOn(CustomerController.class).getCustomers(),
                addLink(messageResource, "customers")
        );

        authorizedLinkBuilder.withRightsOn(
                methodOn(CustomerController.class).register(null),
                addLink(messageResource, "register")
        );
    }

    private void addCatalogControllerLinks(MessageResource messageResource) {
        authorizedLinkBuilder.withRightsOn(
                methodOn(CatalogController.class).products(),
                addLink(messageResource, "products")
        );
    }

}
