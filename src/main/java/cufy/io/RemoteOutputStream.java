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

import cufy.lang.Instructor;
import org.cufy.lang.Do;
import org.cufy.util.function.ThrowingRunnable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * A way to control an input-stream using an {@link Instructor}.
 *
 * @author LSaferSE
 * @version 2 release (14-Feb-2020)
 * @since 13-Feb-2020
 */
public class RemoteOutputStream extends OutputStream {
	/**
	 * The instructor to allow this stream to run.
	 */
	final protected Instructor instructor;
	/**
	 * The original stream. (The actual stream)
	 */
	final protected OutputStream stream;

	/**
	 * Construct a new controllable output-stream.
	 *
	 * @param instructor to control the this stream with
	 * @param stream     the original stream (to get data from)
	 * @throws NullPointerException if any of the given parameters is null
	 */
	public RemoteOutputStream(Instructor instructor, OutputStream stream) {
		Objects.requireNonNull(instructor, "instructor");
		Objects.requireNonNull(stream, "stream");
		this.instructor = instructor;
		this.stream = stream;
	}

	@Override
	public void write(int b) throws IOException {
		this.exec(() -> this.stream.write(b));
	}
	@Override
	public void write(byte[] b) throws IOException {
		this.exec(() -> this.stream.write(b));
	}
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.exec(() -> this.stream.write(b, off, len));
	}

	@Override
	public void flush() throws IOException {
		this.exec(this.stream::flush);
	}

	@Override
	public void close() throws IOException {
		this.exec(this.stream::close);
	}

	/**
	 * Execute the given code with respect to the {@link #instructor}.
	 *
	 * @param runnable to be executed
	 * @throws IOException          if the instructor has specified that this stream shall be closed. Or if any other I/O exception occurred.
	 * @throws NullPointerException if the given runnable is null
	 */
	protected void exec(ThrowingRunnable<IOException> runnable) throws IOException {
		Objects.requireNonNull(runnable, "runnable");
		boolean[] executed = {false};

		this.instructor.start(new Do(d -> {
			executed[0] = true;
			runnable.run();
		}));

		if (!executed[0])
			throw new IOException("RemoteOutputStream stopped");
	}
}
