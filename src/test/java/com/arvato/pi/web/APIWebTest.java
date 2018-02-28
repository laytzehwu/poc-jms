package com.arvato.pi.web;
import org.mockito.Mockito;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.arvato.pi.data.PIDocument;
import com.arvato.pi.jms.producer.Sender;

@RunWith(SpringRunner.class)
@WebMvcTest(APIHandler.class)
public class APIWebTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private JmsTemplate jmsTemplate;
    
    @MockBean
    private Sender sender;
    
    @Test
    public void whenCallMissingPayload_thenReturnBadRequest() throws Exception {
    	mockMvc.perform(post("/api/")).andDo(print()).andExpect(status().isBadRequest());
    	Mockito.verify(sender, Mockito.never()).sendPIDoc(Mockito.anyString(), Mockito.any(PIDocument.class));
    }
    
    @Test
    public void whenCallEmptyPayload_thenReturnBadRequest() throws Exception {
    	mockMvc.perform(post("/api/").content("")).andExpect(status().isBadRequest());
    	Mockito.verify(sender, Mockito.never()).sendPIDoc(Mockito.anyString(), Mockito.any(PIDocument.class));
    }

    @Test
    public void whenCallDocument_thenReturnAccepted() throws Exception {
    	mockMvc.perform(post("/api/").content("Just a doc")).andExpect(status().isAccepted());
    	Mockito.verify(sender).sendPIDoc(Mockito.anyString(), Mockito.any(PIDocument.class));
    }
}
