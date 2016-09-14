package de.infonautika.monomusiccorp.app.controller;

import de.infonautika.monomusiccorp.app.business.ApplicationState;
import de.infonautika.monomusiccorp.app.business.BusinessProcess;
import de.infonautika.monomusiccorp.app.business.CustomerInfo;
import de.infonautika.monomusiccorp.app.business.ResultStatus;
import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static de.infonautika.monomusiccorp.app.business.ResultStatus.isOk;

@RestController
@RequestMapping("/app")
public class ApplicationController {

    @Autowired
    private BusinessProcess businessProcess;

    @Autowired
    private ApplicationState applicationState;

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @RequestMapping("/createdb")
    public ResultStatus createDB() {
        applicationState.dropState();
        applicationState.createState();
        return ResultStatus.OK;
    }

    @RequestMapping("/addCustomer")
    @PostMapping
    public ResultStatus addCustomer(@RequestBody CustomerInfo customer, HttpServletResponse response){
        ResultStatus resultStatus = businessProcess.addCustomer(customer);
        if (!isOk(resultStatus)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        return resultStatus;
    }

}
