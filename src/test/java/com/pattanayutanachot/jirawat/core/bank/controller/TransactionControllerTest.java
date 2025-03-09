package com.pattanayutanachot.jirawat.core.bank.controller;

import com.pattanayutanachot.jirawat.core.bank.dto.BankStatementRequest;
import com.pattanayutanachot.jirawat.core.bank.dto.BankStatementResponse;
import com.pattanayutanachot.jirawat.core.bank.dto.TransactionResponse;
import com.pattanayutanachot.jirawat.core.bank.model.User;
import com.pattanayutanachot.jirawat.core.bank.repository.UserRepository;
import com.pattanayutanachot.jirawat.core.bank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void getAllTransactions_ShouldReturnTransactions_WhenTellerAuthorized() throws Exception {
        List<TransactionResponse> transactions = List.of(
                new TransactionResponse(1L, "1234567", "DEPOSIT", BigDecimal.valueOf(100.00), null)
        );

        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/transaction/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(transactions.size()))
                .andExpect(jsonPath("$[0].accountNumber").value("1234567"))
                .andExpect(jsonPath("$[0].transactionType").value("DEPOSIT"));
    }

    @Test
    void getBankStatement_ShouldReturnStatement_WhenCustomerAuthorized() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("customer@example.com");

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(user));

        List<BankStatementResponse> bankStatement = List.of(
                BankStatementResponse.builder()
                        .accountNumber("1234567")
                        .date("2025-03-09")
                        .time("14:30:00")
                        .code("TRF")
                        .channel("MOBILE")
                        .debit(BigDecimal.valueOf(500.00))
                        .credit(null)
                        .balance(BigDecimal.valueOf(1500.00))
                        .remark("Transfer to x7890 John Doe")
                        .build(),

                BankStatementResponse.builder()
                        .accountNumber("1234567")
                        .date("2025-03-08")
                        .time("10:15:00")
                        .code("DEP")
                        .channel("ATM")
                        .debit(null)
                        .credit(BigDecimal.valueOf(2000.00))
                        .balance(BigDecimal.valueOf(2000.00))
                        .remark("Deposit Terminal 1234")
                        .build()
        );

        when(transactionService.getBankStatement(eq(1L), any(BankStatementRequest.class))).thenReturn(bankStatement);

        mockMvc.perform(post("/api/transaction/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"year\": 2025, \"month\": 3, \"accountNumber\": \"1234567\", \"pin\": \"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(bankStatement.size()))
                .andExpect(jsonPath("$[0].accountNumber").value("1234567"))
                .andExpect(jsonPath("$[0].date").value("2025-03-09"))
                .andExpect(jsonPath("$[0].time").value("14:30:00"))
                .andExpect(jsonPath("$[0].code").value("TRF"))
                .andExpect(jsonPath("$[0].channel").value("MOBILE"))
                .andExpect(jsonPath("$[0].debit").value(500.00))
                .andExpect(jsonPath("$[0].credit").doesNotExist())
                .andExpect(jsonPath("$[0].balance").value(1500.00))
                .andExpect(jsonPath("$[0].remark").value("Transfer to x7890 John Doe"));
    }
}