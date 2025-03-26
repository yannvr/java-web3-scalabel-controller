package com.example.demo;

import com.example.demo.model.Wallet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllWallets() throws Exception {
        mockMvc.perform(get("/wallets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].balance").value("10 BTC"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].balance").value("5 ETH"));
    }

    @Test
    public void testGetWallet() throws Exception {
        mockMvc.perform(get("/wallets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.balance").value("10 BTC"));
    }

    @Test
    public void testCreateWallet() throws Exception {
        Wallet wallet = new Wallet(3L, "15 BTC");
        mockMvc.perform(post("/wallets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(wallet)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.balance").value("15 BTC"));
    }

    @Test
    public void testUpdateWallet() throws Exception {
        Wallet wallet = new Wallet(1L, "25 BTC");
        mockMvc.perform(put("/wallets/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(wallet)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.balance").value("25 BTC"));
    }

    @Test
    public void testDeleteWallet() throws Exception {
        mockMvc.perform(delete("/wallets/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    public void testGetWalletNotFound() throws Exception {
        mockMvc.perform(get("/wallets/999"))
            .andExpect(status().isNotFound());
    }
}
