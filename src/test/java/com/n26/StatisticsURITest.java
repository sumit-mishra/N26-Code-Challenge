package com.n26;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.model.Transaction;
import com.n26.service.repository.TransactionRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class StatisticsURITest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    private String dummyJsonReq;

    private TransactionRepository repository;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        repository = TransactionRepository.getInstance();
        dummyJsonReq = getJsonString(new Transaction("11.11", String.valueOf(Instant.now())));
    }

    @Test
    public void contextLoads() {
        assertNotNull(wac);
    }

    @Test
    public void StatisticsShouldReturnCountAsZeroWhenRepoIsCleared() throws Exception {
        repository.clear();
        this.mockMvc.perform(get("/statistics").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print())
                        .andExpect(jsonPath("$.count", is(0)));
    }

    @Test
    public void TransactionStatisticsVerifiesRequestCount() throws Exception {
        this.mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(dummyJsonReq));
        this.mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(dummyJsonReq));

        this.mockMvc.perform(get("/statistics").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$.count", is(2)));
    }

    private String getJsonString(final Transaction transaction) {
        try {
            return new ObjectMapper().writeValueAsString(transaction);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
