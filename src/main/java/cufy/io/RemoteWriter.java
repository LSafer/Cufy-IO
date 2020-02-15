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
import java.io.Writer;
import java.util.Objects;

/**
 * A way to control a reader using an {@link Instructor}.
 *
 * @author LSaferSE
 * @version 2 release (14-Feb-2020)
 * @since 13-Feb-2020
 */
public class RemoteWriter extends Writer {
	/**
	 * The instructor to allow this writer to run.
	 */
	final protected Instructor instructor;
	/**
	 * The original writer. (The actual writer)
	 */
	final protected Writer writer;

	/**
	 * Construct a new controllable writer.
	 *
	 * @param instructor to control the this writer with
	 * @param writer     the original writer (to get data from)
	 * @throws NullPointerException if any of the given parameters is null
	 */
	public RemoteWriter(Instructor instructor, Writer writer) {
		Objects.requireNonNull(instructor, "instructor");
		Objects.requireNonNull(writer, "writer");
		this.instructor = instructor;
		this.writer = writer;
	}

	@Override
	public void write(int c) throws IOException {
		this.exec(() -> this.writer.write(c));
	}
	@Override
	public void write(char[] cbuf) throws IOException {
		this.exec(() -> this.writer.write(cbuf));
	}
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		this.exec(() -> this.writer.write(cbuf, off, len));
	}
	@Override
	public void write(String str) throws IOException {
		this.exec(() -> this.writer.write(str));
	}
	@Override
	public void write(String str, int off, int len) throws IOException {
		this.exec(() -> this.writer.write(str, off, len));
	}

	@Override
	public Writer append(CharSequence csq) throws IOException {
		this.exec(() -> this.writer.append(csq));
		return this;
	}
	@Override
	public Writer append(CharSequence csq, int start, int end) throws IOException {
		this.exec(() -> this.writer.append(csq, start, end));
		return this;
	}
	@Override
	public Writer append(char c) throws IOException {
		this.exec(() -> this.writer.append(c));
		return this;
	}

	@Override
	public void flush() throws IOException {
		this.exec(this.writer::flush);
	}

	@Override
	public void close() throws IOException {
		this.exec(this.writer::close);
	}

	/**
	 * Execute the given code with respect to the {@link #instructor}.
	 *
	 * @param runnable to be executed
	 * @throws IOException          if the instructor has specified that this writer shall be closed. Or if any other I/O exception occurred.
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
			throw new IOException("RemoteWriter stopped");
	}
}
