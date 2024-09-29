package br.ufg.ceia.gameinsight.gameservice.controller;

import br.ufg.ceia.gameinsight.gameservice.etls.service.IgdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/igdb")
public class IGDBController {

    @Autowired
    private IgdbService igdbService;

    /**
     * @brief Method to run the ETL
     * @details Method to run the ETL process from a specific starting date.
     * @param dateToStart The date to start the ETL process (must be in ISO-8601 format).
     * @param searchType The type of search to be performed, can be "updated_at" or "created_at".
     * @return ResponseEntity - Response entity indicating the success of the ETL process.
     */
    @PostMapping("/etl")
    public ResponseEntity<?> runETL(@RequestParam Instant dateToStart, @RequestParam String searchType) {
        try {
            // Run the ETL process with the provided start date
            igdbService.RunETL(dateToStart, searchType);
            return ResponseEntity.ok(
                    "ETL process started successfully from: " + dateToStart);
        } catch (Exception e) {
            // Log the error (if you have a logging system)
            // logger.error("Error while running the ETL process", e);
            return ResponseEntity.status(500)
                    .body("Error while running the ETL process: " + e.getMessage());
        }
    }
}