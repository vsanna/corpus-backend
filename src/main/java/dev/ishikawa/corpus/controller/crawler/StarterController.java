package dev.ishikawa.corpus.controller.crawler;

import dev.ishikawa.corpus.service.port.CrawlerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController("/crawler")
public class StarterController {
    final private CrawlerService crawlerService;

    @PostMapping(
        path = "/",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> start() {
        log.info("started crawling manually");
        crawlerService.fetchUrls();
        return ResponseEntity.ok("OK");
    }
}
