package com.tk.wallet.controller;

import com.tk.wallet.model.Wallet;
import com.tk.wallet.service.ExchangeRateService;
import com.tk.wallet.service.UserService;
import com.tk.wallet.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class WalletController {
    private final UserService userService;
    private final ExchangeRateService exchangeRateService;
    private final WalletService walletService;

    public WalletController(UserService userService, ExchangeRateService exchangeRateService, WalletService walletService) {
        this.userService = userService;
        this.exchangeRateService = exchangeRateService;
        this.walletService = walletService;
    }

    @GetMapping("/wallet/{userId}")
    public String showWalletPage(@PathVariable("userId") Long userId, Model model) {
        Wallet wallet = userService.getWalletByUserId(userId);
        if (wallet == null) {
            model.addAttribute("errorMessage", "Wallet not found for user id: " + userId);
            return "error";
        }
        double commissionRate = 0.2;
        Map<String, Double> exchangeRates = exchangeRateService.getExchangeRates(wallet.getCurrency());
        model.addAttribute("wallet", wallet);
        model.addAttribute("exchangeRates", exchangeRates);
        model.addAttribute("commissionRate", commissionRate);
        return "wallet";
    }

    @PostMapping("/deposit/{userId}")
    public String deposit(@PathVariable("userId") Long userId, @RequestParam double amount, RedirectAttributes redirectAttributes) {
        Wallet wallet = userService.getWalletByUserId(userId);
        if (wallet == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wallet not found for user id: " + userId);
            return "redirect:/wallet/" + userId;
        }

        if (amount <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deposit amount must be a positive value.");
        } else if (amount > 1000000) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deposit amount cannot exceed 1.000.000.");
        } else {
            wallet.setBalance(wallet.getBalance() + amount);
            walletService.updateWallet(wallet);
            redirectAttributes.addFlashAttribute("successMessage", "Deposit successful. New balance: " + wallet.getBalance());
        }

        return "redirect:/wallet/" + userId;
    }

    @PostMapping("/withdraw/{userId}")
    public String withdraw(@PathVariable("userId") Long userId, @RequestParam("amount") double amount, RedirectAttributes redirectAttributes) {
        Wallet wallet = userService.getWalletByUserId(userId);
        if (wallet == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wallet not found for user id: " + userId);
            return "redirect:/wallet/" + userId;
        }

        if (amount <= 0) {
            redirectAttributes.addFlashAttribute("withdrawErrorMessage", "Withdrawal amount must be a positive value.");
        } else if (amount > 1000000) {
            redirectAttributes.addFlashAttribute("withdrawErrorMessage", "Withdrawal amount cannot exceed 1.000.000.");
        } else if (amount > wallet.getBalance()) {
            redirectAttributes.addFlashAttribute("withdrawErrorMessage", "Withdrawal amount cannot exceed the current balance.");
        } else {
            wallet.setBalance(wallet.getBalance() - amount);
            walletService.updateWallet(wallet);
            redirectAttributes.addFlashAttribute("withdrawSuccessMessage", "Withdrawal successful. New balance: " + wallet.getBalance());
        }

        return "redirect:/wallet/" + userId;
    }
}
