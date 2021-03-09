package com.tk.fluentGraph.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController
{
    @GetMapping("/home")
    public String getHome()
    {
        return "home";
    }

    @GetMapping("/error")
    public String getErrorPage()
    {
        return "error";
    }
}
