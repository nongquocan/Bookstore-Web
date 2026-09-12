package com.bookstore.config;

import com.bookstore.entity.HomeSection;
import com.bookstore.repository.HomeSectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class HomeLayoutDataInitializer implements CommandLineRunner {

    @Autowired
    private HomeSectionRepository homeSectionRepository;

    @Override
    public void run(String... args) throws Exception {
        if (homeSectionRepository.count() == 0) {
            log.info("Initializing default home layout sections...");
            
            List<HomeSection> defaultSections = Arrays.asList(
                new HomeSection(null, "Sách Bán Chạy Nhất", "FEATURED", 8, 1, true),
                new HomeSection(null, "Flash Sale", "FLASH_SALE", 10, 2, true),
                new HomeSection(null, "Tất Cả Sản Phẩm", "ALL_BOOKS", 12, 3, true)
            );
            
            homeSectionRepository.saveAll(defaultSections);
            log.info("Default home layout initialized successfully.");
        }
    }
}
