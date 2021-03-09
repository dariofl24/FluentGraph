package com.tk.fluentGraph.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClusterViewController
{
    @GetMapping("/clusterview")
    public String greeting(@RequestParam(name = "clusterId") final String clusterId, final Model model)
    {
        model.addAttribute("clusterId", clusterId);

        return "clusterview";
    }
}
