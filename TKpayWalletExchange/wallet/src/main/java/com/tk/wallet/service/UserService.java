// UserService.java
package com.tk.wallet.service;

import com.tk.wallet.exceptionHandling.WalletNotFoundException;
import com.tk.wallet.model.AppUser;
import com.tk.wallet.model.Wallet;
import com.tk.wallet.repository.UserRepository;
import com.tk.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    public void createUser(String username, String email, String currency, double balance) {
        AppUser user = new AppUser(username, email);
        user.setCurrency(currency);
        user.setBalance(balance);
        userRepository.save(user);

        Wallet wallet = new Wallet(user, currency, balance);
        walletRepository.save(wallet);
    }

    public AppUser getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user with ID: " + userId));
    }

    public AppUser getLastCreatedUser() {
        List<AppUser> users = userRepository.findAll();
        return users.get(users.size() - 1);
    }

    public void updateWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }

    private List<AppUser> users = new ArrayList<>();

    public boolean isUsernameTaken(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public boolean isEmailRegistered(String email) {
        return users.stream().anyMatch(user -> user.getEmail().equals(email));
    }
}
