package com.arvato.pi.jms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import com.arvato.pi.data.PIDocument;

public class PIDocTransistor {
	public static String docToString(PIDocument doc) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(doc);
		oos.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}

	public static PIDocument decodePIDocument(String message) throws IOException, ClassNotFoundException {
		byte [] data = Base64.getDecoder().decode(message);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return PIDocument.class.cast(ois.readObject());
	}
}
