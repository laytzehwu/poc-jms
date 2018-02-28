package com.arvato.pi.web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arvato.pi.data.JSONResponse;
import com.arvato.pi.data.PIDocument;
//import com.arvato.pi.jms.consumer.Submitter;
import com.arvato.pi.jms.producer.Sender;

@Controller
@RequestMapping("/api")
public class APIHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(APIHandler.class);
	
	@Value("${activemq.queue.pi-doc}")
	private String queueName;

	@Autowired
	private Sender sender;
	
	// Comment below 2 lines when stop submitting
	//@Autowired
	//private Submitter submitter;
	
	@RequestMapping("/")
	public String showAPIWelcom() {
		return "APIWelcom";
	}
	
	private String retrievePayload(HttpServletRequest request) throws IOException {
		InputStream inputStream = request.getInputStream();
		if (inputStream != null) {
			Charset charset = Charset.forName("UTF-8");
			String strBuf = IOUtils.toString(inputStream, charset);
			if (strBuf != null && strBuf.length() > 0) {
				return strBuf;
			}
		}
		throw new IOException("Empty payload");
	}
	
	private PIDocument getSubmittedDocument(HttpServletRequest request) throws IOException {
		return new PIDocument(retrievePayload(request));
	}
	
	
	@RequestMapping(value="/", method=RequestMethod.POST)
	public @ResponseBody JSONResponse doPost(HttpServletRequest request, HttpServletResponse resp) {
		JSONResponse respJSON = new JSONResponse();
		
		// It is done by Spring Boot
		//resp.setContentType("application/json");
		
		try {
			PIDocument doc = getSubmittedDocument(request);
			sender.sendPIDoc(queueName, doc);
			resp.setStatus(HttpServletResponse.SC_ACCEPTED);
			respJSON.setStatus(HttpServletResponse.SC_ACCEPTED);
			respJSON.setMessage("Accepted");
		} catch (IOException ex) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			respJSON.setStatus(resp.getStatus());
			respJSON.setMessage(ex.getMessage());
			LOGGER.error(ex.getMessage());
		} catch (Exception ex) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			respJSON.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			respJSON.setMessage(ex.getMessage());
			LOGGER.error(ex.getMessage());
		}
		
		return respJSON;
	}

}
