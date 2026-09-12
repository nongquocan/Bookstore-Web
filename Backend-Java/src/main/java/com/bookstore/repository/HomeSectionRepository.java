package com.bookstore.repository;

import com.bookstore.entity.HomeSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeSectionRepository extends JpaRepository<HomeSection, Long> {
    List<HomeSection> findAllByActiveTrueOrderByDisplayOrderAsc();
    List<HomeSection> findAllByOrderByDisplayOrderAsc();
}
