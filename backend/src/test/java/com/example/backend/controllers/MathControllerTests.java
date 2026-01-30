package com.example.backend.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MathControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void add_returnsSumOfTwoNumbers() throws Exception {
        mockMvc.perform(get("/add")
                .param("a", "2")
                .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        mockMvc.perform(get("/add")
                .param("a", "-3")
                .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
