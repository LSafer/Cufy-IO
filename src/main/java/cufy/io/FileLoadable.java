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
import cufy.lang.Loadable;

import java.io.*;
import java.util.Objects;

/**
 * A loadable that uses {@link File} as it's container.
 *
 * @author LSaferSE
 * @version 1 release (14-Feb-2020)
 * @since 14-Feb-2020
 */
public interface FileLoadable extends Loadable {
	@Override
	default InputStream getInputStream() throws IOException {
		InputStream base = new FileInputStream(this.getFile());
		InputStream buff = new BufferedInputStream(base);

		return buff;
	}
	@Override
	default InputStream getInputStream(Instructor instructor) throws IOException {
		Objects.requireNonNull(instructor, "instructor");

		InputStream base = new FileInputStream(this.getFile());
		InputStream buff = new BufferedInputStream(base);
		InputStream ctrl = new RemoteInputStream(instructor, buff);

		return ctrl;
	}

	@Override
	default OutputStream getOutputStream() throws IOException {
		OutputStream base = new FileOutputStream(this.getFile());

		return base;
	}
	@Override
	default OutputStream getOutputStream(Instructor instructor) throws IOException {
		Objects.requireNonNull(instructor, "instructor");

		OutputStream base = new FileOutputStream(this.getFile());
		OutputStream ctrl = new RemoteOutputStream(instructor, base);

		return ctrl;
	}

	@Override
	default Reader getReader() throws IOException {
		Reader base = new FileReader(this.getFile());
		Reader buff = new BufferedReader(base);

		return buff;
	}
	@Override
	default Reader getReader(Instructor instructor) throws IOException {
		Objects.requireNonNull(instructor, "instructor");

		Reader base = new FileReader(this.getFile());
		Reader buff = new BufferedReader(base);
		Reader ctrl = new RemoteReader(instructor, buff);

		return ctrl;
	}

	@Override
	default Writer getWriter() throws IOException {
		Writer base = new FileWriter(this.getFile());

		return base;
	}
	@Override
	default Writer getWriter(Instructor instructor) throws IOException {
		Objects.requireNonNull(instructor, "instructor");

		Writer base = new FileWriter(this.getFile());
		Writer ctrl = new RemoteWriter(instructor, base);

		return ctrl;
	}

	/**
	 * Get the file container of this loadable.
	 *
	 * @return the file of this loadable
	 */
	File getFile();
}
