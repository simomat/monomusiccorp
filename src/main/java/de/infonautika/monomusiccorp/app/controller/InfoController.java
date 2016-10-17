package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.controller.resources.MessageResource;
import de.infonautika.monomusiccorp.app.controller.utils.AuthorizedInvocationFilter;
import de.infonautika.monomusiccorp.app.controller.utils.links.Relation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static de.infonautika.monomusiccorp.app.controller.utils.LinkSupport.addLink;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.linkOn;
import static de.infonautika.monomusiccorp.app.controller.utils.links.LinkFacade.methodOn;

@RestController
@RequestMapping("/api")
public class InfoController {

    @Autowired
    private AuthorizedInvocationFilter authorizedInvocationFilter;

    @RequestMapping(method = RequestMethod.GET)
    @Relation("currentuser")
    public MessageResource getApi() {

        MessageResource messageResource = new MessageResource();
        messageResource.setMessage("Welcome to MonoMusicCorp API");

        addCatalogControllerLinks(messageResource);
        addStockControllerLinks(messageResource);
        addShoppingControllerLinks(messageResource);
        messageResource.add(linkOn(methodOn(CustomerController.class).getCurrent()).withGivenRel());
        messageResource.add(linkOn(methodOn(UserController.class).currentUser()).withGivenRel());
        addOrdersLinks(messageResource);

        return messageResource;
    }

    private void addOrdersLinks(MessageResource messageResource) {
        authorizedInvocationFilter.withRightsOn(
                methodOn(OrdersController.class).getOrders(),
                addLink(messageResource)
        );

        authorizedInvocationFilter.withRightsOn(
                methodOn(OrdersController.class).getOrders(null),
                addLink(messageResource)
        );
    }

    private void addShoppingControllerLinks(MessageResource messageResource) {
        authorizedInvocationFilter.withRightsOn(
                methodOn(ShoppingController.class).getBasket(),
                addLink(messageResource)
        );
    }

    private void addStockControllerLinks(MessageResource messageResource) {
        authorizedInvocationFilter.withRightsOn(
                methodOn(StockController.class).getStockItems(),
                addLink(messageResource)
        );
    }

    private void addCatalogControllerLinks(MessageResource messageResource) {
        authorizedInvocationFilter.withRightsOn(
                methodOn(CatalogController.class).products(),
                addLink(messageResource)
        );
    }

}
