package com.tk.wallet.controller;

import com.tk.wallet.model.AppUser;
import com.tk.wallet.model.Wallet;
import com.tk.wallet.service.UserService;
import com.tk.wallet.service.ExchangeRateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class UserController {
    private final UserService userService;
    private final ExchangeRateService exchangeRateService;

    public UserController(UserService userService, ExchangeRateService exchangeRateService) {
        this.userService = userService;
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/")
    public String redirectToRegister() {
        return "redirect:/register";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute AppUser user, Model model) {
        // Check if the username is already taken
        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("error", "Username is already taken. Please choose a different one.");
            return "register";
        }

        // Check if the email is already registered
        if (userService.isEmailRegistered(user.getEmail())) {
            model.addAttribute("error", "This email is already registered. Please use a different one.");
            return "register";
        }

        userService.createUser(user.getUsername(), user.getEmail(), user.getCurrency(), user.getBalance());
        return "redirect:/success";
    }

    @GetMapping("/success")
    public String showSuccessPage(Model model) {
        AppUser user = userService.getLastCreatedUser();
        Wallet wallet = userService.getWalletByUserId(user.getId());
        model.addAttribute("id", user.getId());
        model.addAttribute("wallet", wallet);
        return "success";
    }

    @GetMapping("/user/{id}")
    public String showUserPage(@PathVariable("id") Long userId, Model model) {
        AppUser user = userService.getUserById(userId);
        Wallet wallet = userService.getWalletByUserId(userId);
        Map<String, Double> exchangeRates = exchangeRateService.getExchangeRates(wallet.getCurrency());
        model.addAttribute("user", user);
        model.addAttribute("wallet", wallet);
        model.addAttribute("exchangeRates", exchangeRates);
        return "user";
    }
}
