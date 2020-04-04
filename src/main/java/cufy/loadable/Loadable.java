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
import cufy.io.RemoteInputStream;
import cufy.io.RemoteOutputStream;
import cufy.io.RemoteReader;
import cufy.io.RemoteWriter;

import java.io.*;

/**
 * An object that can be loaded-from, saved-to another container.
 *
 * @author LSaferSE
 * @version 2 release (14-Feb-2020)
 * @since 13-Feb-2020
 */
public interface Loadable {
	/**
	 * Get a new, unused input stream from the container of this loadable.
	 *
	 * @param instructor a controller for the returned stream
	 * @return an input stream from the container of this loadable
	 * @throws IOException          if any I/O exception occurs
	 * @throws NullPointerException if the given instructor is null
	 */
	default InputStream getInputStream(Instructor instructor) throws IOException {
		return new RemoteInputStream(instructor, this.getInputStream());
	}
	/**
	 * Get a new, unused output stream from the container of this loadable.
	 *
	 * @param instructor a controller for the returned stream
	 * @return an output stream from the container of this loadable
	 * @throws IOException          if any I/O exception occurs
	 * @throws NullPointerException if the given instructor is null
	 */
	default OutputStream getOutputStream(Instructor instructor) throws IOException {
		return new RemoteOutputStream(instructor, this.getOutputStream());
	}

	/**
	 * Get a new, unused reader from the container of this loadable.
	 *
	 * @param instructor a controller for the returned reader
	 * @return an reader from the container of this loadable
	 * @throws IOException          if any I/O exception occurs
	 * @throws NullPointerException if the given instructor is null
	 */
	default Reader getReader(Instructor instructor) throws IOException {
		return new RemoteReader(instructor, this.getReader());
	}
	/**
	 * Get a new, unused writer from the container of this loadable.
	 *
	 * @param instructor a controller for the returned writer
	 * @return an writer from the container of this loadable
	 * @throws IOException          if any I/O exception occurs
	 * @throws NullPointerException if the given instructor is null
	 */
	default Writer getWriter(Instructor instructor) throws IOException {
		return new RemoteWriter(instructor, this.getWriter());
	}

	/**
	 * Get a new, unused input stream from the container of this loadable.
	 *
	 * @return an input stream from the container of this loadable
	 * @throws IOException if any I/O exception occurs
	 */
	InputStream getInputStream() throws IOException;
	/**
	 * Get a new, unused output stream from the container of this loadable.
	 *
	 * @return an output stream from the container of this loadable
	 * @throws IOException if any I/O exception occurs
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * Get a new, unused reader from the container of this loadable.
	 *
	 * @return an reader from the container of this loadable
	 * @throws IOException if any I/O exception occurs
	 */
	Reader getReader() throws IOException;
	/**
	 * Get a new, unused writer from the container of this loadable.
	 *
	 * @return an writer from the container of this loadable
	 * @throws IOException if any I/O exception occurs
	 */
	Writer getWriter() throws IOException;

	/**
	 * Load this loadable from the container of this loadable.
	 *
	 * @throws IOException if any I/O exception occurs
	 */
	void load() throws IOException;
	/**
	 * Load this loadable from the container of this loadable.
	 *
	 * @param instructor to control the loading operation
	 * @throws IOException          if any I/O exception occurs
	 * @throws NullPointerException if the given instructor is null
	 */
	void load(Instructor instructor) throws IOException;

	/**
	 * Save this loadable to the container of this loadable.
	 *
	 * @throws IOException if any I/O exception occurs
	 */
	void save() throws IOException;
	/**
	 * Save this loadable to the container of this loadable.
	 *
	 * @param instructor to control the saving operation
	 * @throws IOException          if any I/O exception occurs
	 * @throws NullPointerException if the given instructor is null
	 */
	void save(Instructor instructor) throws IOException;
}
