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
package cufy.text;

import cufy.lang.Loadable;
import cufy.lang.Instructor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An object that can be loaded from a container. And that container provides an unformatted data that requires that loadable to format it.
 *
 * @author LSaferSE
 * @version 2 release (14-Feb-2020)
 * @since 13-Feb-2020
 */
public interface FormatLoadable extends Loadable {
	@Override
	default void load() throws IOException {
		AtomicReference buffer = new AtomicReference(this);
		try (Reader reader = this.getReader()) {
			this.getFormat().parse(
					buffer,
					reader,
					null,
					null
			);
		}
	}
	@Override
	default void load(Instructor instructor) throws IOException {
		AtomicReference buffer = new AtomicReference(this);
		try (Reader reader = this.getReader(instructor)) {
			this.getFormat().parse(
					buffer,
					reader,
					null,
					null
			);
		}
	}

	@Override
	default void save() throws IOException {
		try (Writer writer = this.getWriter()) {
			this.getFormat().format(
					writer,
					this,
					null,
					null
			);
		}
	}
	@Override
	default void save(Instructor instructor) throws IOException {
		try (Writer writer = this.getWriter(instructor)) {
			this.getFormat().format(
					writer,
					this,
					null,
					null
			);
		}
	}

	/**
	 * Get the formatter/parser to format/parse the data from/to the container.
	 *
	 * @return the format of this loadable
	 */
	Format getFormat();
}
