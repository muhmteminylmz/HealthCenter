package com.example.health_center.service.loader;

import com.example.health_center.entity.concretes.Disease;
import com.example.health_center.repository.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class DiseaseLoaderService implements ApplicationRunner {

    private final DiseaseRepository diseaseRepository;

    public void loadDiseaseFromCsv() {
        try (
                Reader reader = new InputStreamReader(
                        getClass().getResourceAsStream("/diseases.csv"), StandardCharsets.UTF_8);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)  // Header yok!
        ) {
            Set<String> existCodes = new HashSet<>(diseaseRepository.findAllCodes());

            List<Disease> diseases = new ArrayList<>();

            if (!existCodes.contains("00")) {
                diseases.add(
                        Disease.builder()
                                .code("00")
                                .name("None")
                                .build()
                );
                existCodes.add("00");
            }


            for (CSVRecord record : csvParser) {
                String code = record.get(0);      // 0. sütun = kod
                String name = record.get(1);     // 1. sütun = hastalik_ismi

                if (!diseaseRepository.existsByCode(code)) {
                    diseases.add(
                            Disease.builder()
                            .code(code)
                            .name(name)
                            .build()
                    );
                    existCodes.add(code);
                }
            }

            diseaseRepository.saveAll(diseases);
            System.out.println(diseases.size() + " hastalık yüklendi.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        loadDiseaseFromCsv();
    }
}
