/*
 * Copyright (c) ${YEAR}, LSafer, All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * -You can edit this file (except the header).
 * -If you have change anything in this file. You
 *  shall mention that this file has been edited.
 *  By adding a new header (at the bottom of this header)
 *  with the word "Editor" on top of it.
 */

package cufy.io;

import org.cufy.annotation.Beta;

import java.io.File;
import java.util.Arrays;

/**
 * An exception designed for files synchronized processes. aka designed to be thrown to {@link cufy.io.File.Synchronizer#handle}
 *
 * @author LSaferSE
 * @version 2 release (03-Nov-2019)
 * @since 31-Oct-2019
 */
@Beta("ideas flow")
public class FileException extends RuntimeException {
	/**
	 * Thrown when a file is already exist. And the process targeting it as an output file.
	 */
	final public static String ALREADY_EXIST = "already exist";
	/**
	 * Thrown when the process can't delete the output file. And it don't know why.
	 */
	final public static String CANT_DELETE = "undeletable";
	/**
	 * Thrown when the process can't move the input file to the output file. And it don't know why.
	 */
	final public static String CANT_MOVE = "unmovable";
	/**
	 * Thrown when the process can't write on the output file. And it don't know why.
	 */
	final public static String CANT_WRITE = "can't write";
	/**
	 * Thrown when the process tried to do something with the output directory. And find it not empty.
	 */
	final public static String DIRECTORY_NOT_EMPTY = "directory not empty";
	/**
	 * Thrown when the process tried to do something with the input/output file. And find it a directory.
	 */
	final public static String IS_DIRECTORY = "is directory";
	/**
	 * Thrown when the process tried to do something with the input/output file. And find it not a directory.
	 */
	final public static String NOT_DIRECTORY = "not directory";
	/**
	 * Thrown when the process tried to do something with the input/output file. And find it not exist.
	 */
	final public static String NOT_EXIST = "not exist";

	/**
	 * The causes of the exception/error. Sorted by  input first.
	 */
	public File[] causes;
	/**
	 * The name of the error.
	 */
	public String name;

	/**
	 * Initialize this.
	 *
	 * @param name   the name of the error
	 * @param cause  the exception object causes the error
	 * @param causes the files causes the error. Or the files processing during the error
	 */
	public FileException(String name, Throwable cause, File... causes) {
		super(causes == null || causes.length == 0 ? name : Arrays.toString(causes) + " (" + name + ")", cause);
		this.name = name;
		this.causes = causes;
	}

	/**
	 * Initialize this.
	 *
	 * @param name   the name of the error
	 * @param causes the files causes the error. Or the files processing during the error
	 */
	public FileException(String name, File... causes) {
		this(name, null, causes);
	}

	/**
	 * Initialize this.
	 *
	 * @param name  the name of the error
	 * @param cause the exception object causes the error
	 */
	public FileException(String name, Throwable cause) {
		this(name, cause, (File[]) null);
	}

	/**
	 * Initialize this.
	 *
	 * @param name the name of the error
	 */
	public FileException(String name) {
		this(name, null, (File[]) null);
	}
}
