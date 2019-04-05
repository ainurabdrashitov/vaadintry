package com.aainur.vaadintry.vaadin;

import com.aainur.vaadintry.model.Company;
import com.aainur.vaadintry.service.CompanyService;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.button.ConfirmButton;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

import java.util.ArrayList;
import java.util.List;

@Title("Vaadin Try Example")
@Theme("valo")
@SpringUI
@RequiredArgsConstructor
public class VaadinUI extends UI {

    final int PAGESIZE = 45;

    final CompanyService companyService;
    final EventBus.UIEventBus eventBus;
    final CompanyForm companyForm;

    private MGrid<Company> list = new MGrid<>(Company.class)
            .withProperties("id", "name", "email")
            .withColumnHeaders("id", "Name", "Email")
            .withFullWidth();

    private MTextField filterByName = new MTextField()
            .withPlaceholder("Name filter");
    private Button addNew = new MButton(VaadinIcons.PLUS, this::add);
    private Button edit = new MButton(VaadinIcons.PENCIL, this::edit);
    private Button delete = new ConfirmButton(VaadinIcons.TRASH,
            "Are you sure you want to delete the entry?", this::remove);
    @Override
    protected void init(VaadinRequest request) {
        setContent(
                new MVerticalLayout(new MHorizontalLayout(filterByName, addNew, edit, delete), list)
                        .expand(list)
        );
        listEntities();

        list.asSingleSelect().addValueChangeListener(e -> adjustActionButtonState());
        filterByName.addValueChangeListener(e -> {
            listEntities(e.getValue());
        });

        eventBus.subscribe(this);
    }

    protected void adjustActionButtonState() {
        boolean hasSelection = !list.getSelectedItems().isEmpty();
        edit.setEnabled(hasSelection);
        delete.setEnabled(hasSelection);
    }

    private void listEntities() {
        listEntities(filterByName.getValue());
    }

    private void listEntities(String nameFilter) {
        String likeFilter = "%" + nameFilter + "%";
        list.setDataProvider(
                (sortOrder, offset, limit) -> {
                    final int pageSize = limit;
                    final int startPage = (int) Math.floor((double) offset / pageSize);
                    final int endPage = (int) Math.floor((double) (offset + pageSize - 1) / pageSize);
                    final Sort.Direction sortDirection = sortOrder.isEmpty()
                            || sortOrder.get(0).getDirection() == SortDirection.ASCENDING
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;
                    final String sortProperty = sortOrder.isEmpty() ? "id" : sortOrder.get(0).getSorted();
                    if (startPage != endPage) {
                        List<Company> page0 = companyService.findByNameLikeIgnoreCase(
                                likeFilter, PageRequest.of(startPage, pageSize, sortDirection, sortProperty));
                        page0 = page0.subList(offset % pageSize, page0.size());
                        List<Company> page1 = companyService.findByNameLikeIgnoreCase(
                                likeFilter, PageRequest.of(endPage, pageSize, sortDirection, sortProperty));
                        page1 = page1.subList(0, limit - page0.size());
                        List<Company> result = new ArrayList<>(page0);
                        result.addAll(page1);
                        return result.stream();
                    } else {
                        return companyService.findByNameLikeIgnoreCase(likeFilter, PageRequest.of(startPage, pageSize, sortDirection, sortProperty)).stream();
                    }
                },
                () -> companyService.countByNameLikeIgnoreCase(likeFilter)
        );
        adjustActionButtonState();
    }

    public void add(Button.ClickEvent clickEvent) {
        edit(new Company());
    }

    public void edit(Button.ClickEvent e) {
        edit(list.asSingleSelect().getValue());
    }

    public void remove() {
        companyService.delete(list.asSingleSelect().getValue());
        list.deselectAll();
        listEntities();
    }

    protected void edit(final Company phoneBookEntry) {
        companyForm.setEntity(phoneBookEntry);
        companyForm.openInModalPopup();
    }

    @EventBusListenerMethod(scope = EventScope.UI)
    public void onCompanyModified(CompanyModifiedEvent event) {
        listEntities();
        companyForm.closePopup();
    }
}
