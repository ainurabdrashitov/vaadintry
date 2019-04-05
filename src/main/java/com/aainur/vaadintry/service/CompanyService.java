package com.aainur.vaadintry.service;

import com.aainur.vaadintry.model.Company;
import com.aainur.vaadintry.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public void delete(Company company) {
        companyRepository.delete(company);
    }

    public List<Company> findByNameLikeIgnoreCase(String name, PageRequest pageRequest) {
        return companyRepository.findByNameLikeIgnoreCase(name, pageRequest);
    }

    public int countByNameLikeIgnoreCase(String name) {
        return companyRepository.countByNameLikeIgnoreCase(name);
    }

    public Company save(Company company) {
        return companyRepository.save(company);
    }
}
