package com.pattanayutanachot.jirawat.core.bank.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;


//    @Test
//    void testPublicApiAuthIsAccessible() throws Exception {
//        mockMvc.perform(get("/api/auth/test"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testOtherApiRequiresAuthentication() throws Exception {
//        mockMvc.perform(get("/api/private/test"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void testAuthenticatedApiAccess() throws Exception {
//        mockMvc.perform(get("/api/private/test")
//                        .with(SecurityMockMvcRequestPostProcessors.user("user").roles("USER")))
//                .andExpect(status().isOk());
//    }
}