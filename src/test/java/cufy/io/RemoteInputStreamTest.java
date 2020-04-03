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
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

@SuppressWarnings("JavaDoc")
public class RemoteInputStreamTest {
	@SuppressWarnings("StringConcatenationInLoop")
	@Test
	public void stop() throws IOException {
		String str = "ABCD" + "EFGH" + "IJKL" + "MNOP" + "QRST" + "UVWX" + "YZ01";
		Instructor instructor = new Instructor();
		RemoteInputStream stream = new RemoteInputStream(instructor, new InputStream() {
			Reader reader = new StringReader(str);

			@Override
			public int read() throws IOException {
				return reader.read();
			}
		});

		String s = "";
		boolean w = false;

		while (true) {
			char i;
			try {
				i = (char) stream.read();
				if (w)
					Assert.fail("Not broken: even when it have been notified to stop");
			} catch (IOException e) {
				if (!w)
					Assert.fail("broken before reaching the letter 'E'");
				break;
			}

			if (i == 'E') {
				w = true;
				instructor.notify(Loop.BREAK);
			}

			s += i;
		}

		Assert.assertEquals("Source don't match", "ABCDE", s);
	}
}
