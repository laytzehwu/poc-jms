package com.arvato.pi.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PIDocumentTest {
	@Value("${activemq.retry-limit}")
	private int retry_limit;

	@Test
	public void testPIDocumentNoRetryByDefault() {
		PIDocument doc1 = new PIDocument();
		Assert.assertEquals("Document retry must be 0 by default", 0, doc1.getRetry());
	}
	
	@Test
	public void testPIDocumentNotEqual() {
		PIDocument doc1 = new PIDocument();
		PIDocument doc2 = new PIDocument();
		doc2.setReceivedTime(doc2.getReceivedTime().plusSeconds(1));
		Assert.assertFalse("Expect documents not equal with different receiving time", doc1.equals(doc2));
		doc2.setReceivedTime(doc1.getReceivedTime());
		doc2.setPayload("A new payload");
		Assert.assertFalse("Expect documents not equal with different payload", doc1.equals(doc2));
	}
	
	@Test
	public void testPIDocumentEqual() {
		PIDocument doc1 = new PIDocument();
		Assert.assertTrue("Expect empty documents must be equal to itself", doc1.equals(doc1));
		PIDocument doc2 = new PIDocument();
		doc2.setReceivedTime(doc1.getReceivedTime());
		Assert.assertTrue("Expect empty documents must be equal when being created at same time", doc1.equals(doc2));
		Assert.assertTrue("Expect empty documents must be equal when being created at same time", doc2.equals(doc1));
		
		PIDocument doc3 = new PIDocument("A");
		PIDocument doc4 = new PIDocument("A");
		Assert.assertTrue("Expect empty documents must be equal with same payload", doc3.equals(doc4));
		Assert.assertTrue("Expect empty documents must be equal with same payload", doc4.equals(doc3));
	}
	
	@Test
	public void testLogRetry() {
		PIDocument doc = new PIDocument();
		Assert.assertEquals("Default retry counter is not 0", 0, doc.getRetry());
		for(int i=0;i <retry_limit;i++) {
			doc.logRetry();
			Assert.assertEquals(
				"Retry counter does not increase after log retry",
				i + 1,
				doc.getRetry()
			);
			List<LocalDateTime> retryTimes = doc.getRetryTimes();
			Assert.assertEquals(
				"Retry time is not logged",
				i + 1,
				retryTimes.size()
			);
		}
	}
}
