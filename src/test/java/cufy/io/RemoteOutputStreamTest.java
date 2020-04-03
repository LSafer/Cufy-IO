/*
 * Copyright (c) 2019, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *  shall mention that this file has been edited.
 *  By adding a new header (at the bottom of this header)
 *  with the word "Editor" on top of it.
 */
package cufy.io;

import cufy.concurrent.Instructor;
import cufy.concurrent.Loop;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

@SuppressWarnings("JavaDoc")
public class RemoteOutputStreamTest {
	@Test
	public void stop() {
		String str = "ABCD" + "EFGH" + "IJKL" + "MNOP" + "QRST" + "UVWX" + "YZ01";
		Instructor instructor = new Instructor();
		StringWriter origin = new StringWriter();
		OutputStream writer = new RemoteOutputStream(instructor, new OutputStream() {
			@Override
			public void write(int i) throws IOException {
				origin.write(i);
			}
		});

		boolean w = false;
		for (char c : str.toCharArray()) {
			if (c == 'E') {
				instructor.notify(Loop.BREAK);
				w = true;
			}

			try {
				writer.write(c);
				if (w)
					Assert.fail("Writer expected to be broken");
			} catch (IOException e) {
				if (!w)
					Assert.fail("Writer expected to be valid");
				break;
			}
		}

		Assert.assertEquals("Text output from the writer is not as expected", "ABCD", origin.toString());
	}
}
