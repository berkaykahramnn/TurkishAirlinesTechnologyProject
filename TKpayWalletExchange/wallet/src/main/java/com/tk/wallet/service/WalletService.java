// WalletService.java
package com.tk.wallet.service;

import com.tk.wallet.exceptionHandling.WalletNotFoundException;
import com.tk.wallet.model.Wallet;
import com.tk.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId).orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
    }

    public Wallet updateWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }
}
