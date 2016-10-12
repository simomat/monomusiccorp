package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.resources.MessageResource;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedLinkBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

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
                linkBuilder -> messageResource.add(linkBuilder.withRel("stock"))
        );

//        authorizedLinkBuilder.withRightsOn(
//                methodOn(StockController.class).getStockItem("id"),
//                invocationAware -> {
//                    TemplateVariables variables = new TemplateVariables(pathTemplateVariable("id", "helloo"));
//                    Link link = new Link(new UriTemplate("/api/stock/{id}", variables), "stockitem");
//                    messageResource.add(link);
//                }
//
//        );
    }

    private TemplateVariable pathTemplateVariable(String name, String description) {
        return new TemplateVariable(name, TemplateVariable.VariableType.PATH_VARIABLE, description);
    }

    private void addCustomerControllerLinks(MessageResource messageResource) {
        authorizedLinkBuilder.withRightsOn(
                methodOn(CustomerController.class).getCustomers(),
                linkBuilder -> messageResource.add(linkBuilder.withRel("customers"))
        );

        authorizedLinkBuilder.withRightsOn(
                methodOn(CustomerController.class).register(null),
                linkBuilder -> messageResource.add(linkBuilder.withRel("register"))
        );
    }

    private void addCatalogControllerLinks(MessageResource messageResource) {
        authorizedLinkBuilder.withRightsOn(
                methodOn(CatalogController.class).products(),
                linkBuilder -> messageResource.add(linkBuilder.withRel("products"))
        );
    }

}
