package com.aainur.vaadintry.vaadin;

import com.aainur.vaadintry.model.Company;
import com.aainur.vaadintry.service.CompanyService;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import org.vaadin.spring.events.EventBus;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@UIScope
@SpringComponent
public class CompanyForm extends AbstractForm<Company> {

    private static final long serialVersionUID = 1L;

    EventBus.UIEventBus eventBus;
    CompanyService companyService;

    private TextField name = new MTextField("Name");
    private TextField email = new MTextField("Email");

    CompanyForm(CompanyService companyService, EventBus.UIEventBus b) {
        super(Company.class);
        this.companyService = companyService;
        this.eventBus = b;

        setSavedHandler(company -> {
            companyService.save(company);
            eventBus.publish(this, new CompanyModifiedEvent(company));
        });
        setResetHandler(p -> eventBus.publish(this, new CompanyModifiedEvent(p)));

        setSizeUndefined();
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        name,
                        email
                ).withWidth(""),
                getToolbar()
        ).withWidth("");
    }
}
