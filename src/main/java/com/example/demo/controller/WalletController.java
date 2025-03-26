package com.example.demo.controller;

import com.example.demo.model.Wallet;
import com.example.demo.repository.WalletRepository;
import com.example.demo.exception.WalletNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@CrossOrigin(origins = "*")
public class WalletController {

    private final WalletRepository walletRepo;

    public WalletController(WalletRepository walletRepo) {
        this.walletRepo = walletRepo;
    }

    @GetMapping
    public ResponseEntity<List<Wallet>> getAllWallets() {
        return ResponseEntity.ok(walletRepo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletRepo.findById(id)
            .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Wallet> createWallet(@RequestBody Wallet wallet) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(walletRepo.save(wallet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wallet> updateWallet(@PathVariable Long id, @RequestBody Wallet wallet) {
        if (!walletRepo.existsById(id)) {
            throw new WalletNotFoundException("Wallet not found with id: " + id);
        }
        wallet.setId(id);
        return ResponseEntity.ok(walletRepo.save(wallet));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteWallet(@PathVariable Long id) {
        if (!walletRepo.existsById(id)) {
            throw new WalletNotFoundException("Wallet not found with id: " + id);
        }
        walletRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
