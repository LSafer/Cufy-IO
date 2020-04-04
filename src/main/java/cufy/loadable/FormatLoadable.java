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
package cufy.loadable;

import cufy.concurrent.Instructor;
import cufy.text.Format;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * An object that can be loaded from a container. And that container provides an unformatted data that requires that loadable to format it.
 *
 * @author LSaferSE
 * @version 3 release (16-Feb-2020)
 * @since 13-Feb-2020
 */
public interface FormatLoadable extends Loadable {
	@Override
	default void load() throws IOException {
		try (Reader reader = this.getReader()) {
			this.getFormat().parse(reader, this);
		}
	}
	@Override
	default void load(Instructor instructor) throws IOException {
		try (Reader reader = this.getReader(instructor)) {
			this.getFormat().parse(reader, this);
		}
	}

	@Override
	default void save() throws IOException {
		try (Writer writer = this.getWriter()) {
			this.getFormat().format(this, writer);
		}
	}
	@Override
	default void save(Instructor instructor) throws IOException {
		try (Writer writer = this.getWriter(instructor)) {
			this.getFormat().format(this, writer);
		}
	}

	/**
	 * Get the format used to parse/format the source of this loadable.
	 *
	 * @return the format of this loadable
	 */
	Format getFormat();
}
