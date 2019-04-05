package com.aainur.vaadintry.repository;

import com.aainur.vaadintry.model.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByNameLikeIgnoreCase(String name, Pageable request);

    int countByNameLikeIgnoreCase(String name);
}
