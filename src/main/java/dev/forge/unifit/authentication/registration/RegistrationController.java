package dev.forge.unifit.authentication.registration;


import dev.forge.unifit.email.EmailService;
import dev.forge.unifit.exception.UserAlreadyExistException;
import dev.forge.unifit.user.IUserService;
import dev.forge.unifit.user.User;
import dev.forge.unifit.user.UserDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/registration")
public class RegistrationController {
    private final IUserService userService;
    private final EmailService emailService;

    @GetMapping
    public String showRegistrationForm(Model model){
        UserDTO userDTO = new UserDTO();
        model.addAttribute("user",userDTO);
        return "registration";
    }

    @PostMapping("/register")
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDTO userDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ModelAndView("registration");
        }
        try{
            User registered = userService.registerUser(userDTO);
            System.out.println("Sending email to: "+ registered.getEmail());

        } catch (UserAlreadyExistException uaeEx){
            ModelAndView mav = new ModelAndView("redirect:/registration?error","user",userDTO);
            mav.addObject("message","An account with that email already exists");
        }catch(RuntimeException ex){
            ex.printStackTrace();
            return new ModelAndView("emailError","user",userDTO);

        }
        return new ModelAndView("redirect:/registration?success","user",userDTO);
    }

}
