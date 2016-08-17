package de.infonautika.monomusiccorp.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class BusinessController {

    @Autowired
    private BusinessProcess businessProcess;

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/createdb")
    public String createDB() {
        businessProcess.createDatabase();
        return "Done";
    }

    @RequestMapping("/products")
    public Collection<Product> products() {
        return businessProcess.getAllProducts();
    }

}
