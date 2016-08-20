package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class ApplicationController {

    @Autowired
    private BusinessProcess businessProcess;

    @RequestMapping("/")
    public String index() {
        return "Welcome to MonoMusicCorp!";
    }

    @RequestMapping("/createdb")
    public void createDB() {
        businessProcess.createDatabase();
    }

}
