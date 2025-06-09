package com.example.health_center.service.loader;

import com.example.health_center.entity.concretes.Allergy;
import com.example.health_center.entity.concretes.Disease;
import com.example.health_center.repository.AllergyRepository;
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

@Component
@RequiredArgsConstructor
public class AllergyLoaderService implements ApplicationRunner {

    private final AllergyRepository allergyRepository;

    public void loadAllergiesFromCsv() {
        try (
                Reader reader = new InputStreamReader(
                        getClass().getResourceAsStream("/allergies.csv"), StandardCharsets.UTF_8);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)  // Header yok!
        ) {

            Set<String> existingCodes = new HashSet<>(allergyRepository.findAllCodes());

            List<Allergy> allergies = new ArrayList<>();

            for (CSVRecord record : csvParser) {
                String code = record.get(0);
                String name = record.get(1);

                if (!allergyRepository.existsByCode(code)) {
                    allergies.add(
                            Allergy.builder()
                            .code(code)
                            .name(name)
                            .build()
                    );
                    existingCodes.add(code);
                }
            }

            allergyRepository.saveAll(allergies);
            System.out.println(allergies.size() + " alerji yüklendi.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        loadAllergiesFromCsv();
    }
}
