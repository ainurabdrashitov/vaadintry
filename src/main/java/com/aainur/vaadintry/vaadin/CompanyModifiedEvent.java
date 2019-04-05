package com.aainur.vaadintry.vaadin;

import com.aainur.vaadintry.model.Company;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
class CompanyModifiedEvent implements Serializable {

    private final Company company;
}
