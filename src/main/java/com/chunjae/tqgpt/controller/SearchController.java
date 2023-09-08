package com.chunjae.tqgpt.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/search")
public class SearchController {
    @GetMapping("")
    public String showSearchPage() {
        return "views/search/search";
    }
}
