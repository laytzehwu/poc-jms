package com.arvato.pi.jms.consumer;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.arvato.pi.data.PIDocument;
import com.arvato.pi.jms.PIDocTransistor;
import com.arvato.pi.jms.RandomString;
import com.arvato.pi.jms.producer.Sender;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SubmitterResendTest {

	@Value("${activemq.retry-limit}")
	private int retry_limit;

	@MockBean
	private Sender sender;
	
	@MockBean
	private Submitter submitter;

	@Autowired
	private SubmitHandler submitterHandler;
	
	@Test
	public void ensureSubmitterRetryLimitIsExisted() {
		Assert.assertNotNull(submitterHandler);
		Assert.assertTrue(
			"Retry limit must be greater than 0",
			submitterHandler.getRetryLimit() > 0
		);
		Assert.assertEquals(
			"Retry limit must be same as applicaiton.properties setting",
			retry_limit,
			submitterHandler.getRetryLimit()
		);
	}
	
	@Test
	public void ensurePIDocumentCanRetry() {
		PIDocument doc = new PIDocument(RandomString.makeString(1000));
		Assert.assertTrue(
				"PIDocument defaultly can retry",
				submitterHandler.canPIDocumentRetry(doc)
		);
		for(int i=doc.getRetry(); i < retry_limit; i ++) {
			doc.setRetry(i);
			Assert.assertTrue(
					"PIDocument can retry be hit the limit",
					submitterHandler.canPIDocumentRetry(doc)
			);
		}
		doc.setRetry(retry_limit);
		Assert.assertTrue(
				"PIDocument still can return when first reach retry limit ",
				submitterHandler.canPIDocumentRetry(doc)
		);
	}
	
	@Test(expected=SubmitRetryLimitException.class)
	public void ensureRetryLimitExceptionRaise() throws SubmitRetryLimitException {
		PIDocument doc = new PIDocument(RandomString.makeString(1000));
		// The first send is not considered retry.
		// The doc is actually send retry_limit + 1 time
		// It will only stop when attempt to send retry_limit + 2
		int tryTime = retry_limit + 2;
		for(int i=0; i < tryTime; i ++) {
			submitterHandler.resendPIDocument(doc);
			// Ensure doc resend is called
			try {
				Mockito.verify(sender).resendPIDoc(
					Mockito.anyString(), 
					Mockito.anyObject()
				);
			} catch(IOException ex) {
				Assert.fail(ex.getMessage());
			}
			Mockito.reset(sender);
		}
		// Ensure no resend is called when hitting retry limit
		try {
			Mockito.verify(sender, Mockito.never()).resendPIDoc(
				Mockito.anyString(), 
				Mockito.anyObject()
			);
		} catch(IOException ex) {
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void ensurePIDocumentNoRetryAfterLimit() {
		try {
			PIDocument doc = new PIDocument(RandomString.makeString(1000));
			doc.setRetry(retry_limit + 1);
			int previousRetryCounter = doc.getRetry();
			Assert.assertFalse(
					"PIDocument can not return after hit limit ",
					submitterHandler.canPIDocumentRetry(doc)
			);
			
			String message = PIDocTransistor.docToString(doc);
			Mockito.doThrow(new SubmitFailException(doc, "fail")).when(submitter).submitPIDocument(Mockito.anyObject());
			submitterHandler.receiveMessage(message);
			Mockito.verify(submitter).submitPIDocument(Mockito.anyObject());
			Assert.assertEquals("Retry counter must not change", previousRetryCounter, doc.getRetry());
			Mockito.verify(sender, Mockito.never()).sendPIDoc(Mockito.anyString(), Mockito.anyObject());
			Mockito.verify(sender, Mockito.never()).resendPIDoc(Mockito.anyString(), Mockito.anyObject());
		} catch (Exception e) {
			Assert.fail("Exception is not expected to raise during hiting retry and no retry");
		}
	}
	
	@Test
	public void ensureRetryCounterIncreaseWhenResend() throws IOException {
		PIDocument doc = new PIDocument(RandomString.makeString(1000));
		try {
			for(int i=0;i< retry_limit; i ++) {
				submitterHandler.resendPIDocument(doc);
				Mockito.verify(sender).resendPIDoc(Mockito.anyString(), Mockito.anyObject());
				Assert.assertEquals(
					"Resend counter does not increase after resend",
					i + 1,
					doc.getRetry()
				);
				Mockito.reset(sender);
			}
		} catch (SubmitRetryLimitException ex) {
			Assert.fail("Not suppose to have retry limit exception raise!");
		}
	}
	
	@Test
	public void ensureNoResendWithoutSubmitFailException() {
		try {
			PIDocument doc = new PIDocument(RandomString.makeString(1000));
			String message = PIDocTransistor.docToString(doc);
			submitterHandler.receiveMessage(message);
			Mockito.verify(submitter).submitPIDocument(Mockito.anyObject());
			Mockito.verify(sender, Mockito.never()).sendPIDoc(Mockito.anyString(), Mockito.anyObject());
			Mockito.verify(sender, Mockito.never()).resendPIDoc(Mockito.anyString(), Mockito.anyObject());
		} catch (SubmitFailException e) {
			Assert.fail("SubmitFailException raises when submitting document");
		} catch (IOException e) {
		} catch (Exception e) {
			Assert.fail("Exception raises when submitting document");
		}
	}
	
	@Test
	public void ensureResendWithSubmitFailException() {
		try {
			PIDocument doc = new PIDocument(RandomString.makeString(1000));
			Mockito.doThrow(new SubmitFailException(doc, "fail")).when(submitter).submitPIDocument(Mockito.anyObject());
			String message = PIDocTransistor.docToString(doc);
			submitterHandler.receiveMessage(message);
			Mockito.verify(submitter).submitPIDocument(Mockito.anyObject());
			Mockito.verify(sender).resendPIDoc(Mockito.anyString(), Mockito.anyObject());
		} catch (SubmitFailException e) {
			Assert.fail("SubmitFailException raises when submitting document");
		} catch (IOException e) {
		} catch (Exception e) {
			Assert.fail("Exception raises when submitting document");
		}
	}
}
