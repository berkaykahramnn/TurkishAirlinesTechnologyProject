package com.tk.wallet.controller;

import com.tk.wallet.model.AppUser;
import com.tk.wallet.model.Wallet;
import com.tk.wallet.service.ExchangeRateService;
import com.tk.wallet.service.UserService;
import com.tk.wallet.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ExchangeController {
    private final UserService userService;
    private final ExchangeRateService exchangeRateService;
    private final WalletService walletService;

    public ExchangeController(UserService userService, ExchangeRateService exchangeRateService,
            WalletService walletService) {
        this.userService = userService;
        this.exchangeRateService = exchangeRateService;
        this.walletService = walletService;
    }

    @GetMapping("/exchangeConfirmation")
    public String showExchangeConfirmation(@RequestParam Long userId, @RequestParam String currency, Model model) {
        AppUser user = userService.getUserById(userId);
        Wallet wallet = walletService.getWalletByUserId(userId);

        double commissionRate = 0.2;

        double exchangeRate = exchangeRateService.getExchangeRate(wallet.getCurrency(), currency);
        double newBalance = wallet.getBalance() * exchangeRate * (1 - commissionRate); // Komisyon oranı uygulanmış
                                                                                        // yeni bakiye

        model.addAttribute("user", user);
        model.addAttribute("wallet", wallet);
        model.addAttribute("currency", currency);
        model.addAttribute("exchangeRate", exchangeRate);
        model.addAttribute("newBalance", newBalance);
        model.addAttribute("commissionRate", commissionRate * 100); // Komisyon oranını yüzde olarak ekliyoruz

        return "exchangeConfirmation";
    }

    @PostMapping("/exchangeConfirm")
    public String confirmExchange(@RequestParam Long userId, @RequestParam String currency,
            @RequestParam double newBalance, Model model) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        wallet.setCurrency(currency);
        wallet.setBalance(newBalance);
        walletService.updateWallet(wallet);

        model.addAttribute("wallet", wallet);

        return "exchangeSuccess";
    }
}
