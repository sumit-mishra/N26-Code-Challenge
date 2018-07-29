package com.n26;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n26.model.Transaction;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class TransactionsURITest {

    @Autowired
    private WebApplicationContext appContext;
    private MockMvc mockMvc;

    private String dummyJsonReq;
    private String timeStamp, futuretimeStamp;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.appContext).build();
        timeStamp = "2018-07-29T15:35:45.119Z";
        futuretimeStamp = "2019-07-29T15:35:45.119Z";
    }

    @Test
    public void contextLoads() {
        assertNotNull(appContext);
    }

    @Test
    public void validTimestampRequestReturns201() throws Exception {
        dummyJsonReq = getJsonString(new Transaction("11.11", String.valueOf(Instant.now())));
        this.mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(dummyJsonReq)).andExpect(status().isCreated());
    }

    @Test
    public void requestOlderThan60SecReturns204() throws Exception {
        dummyJsonReq = getJsonString(new Transaction("22.22", String.valueOf(Instant.parse(timeStamp))));
        this.mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(dummyJsonReq)).andExpect(status().isNoContent());
    }

    @Test
    public void invalidJsonFormatReturns400() throws Exception {
        dummyJsonReq = String.format("{\"amount\": 0,\"time\":  %s }", timeStamp);
        this.mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(dummyJsonReq)).andExpect(status().isBadRequest());
    }

    @Test
    public void futureTimestampReturns422() throws Exception {
        dummyJsonReq = getJsonString(new Transaction("33.33", String.valueOf(Instant.parse(futuretimeStamp))));
        this.mockMvc.perform(post("/transactions").contentType(MediaType.APPLICATION_JSON).content(dummyJsonReq))
                        .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void deleteRequestReturnsNoContent() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/transactions").contentType(MediaType.ALL)).andExpect(status().isOk());
    }

    private String getJsonString(final Transaction transaction) {
        try {
            return new ObjectMapper().writeValueAsString(transaction);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
