package pl.edu.pwr.s34641bank.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BankIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void fullFlow_register_deposit_transfer_getClient() throws Exception {
        // register
        var registerBody = om.writeValueAsString(Map.of(
                "firstName", "Jan",
                "lastName", "Kowalski",
                "initialBalance", 100
        ));

        var registerRes = mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.balance").value(100))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String clientId = om.readTree(registerRes).get("id").asText();

        // deposit +50
        var depositBody = om.writeValueAsString(Map.of(
                "clientId", clientId,
                "amount", 50
        ));

        mvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.balanceAfter").value(150));

        // transfer 70 => balance 80
        var transferBody = om.writeValueAsString(Map.of(
                "clientId", clientId,
                "amount", 70
        ));

        mvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.balanceAfter").value(80));

        // get client => balance 80
        mvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(80));
    }

    @Test
    void transfer_insufficientFunds_returnsDeclined() throws Exception {
        // register with 10
        var registerBody = om.writeValueAsString(Map.of(
                "firstName", "A",
                "lastName", "B",
                "initialBalance", 10
        ));

        var registerRes = mvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String clientId = om.readTree(registerRes).get("id").asText();

        // transfer 50 => DECLINED
        var transferBody = om.writeValueAsString(Map.of(
                "clientId", clientId,
                "amount", 50
        ));

        mvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DECLINED"))
                .andExpect(jsonPath("$.message").value("Insufficient funds"))
                .andExpect(jsonPath("$.balanceAfter").value(10));
    }

    @Test
    void deposit_clientNotRegistered_returnsRejected() throws Exception {
        var depositBody = om.writeValueAsString(Map.of(
                "clientId", "00000000-0000-0000-0000-000000000001",
                "amount", 10
        ));

        mvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.message").value("Client not registered"));
    }
}