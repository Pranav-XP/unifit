package dev.forge.unifit.view;

import dev.forge.unifit.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ViewController {
    private final EmailService email;

    @GetMapping()
    public String homePage(){
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }



}
