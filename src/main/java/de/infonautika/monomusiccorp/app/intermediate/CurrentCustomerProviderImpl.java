package de.infonautika.monomusiccorp.app.intermediate;

import de.infonautika.monomusiccorp.app.domain.Customer;
import de.infonautika.monomusiccorp.app.repository.CustomerLookup;
import de.infonautika.monomusiccorp.app.security.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentCustomerProviderImpl implements CurrentCustomerProvider {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private CustomerLookup customerLookup;

    @Override
    public Optional<String> getCustomerId() {
        return authenticationFacade.getCurrentUserName()
                .flatMap((userName) -> customerLookup.getCustomerByName(userName)
                        .map(Customer::getId));
    }

}
