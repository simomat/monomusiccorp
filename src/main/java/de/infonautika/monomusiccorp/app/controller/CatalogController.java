package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @Autowired
    private BusinessProcess businessProcess;

    @RequestMapping("/products")
    public Collection<Product> products() {
        return businessProcess.getAllProducts();
    }

}
