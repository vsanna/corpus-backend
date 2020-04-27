package dev.ishikawa.corpus.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StaticController {
    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
       return ResponseEntity.ok(new HashMap(){{
           put("home", "OK");
       }});
    }
}
