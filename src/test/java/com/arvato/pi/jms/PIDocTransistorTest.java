package com.arvato.pi.jms;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.arvato.pi.data.PIDocument;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
public class PIDocTransistorTest {
	
	@Test
	public void testDocCanToString() {
		PIDocument doc = new PIDocument(RandomString.makeString(1000));
		String serializedString;
		try {
			serializedString = PIDocTransistor.docToString(doc);
			Assert.notNull(serializedString, "PIDocument serialized is null!");
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPIDocCanBeDecoded() {
		PIDocument sourceDoc = new PIDocument(RandomString.makeString(1000));
		try {
			String serializedString = PIDocTransistor.docToString(sourceDoc);
			PIDocument targetDoc = PIDocTransistor.decodePIDocument(serializedString);
			Assert.notNull(serializedString, "Empty decoded PIDocument!");
			if (!targetDoc.equals(sourceDoc)) {
				fail("Decoded document does not match to the source");
			}
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getMessage());
		}
		
		
	}
}
