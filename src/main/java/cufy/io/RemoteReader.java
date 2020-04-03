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

import cufy.concurrent.Do;
import cufy.concurrent.Instructor;
import cufy.util.function.ThrowingRunnable;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Objects;

/**
 * A way to control a reader using an {@link Instructor}.
 *
 * @author LSaferSE
 * @version 2 release (14-Feb-2020)
 * @since 13-Feb-2020
 */
public class RemoteReader extends Reader {
	/**
	 * The instructor to allow this reader to run.
	 */
	final protected Instructor instructor;
	/**
	 * The original reader. (The actual reader)
	 */
	final protected Reader reader;

	/**
	 * Construct a new controllable reader.
	 *
	 * @param instructor to control the this reader with
	 * @param reader     the original reader (to get data from)
	 * @throws NullPointerException if any of the given parameters is null
	 */
	public RemoteReader(Instructor instructor, Reader reader) {
		Objects.requireNonNull(instructor, "instructor");
		Objects.requireNonNull(reader, "reader");
		this.instructor = instructor;
		this.reader = reader;
	}

	@Override
	public int read(CharBuffer target) throws IOException {
		int[] atomic = new int[1];
		this.exec(() -> atomic[0] = this.reader.read(target));
		return atomic[0];
	}

	@Override
	public int read() throws IOException {
		int[] atomic = new int[1];
		this.exec(() -> atomic[0] = this.reader.read());
		return atomic[0];
	}

	@Override
	public int read(char[] cbuf) throws IOException {
		int[] atomic = new int[1];
		this.exec(() -> atomic[0] = this.reader.read(cbuf));
		return atomic[0];
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int[] atomic = new int[1];
		this.exec(() -> atomic[0] = this.reader.read(cbuf, off, len));
		return atomic[0];
	}

	@Override
	public long skip(long n) throws IOException {
		long[] atomic = new long[1];
		this.exec(() -> atomic[0] = this.reader.skip(n));
		return atomic[0];
	}

	@Override
	public boolean ready() throws IOException {
		boolean[] atomic = new boolean[1];
		this.exec(() -> atomic[0] = this.reader.ready());
		return atomic[0];
	}

	@Override
	public boolean markSupported() {
		return this.reader.markSupported();
	}

	@Override
	public void mark(int readAheadLimit) throws IOException {
		this.exec(() -> this.reader.mark(readAheadLimit));
	}

	@Override
	public void reset() throws IOException {
		this.exec(this.reader::reset);
	}

	@Override
	public void close() throws IOException {
		this.exec(this.reader::close);
	}

	/**
	 * Execute the given code with respect to the {@link #instructor}.
	 *
	 * @param runnable to be executed
	 * @throws IOException          if the instructor has specified that this reader shall be closed. Or if any other I/O exception occurred.
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
			throw new IOException("RemoteReader stopped");
	}
}
