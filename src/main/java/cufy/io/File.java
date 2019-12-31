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

import cufy.lang.Instructor;
import cufy.lang.Loop;
import cufy.text.Format;
import cufy.util.ArrayUtil;
import cufy.util.ObjectUtil;
import cufy.util.StringUtil;
import org.cufy.lang.BaseConverter;
import org.cufy.lang.For;
import org.cufy.lang.Foreach;
import org.cufy.lang.Parallel;

import java.io.*;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static cufy.io.FileException.*;

/**
 * A {@link java.io.File} with useful tools.
 *
 * @author LSaferSE
 * @version 10 release (03-Nov-2019)
 * @since 18 May 2019
 */
public class File extends java.io.File {
	/**
	 * Flags that the process have been canceled.
	 */
	@Deprecated
	final public static int PROCESS_CANCELED = -1;
	/**
	 * Flags that the process continued (or still continuing) to the end without exceptions.
	 */
	@Deprecated
	final public static int PROCESS_CONTINUED = 1;
	/**
	 * Flags that the process failed on some part. But still continued (or still continuing) to the end.
	 */
	@Deprecated
	final public static int PROCESS_FAILED = 0;
	/**
	 * The default exception handler. Designed to handle exceptions thrown by {@link Synchronizer#handle}
	 */
	@Deprecated
	final public static Function<FileException, Integer> HANDLER_DEFAULT = exception -> {
		if (exception.getCause() != null)
			exception.getCause().printStackTrace();
		switch (exception.name) {
			case NOT_EXIST:
			case ALREADY_EXIST:
			case CANT_WRITE:
			case CANT_MOVE:
			case CANT_DELETE:
			case IS_DIRECTORY:
			default:
				return File.PROCESS_FAILED;
			case NOT_DIRECTORY:
				return exception.causes[0].mkdirs() ? File.PROCESS_CONTINUED : File.PROCESS_FAILED;
			case DIRECTORY_NOT_EMPTY:
				return File.PROCESS_CONTINUED;
		}
	};
	/**
	 * An exception handler uses force to continue processes. Designed ot handle exceptions thrown by {@link Synchronizer#handle}
	 */
	@Deprecated
	final public static Function<FileException, Integer> HANDLER_FORCE = exception -> {
		if (exception.getCause() != null)
			exception.getCause().printStackTrace();
		switch (exception.name) {
			case NOT_EXIST:
			case CANT_WRITE:
			case CANT_MOVE:
			case CANT_DELETE:
			default:
				return File.PROCESS_FAILED;
			case ALREADY_EXIST:
			case IS_DIRECTORY:
				return exception.causes[0].delete() ? File.PROCESS_CONTINUED : File.PROCESS_FAILED;
			case NOT_DIRECTORY:
				return exception.causes[0].delete() && exception.causes[0].mkdirs() ? File.PROCESS_CONTINUED : File.PROCESS_FAILED;
			case DIRECTORY_NOT_EMPTY:
				return File.PROCESS_CONTINUED;
		}
	};

	/**
	 * if this file's name starts with dot.
	 * <br>
	 * normal :     "file" dotHidden :  ".file"
	 */
	protected Boolean dot_hidden;
	/**
	 * This file's type extension.
	 * <br><br><b>example:</b>
	 * <pre>
	 * name :       "title.extension"
	 * extension :  "extension"
	 * </pre>
	 */
	protected String extension;
	/**
	 * General mime Of this file.
	 * <br><br><b>example:</b>
	 * <pre>
	 *     "image/png"
	 * </pre>
	 */
	protected String mime;
	/**
	 * The progress of current processing method on this instance.
	 */
	@Deprecated
	protected Long progress = 0L;
	/**
	 * The max progress for the current processing method on this instance.
	 */
	@Deprecated
	protected Long progress_max = 1L;
	/**
	 * This file's copy number. In case there is other files with the same name.
	 */
	protected Integer suffix = null;
	/**
	 * This file's title without copy number.
	 * <br><br><b>example:</b>
	 * <pre>
	 * title :      "title (1)"
	 * cleanTitle : "title"
	 * </pre>
	 */
	protected String suffixless_title;
	/**
	 * This file's name without type extension.
	 * <br><br><b>example:</b>
	 * <pre>
	 * name :   "title.extension"
	 * title :  "title"
	 * </pre>
	 */
	protected String title;

	/**
	 * Initialize this with an absolute path.
	 *
	 * @param pathname absolute path
	 * @see java.io.File#File(String) original method
	 */
	public File(String pathname) {
		super(pathname);
	}

	/**
	 * Copy from other file.
	 *
	 * @param file to copy from
	 */
	public File(java.io.File file) {
		super(file.toString());
	}

	/**
	 * Initialize this using parent's file object.
	 *
	 * @param parent file of the targeted file
	 * @param child  of the targeted file
	 * @see java.io.File#File(java.io.File, String) the original method
	 */
	public File(java.io.File parent, String child) {
		super(parent, child);
	}

	/**
	 * Initialize this using parent's path.
	 *
	 * @param parent path of the targeted file's parent
	 * @param child  of the targeted file
	 * @see java.io.File#File(String, String) the original method
	 */
	public File(String parent, String child) {
		super(parent, child);
	}

	/**
	 * Initialize this using an {@link URI}.
	 *
	 * @param uri of the targeted file
	 * @see java.io.File#File(URI) original method
	 */
	public File(URI uri) {
		super(uri);
	}

	@Override
	public File getParentFile() {
		//As super method
		String p = this.getParent();
		return p == null ? null : new File(p);
	}

	@Override
	public File getAbsoluteFile() {
		//As super method
		return new File(this.getAbsolutePath());
	}

	@Override
	public File getCanonicalFile() throws IOException {
		//As super method
		return new File(this.getCanonicalPath());
	}

	@Override
	public File[] listFiles() {
		//As super method
		String[] list = this.list();
		if (list == null) {
			return null;
		} else {
			File[] files = new File[list.length];

			for (int i = 0; i < files.length; ++i)
				files[i] = new File(this, list[i]);

			return files;
		}
	}

	@Override
	public File[] listFiles(FilenameFilter filter) {
		//As super method
		String[] list = this.list();

		if (list == null) {
			return null;
		} else {
			ArrayList<File> files = new ArrayList<>();

			for (String element : list)
				if (filter == null || filter.accept(this, element))
					files.add(new File(this, element));

			return files.toArray(new File[0]);
		}
	}

	@Override
	public File[] listFiles(FileFilter filter) {
		//As super method
		String[] list = this.list();

		if (list == null) {
			return null;
		} else {
			ArrayList<File> files = new ArrayList<>();

			for (String element : list) {
				File file = new File(this, element);

				if (filter == null || filter.accept(file))
					files.add(file);
			}

			return files.toArray(new File[0]);
		}
	}

	/**
	 * Append the given string to the text written on this file.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_DIRECTORY} the parent of this is not a directory.</li>
	 *     <li>{@link FileException#IS_DIRECTORY} this file is a directory.</li>
	 *     <li>{@link FileException#CANT_WRITE} can't write on this file.</li>
	 * </ul>
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @param value        to be appended to the text of this file
	 */
	@Deprecated
	public void append(Synchronizer<?, ?> synchronizer, String value) {
		java.io.File parent = this.parent();

		if (synchronizer.handle(!parent.isDirectory(), NOT_DIRECTORY, parent) <= PROCESS_FAILED ||
			synchronizer.handle(this.isDirectory(), IS_DIRECTORY, this) <= PROCESS_FAILED)
			return;

		try (FileWriter fw = new FileWriter(this)) {
			//<editor-fold desc="synchronizer.bind()">
			this.setMaxProgress(1L);
			this.setProgress(0L);
			synchronizer.out(this);
			synchronizer.tick();
			//</editor-fold>
			fw.append(value);
			//<editor-fold desc="synchronizer.bind()">
			this.progressed();
			synchronizer.tick();
			//</editor-fold>
		} catch (IOException e) {
			if (synchronizer.handle(CANT_WRITE, this) >= PROCESS_CONTINUED)
				this.append(synchronizer, value);
		}
	}

	/**
	 * Stringify the given object using the given parser. Then Append the output string to the txt of this file.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_DIRECTORY} the parent of this is not a directory.</li>
	 *     <li>{@link FileException#IS_DIRECTORY} this file is a directory.</li>
	 *     <li>{@link FileException#CANT_WRITE} can't write on this file.</li>
	 * </ul>
	 *
	 * @param parser       to be used to stringify the given object
	 * @param value        to append the output from stringing it
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	@Deprecated
	public void append(Synchronizer<?, ?> synchronizer, Format parser, Object value) {
		this.append(synchronizer, parser.format(value));
	}

	/**
	 * Get a child of this with the given name.
	 *
	 * @param name name of child to get
	 * @return a child of this file with specified name
	 */
	public File child(String name) {
		return new File(this, name);
	}

	/**
	 * Get this file's children in a {@link List}.
	 *
	 * @return this file's children
	 */
	public List<File> children() {
		List<File> children = new ArrayList<>();

		String[] list = this.list();

		if (list != null)
			for (String child : list)
				children.add(new File(this, child));

		return children;
	}

	/**
	 * Copy all this file's content to other folder.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_EXIST} this file not exist.</li>
	 *     <li>{@link FileException#NOT_DIRECTORY} the output's parent file is not a directory.</li>
	 *     <li>{@link FileException#ALREADY_EXIST} the output file already exist.</li>
	 *     <li>{@link FileException#CANT_WRITE} the output file can't be make as a directory.</li>
	 *     <li>
	 *         {@link FileNotFoundException} if the file does not exist,
	 *         is a directory rather than a regular file,
	 *         or for some other reason cannot be opened
	 *     </li>
	 *     <li>
	 *         {@link SecurityException} if a security manager exists and its
	 *         checkWrite method denies write access to the file. Or if a
	 *         security manager exists and its checkRead method denies
	 *         read access to the file.
	 *     </li>
	 *     <li>{@link IOException} if an I/O error occurs.</li>
	 * </ul>
	 * <p>
	 * Note: you can change the bufferSize on {@link Synchronizer#byteBufferSize}.
	 *
	 * @param output       the file to paste this to
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	@Deprecated
	public void copy(Synchronizer<?, ?> synchronizer, java.io.File output) {
		File dest = new File(output), parent = dest.parent();

		if (synchronizer.handle(!this.exists(), NOT_EXIST, this) <= PROCESS_FAILED ||
			synchronizer.handle(!parent.isDirectory(), NOT_DIRECTORY, parent) <= PROCESS_FAILED ||
			synchronizer.handle(dest.exists(), ALREADY_EXIST, dest) <= PROCESS_FAILED)
			return;

		if (this.isDirectory()) {
			if (synchronizer.handle(!dest.mkdirs(), CANT_WRITE, dest) <= PROCESS_FAILED)
				return;

			//<editor-fold desc="synchronizer.bind()">
			long max = this.getCount(false, true);
			this.setMaxProgress(max);
			this.setProgress(0L);
			dest.setMaxProgress(max);
			dest.setProgress(0L);
			synchronizer.in(this);
			synchronizer.out(dest);
			synchronizer.tick();
			//</editor-fold>
			synchronizer.start(new Foreach<>(this.children(), (loop, child) -> {
				child.copy(synchronizer, dest);
				if (synchronizer.status <= PROCESS_CANCELED) {
					loop.notify(Loop.BREAK);
				} else {
					//<editor-fold desc="synchronizer.bind()">
					this.progressed();
					dest.progressed();
					synchronizer.tick();
					//</editor-fold>
				}
			}));
		} else {
			try (FileInputStream fis = new FileInputStream(this);
				 FileOutputStream fos = new FileOutputStream(dest)) {
				byte[] buffer = new byte[synchronizer.byteBufferSize];
				int[] length = {0};

				//<editor-fold desc="synchronizer.bind()">
				long max = this.length() / synchronizer.byteBufferSize;
				this.setMaxProgress(max);
				this.setProgress(0L);
				dest.setMaxProgress(max);
				dest.setProgress(0L);
				synchronizer.in(this);
				synchronizer.out(dest);
				synchronizer.tick();
				//</editor-fold>
				synchronizer.start(new Parallel(loop -> {
					try {
						if ((length[0] = fis.read(buffer)) > 0) {
							fos.write(buffer, 0, length[0]);
							//<editor-fold desc="synchronizer.bind()">
							this.progressed();
							dest.progressed();
							synchronizer.tick();
							//</editor-fold>
							return;
						}
					} catch (SecurityException | IOException e) {
						if (synchronizer.handle(e.getClass().getName(), e, this, dest) >= PROCESS_CONTINUED)
							this.copy(synchronizer, dest);
					}
					loop.notify(Loop.BREAK);
				}));
			} catch (SecurityException | IOException e) {
				if (synchronizer.handle(e.getClass().getName(), e, this, dest) >= PROCESS_CONTINUED)
					this.copy(synchronizer, dest);
			}
		}
	}

	/**
	 * Delete this file. This method deletes folders with it's children too.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_EXIST} this file not exist</li>
	 *     <li>
	 *         {@link FileException#DIRECTORY_NOT_EMPTY} this file have some files on it.
	 *         return {@link #PROCESS_CONTINUED} on the catcher to delete it's files.
	 *     </li>
	 *     <li>
	 *         {@link FileException#CANT_DELETE} this file can't be deleted.
	 *         return {@link #PROCESS_CONTINUED} on the catcher to try again.
	 *     </li>
	 *     <li>
	 *         {@link SecurityException}  If a security manager exists and its
	 *         SecurityManager.checkDelete(java.lang.String) method denies
	 *         delete access to the file.
	 *     </li>
	 * </ul>
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	@Deprecated
	public void delete(Synchronizer<?, ?> synchronizer) {
		if (synchronizer.handle(!this.exists(), NOT_EXIST, this) <= PROCESS_FAILED)
			return;

		long filesCount = this.getCount(false, true);

		if (filesCount > 1) {
			if (synchronizer.handle(DIRECTORY_NOT_EMPTY, this) <= PROCESS_FAILED)
				return;

			//<editor-fold desc="synchronizer.bind()">
			this.setMaxProgress(filesCount);
			this.setProgress(0L);
			synchronizer.out(this);
			synchronizer.tick();
			//</editor-fold>
			synchronizer.start(new Foreach<>(this.children(), (loop, child) -> {
				child.delete(synchronizer);
				if (synchronizer.status <= PROCESS_CANCELED) {
					loop.notify(Loop.BREAK);
				} else {
					//<editor-fold desc="synchronizer.bind()">
					this.progressed();
					synchronizer.tick();
					//</editor-fold>
				}
			}));
		}

		//<editor-fold desc="synchronizer.bind()">
		this.setMaxProgress(1L);
		this.setProgress(0L);
		synchronizer.out(this);
		synchronizer.tick();
		//</editor-fold>

		try {
			if (this.delete()) {
				//<editor-fold desc="synchronizer.bind()">
				this.progressed();
				synchronizer.tick();
				//</editor-fold>
			} else if (synchronizer.handle(CANT_DELETE, this) >= PROCESS_CONTINUED) {
				this.delete(synchronizer);
			}
		} catch (SecurityException e) {
			if (synchronizer.handle(e.getClass().getName(), e, this) >= PROCESS_CONTINUED)
				this.delete(synchronizer);
		}
	}

	/**
	 * Get the total count of files inside this (including this).
	 *
	 * @param dirs whether to include directories or not
	 * @param non  whether to include non-directory files or not
	 * @return count of files
	 */
	public long getCount(boolean dirs, boolean non) {
		if (this.isDirectory()) {
			long count = dirs ? 1L : 0L;
			for (File child : this.children())
				count += child.getCount(dirs, non);
			return count;
		} else {
			return non && this.exists() ? 1L : 0L;
		}
	}

	/**
	 * Get this file's type extension as written on it's name
	 * <br><br><b>example:</b>
	 * <pre>
	 * name :       "title.extension"
	 * extension :  "extension"
	 * </pre>
	 *
	 * @return extension of this file
	 * @see #extension cache
	 */
	public String getExtension() {
		return ObjectUtil.requireNonNullElseGet(this.extension, () -> {
			String[] split = this.getName().split("[.]");
			return this.extension = split.length <= 1 || (split.length == 2 && split[0].equals("")) ? "" : split[split.length - 1];
		});
	}

	/**
	 * Get the max progress for the current processing method on this instance.
	 *
	 * @return the max progress for the current process
	 */
	@Deprecated
	public Long getMaxProgress() {
		return this.progress_max;
	}

	/**
	 * Get the mime of this file by it's name.
	 *
	 * @return this file's mime
	 * @see #mime cache
	 */
	public String getMime() {
		return ObjectUtil.requireNonNullElseGet(this.mime, () -> {
			String mime = URLConnection.guessContentTypeFromName(this.getName());
			return this.mime = mime == null ? "*/" + this.getExtension() : mime;
		});
	}

	/**
	 * Get the progress of current processing method on this instance.
	 *
	 * @return the process of the current process
	 */
	@Deprecated
	public Long getProgress() {
		return this.progress;
	}

	/**
	 * Get this file's title without the suffix.
	 * <br><br><b>example</b>
	 * <pre>
	 * title :      "title (1).extension"
	 * cleanTitle : "title.extension"
	 * </pre>
	 *
	 * @return this file's title without the suffix
	 * @see #suffixless_title cache
	 */
	public String getSuffixlessTitle() {
		return ObjectUtil.requireNonNullElseGet(this.suffixless_title, () -> {
			String title = this.getTitle();
			String[] split = title.split(" ");
			String number = split[split.length - 1];

			if (number.charAt(0) == '(' && number.charAt(number.length() - 1) == ')') {
				number = StringUtil.crop(split[split.length - 1], 1, 1);
				this.suffix = BaseConverter.global.convert(number, Integer.class);
				split = ArrayUtil.sublist(split, 0, 1);
				return this.suffixless_title = StringUtil.join(" ", "", split);
			}

			return this.suffixless_title = title;
		});
	}

	/**
	 * Get this file's name without the type extension.
	 * <br><br><b>example</b>
	 * <pre>
	 * name :   "title.extension"
	 * title :  "title"
	 * </pre>
	 *
	 * @return this file's title
	 * @see #title cache
	 */
	public String getTitle() {
		return ObjectUtil.requireNonNullElseGet(this.title, () -> {
			String[] split = this.getName().split("[.]");
			return this.title = split.length <= 2 && split[0].equals("") ? split[split.length - 1] :
								StringUtil.join(".", "", ArrayUtil.sublist(split, 0, 1));
		});
	}

	/**
	 * Get whether this file is hidden by a dot at the first of it's name or not.
	 *
	 * @return whether this file is hidden by a dot or not
	 * @see #dot_hidden cache
	 */
	public boolean isDotHidden() {
		return ObjectUtil.requireNonNullElseGet(this.dot_hidden, () -> {
			String[] split = this.getName().split("[.]");
			return this.dot_hidden = split.length >= 1 && split[0].equals("");
		});
	}

	/**
	 * Get a file with a name of this and also hadn't used by any of this file's siblings.
	 * <br><br><b>example:</b>
	 * <pre>
	 * used title :     title.extension
	 * unused title :   title (1).extension
	 * </pre>
	 *
	 * @return a file of this with a name that hadn't used by any of this file's siblings
	 */
	public File junior() {
		if (!this.exists())
			return this;

		String extension = this.getExtension();
		String title = this.getSuffixlessTitle();
		String a = (this.getParent() == null ? "" : this.getParent() + "/") + this.getSuffixlessTitle() + " (";
		String b = ")" + (extension.equals("") ? "" : "." + extension);

		File junior;
		int suffix = this.suffix() + 1;
		while ((junior = new File(a + suffix++ + b)).exists()) ; //

		return junior;
	}

	/**
	 * Move this file to the given destination.
	 *
	 * <u>
	 * Exception may applied to the catcher (unexpected exception may applied):
	 * <li>{@link FileException#NOT_EXIST} if this file not exist.</li>
	 * <li>{@link FileException#NOT_DIRECTORY} if the parent of the output file is not a directory.</li>
	 * <li>{@link FileException#ALREADY_EXIST} if the output file is already exist.</li>
	 * <li>{@link FileException#CANT_MOVE} if somehow failed to move this file.</li>
	 * <li>
	 * {@link SecurityException} If a security manager exists and its SecurityManager.checkWrite(java.lang.String) method denies write access to
	 * either the old or new pathname
	 * </li>
	 * </u>
	 *
	 * @param output       the file to paste this to
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	@Deprecated
	public void move(Synchronizer<?, ?> synchronizer, java.io.File output) {
		File dest = new File(output), parent = dest.parent();

		if (synchronizer.handle(!this.exists(), NOT_EXIST, this) <= PROCESS_FAILED ||
			synchronizer.handle(!parent.isDirectory(), NOT_DIRECTORY, parent) <= PROCESS_FAILED ||
			synchronizer.handle(dest.exists(), ALREADY_EXIST, dest) <= PROCESS_FAILED)
			return;

		//<editor-fold desc="synchronizer.bind()">
		this.setMaxProgress(1L);
		this.setProgress(0L);
		dest.setMaxProgress(1L);
		dest.setProgress(0L);
		synchronizer.in(this);
		synchronizer.out(dest);
		synchronizer.tick();
		//</editor-fold>

		try {
			if (this.renameTo(dest)) {
				//<editor-fold desc="synchronizer.bind()">
				this.progressed();
				dest.progressed();
				synchronizer.tick();
				//</editor-fold>
			} else if (synchronizer.handle(CANT_MOVE, this, dest) >= PROCESS_CONTINUED) {
				this.move(synchronizer, dest);
			}
		} catch (SecurityException e) {
			if (synchronizer.handle(e.getClass().getName(), e, this, dest) >= PROCESS_CONTINUED)
				this.move(synchronizer, dest);
		}
	}

	/**
	 * Get the parent file of this.
	 *
	 * @return the parent file of this
	 */
	public File parent() {
		File parent = this.getParentFile();
		return parent == null ? new File("") : parent;
	}

	/**
	 * Increase the progress value of this file.
	 *
	 * @param by the values to increase (will increase by 1 if an empty array passed)
	 */
	@Deprecated
	public void progressed(Long... by) {
		this.progress += by.length == 0 ? 1L : ArrayUtil.sum(by, value -> value);
	}

	/**
	 * Search for files that contains one of the given queries on it's name.
	 *
	 * @param queries the queries of the wanted files
	 * @return files that have specific queries
	 */
	@Deprecated
	public List<File> query(String... queries) {
		List<File> found = new ArrayList<>();

		if (StringUtil.any(this.getName(), queries))
			found.add(this);

		for (File child : this.children())
			found.addAll(child.query(queries));

		return found;
	}

	/**
	 * Read this file's Content as a {@link String}.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_EXIST} this file not exist.</li>
	 *     <li>{@link FileException#IS_DIRECTORY} this file is a directory.</li>
	 *     <li>{@link IOException} If an I/O error occurs< /li>
	 * </ul>
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @return value of this file. Or null in case of exceptions
	 */
	@Deprecated
	public String read(Synchronizer<?, ?> synchronizer) {
		if (synchronizer.handle(!this.exists(), NOT_EXIST, this) <= PROCESS_FAILED ||
			synchronizer.handle(this.isDirectory(), IS_DIRECTORY, this) <= PROCESS_FAILED)
			return null;

		try (FileReader fr = new FileReader(this)) {
			StringBuilder text = synchronizer.text = new StringBuilder();
			int[] buffer = new int[1];

			//<editor-fold desc="synchronizer.bind()">
			this.setMaxProgress(this.length() / synchronizer.byteBufferSize);
			this.setProgress(0L);
			synchronizer.in(this);
			synchronizer.tick();
			//</editor-fold>
			synchronizer.start(new For<>(0, i -> true, i -> ++i, (loop, i) -> {
				try {
					if ((buffer[0] = fr.read()) != -1) {
						text.append((char) buffer[0]);
						if ((i % synchronizer.byteBufferSize) == 0) {
							//<editor-fold desc="synchronizer.bind()">
							this.progressed();
							synchronizer.tick();
							//</editor-fold>
						}
						return;
					}
				} catch (IOException e) {
					if (synchronizer.handle(e.getClass().getName(), e, this) >= PROCESS_CONTINUED)
						//noinspection ResultOfMethodCallIgnored
						this.read(synchronizer);
				}
				loop.notify(Loop.BREAK);
			}));

			return text.toString();
		} catch (IOException e) {
			if (synchronizer.handle(e.getClass().getName(), e, this) >= PROCESS_CONTINUED)
				return this.read(synchronizer);
			return null;
		}
	}

	/**
	 * Read this file's Content as a {@link String}. Then parse it using the specified parser.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_EXIST} this file not exist.</li>
	 *     <li>{@link FileException#IS_DIRECTORY} this file is a directory.</li>
	 *     <li>{@link IOException} If an I/O error occurs< /li>
	 * </ul>
	 *
	 * @param parser       to use to parse the read text
	 * @param klass        to make sure the value is instance of
	 * @param <T>          the assumed type of the written text after parsing
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @return value of this file parsed. Or null in case of exceptions
	 */
	@Deprecated
	public <T> T read(Synchronizer<?, ?> synchronizer, Format parser, Class<? super T> klass) {
		Object object = parser.parse(this.read(synchronizer));
		return klass.isInstance(object) ? (T) object : null;
	}

	/**
	 * Read this file's java serial text. And transform it to the targeted class.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_EXIST} this file not exist.</li>
	 *     <li>{@link FileException#IS_DIRECTORY} this file is a directory.</li>
	 *     <li>
	 *         {@link SecurityException}  if a security manager exists
	 *         and its checkRead method denies read access to the file.
	 *         Or if untrusted subclass illegally overrides security-sensitive methods.
	 *     </li>
	 *     <li>{@link StreamCorruptedException} if the stream header is incorrect. Or control information in the stream is inconsistent.</li>
	 *     <li>{@link ClassNotFoundException} Class of a serialized object cannot be found.</li>
	 *     <li>{@link InvalidClassException} Something is wrong with a class used by serialization.</li>
	 *     <li>{@link OptionalDataException} Primitive data was found in the stream instead of objects.</li>
	 *     <li>{@link IOException} if an I/O error occurs while reading stream header.</li>
	 * </ul>
	 * <p>
	 *
	 * @param klass        klass of needed object (just to make sure the object we read is instance of the targeted class)
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @param <S>          targeted class type
	 * @return transformed Java Serial write in this file
	 * @see Serializable
	 */
	@Deprecated
	public <S extends Serializable> S readSerial(Synchronizer<?, ?> synchronizer, Class<S> klass) {
		if (synchronizer.handle(!this.exists(), NOT_EXIST, this) <= PROCESS_FAILED ||
			synchronizer.handle(this.isDirectory(), IS_DIRECTORY, this) <= PROCESS_FAILED)
			return null;

		try (FileInputStream fis = new FileInputStream(this);
			 ObjectInputStream ois = new ObjectInputStream(fis)) {
			S value = (S) ois.readObject();

			if (klass.isInstance(value))
				return value;
		} catch (IOException | ClassNotFoundException e) {
			if (synchronizer.handle(e.getClass().getName(), e, this) >= PROCESS_CONTINUED)
				return this.readSerial(synchronizer, klass);
		}

		return null;
	}

	/**
	 * Rename this file to a new name.
	 *
	 * <u>
	 * Exception may applied to the catcher (unexpected exception may applied):
	 * <li>{@link FileException#NOT_EXIST} if this file not exist.</li>
	 * <li>{@link FileException#NOT_DIRECTORY} if the parent of the output file is not a directory.</li>
	 * <li>{@link FileException#ALREADY_EXIST} if the output file is already exist.</li>
	 * <li>{@link FileException#CANT_MOVE} if somehow failed to move this file.</li>
	 * <li>
	 * {@link SecurityException} If a security manager exists and its SecurityManager.checkWrite(java.lang.String) method denies write access to
	 * either the old or new pathname
	 * </li>
	 * </u>
	 *
	 * @param name         new name
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	@Deprecated
	public void rename(Synchronizer<?, ?> synchronizer, String name) {
		File dest = this.sibling(name);

		if (synchronizer.handle(!this.exists(), NOT_EXIST, this) <= PROCESS_FAILED ||
			synchronizer.handle(dest.exists(), ALREADY_EXIST, dest) <= PROCESS_FAILED)
			return;

		//<editor-fold desc="synchronizer.bind()">
		this.setMaxProgress(1L);
		this.setProgress(0L);
		dest.setMaxProgress(1L);
		dest.setProgress(0L);
		synchronizer.in(this);
		synchronizer.out(dest);
		synchronizer.tick();
		//</editor-fold>

		try {
			if (this.renameTo(dest)) {
				this.progressed();
				dest.progressed();
				synchronizer.tick();
			} else if (synchronizer.handle(CANT_MOVE, this, dest) >= PROCESS_CONTINUED) {
				this.rename(synchronizer, name);
			}
		} catch (SecurityException e) {
			if (synchronizer.handle(e.getClass().getName(), e, this, dest) >= PROCESS_CONTINUED)
				this.rename(synchronizer, name);
		}
	}

	/**
	 * Set the max progress for the current processing method on this instance.
	 *
	 * @param max value to be set
	 */
	@Deprecated
	public void setMaxProgress(Long max) {
		this.progress_max = max;
	}

	/**
	 * Set the progress of current processing method on this instance.
	 *
	 * @param progress value to be set
	 */
	@Deprecated
	public void setProgress(Long progress) {
		this.progress = progress;
	}

	/**
	 * Get a sibling file of this with the same passed name.
	 *
	 * @param name of the sibling file
	 * @return a sibling of this with the same name of the given name
	 */
	public File sibling(String name) {
		return new File(this.getParent(), name);
	}

	/**
	 * Get the size of this file.
	 *
	 * @return this file's total size
	 */
	public long size() {
		if (this.isDirectory()) {
			long size = 0;

			for (File child : this.children())
				size += child.size();

			return size;
		} else {
			return this.exists() ? this.length() : 0L;
		}
	}

	/**
	 * Get this file's parent and grand and grand grand and so on. Sorted from main-root to this.
	 *
	 * @return this file and it's parents
	 */
	public List<File> stack() {
		List<File> stack = new ArrayList<>();

		for (File file = this; file != null; file = file.getParentFile())
			stack.add(file);

		return stack;
	}

	/**
	 * The suffix (or copy number) of this file.
	 * <p>
	 * ex.
	 * <pre>
	 *     path: "/root/parent/title (1).extension"
	 *     suffix: 1
	 * </pre>
	 *
	 * @return the suffix number of this file
	 */
	public int suffix() {
		return ObjectUtil.requireNonNullElseGet(this.suffix, () -> {
			//noinspection ResultOfMethodCallIgnored
			this.getSuffixlessTitle();
			return this.suffix;
		});
	}

	/**
	 * write the given string to the text written on this file.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_DIRECTORY} the parent of this is not a directory.</li>
	 *     <li>{@link FileException#IS_DIRECTORY} this file is a directory.</li>
	 *     <li>{@link FileException#CANT_WRITE} can't write on this file.</li>
	 * </ul>
	 *
	 * @param value        to be written to the text of this file
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	@Deprecated
	public void write(Synchronizer<?, ?> synchronizer, String value) {
		java.io.File parent = this.parent();

		if (synchronizer.handle(!parent.isDirectory(), NOT_DIRECTORY, parent) <= PROCESS_FAILED ||
			synchronizer.handle(this.isDirectory(), IS_DIRECTORY, this) <= PROCESS_FAILED)
			return;

		try (FileWriter fw = new FileWriter(this)) {
			//<editor-fold desc="synchronizer.bind()">
			this.setMaxProgress(1L);
			this.setProgress(0L);
			synchronizer.out(this);
			synchronizer.tick();
			//</editor-fold>
			fw.write(value);
			//<editor-fold desc="synchronizer.bind()">
			this.progressed();
			synchronizer.tick();
			//</editor-fold>
		} catch (IOException e) {
			if (synchronizer.handle(e.getClass().getName(), e, this) >= PROCESS_CONTINUED)
				this.write(synchronizer, value);
		}
	}

	/**
	 * Stringify the given object using the given parser. Then write the output string to the txt of this file.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_DIRECTORY} the parent of this is not a directory.</li>
	 *     <li>{@link FileException#IS_DIRECTORY} this file is a directory.</li>
	 *     <li>{@link FileException#CANT_WRITE} can't write on this file.</li>
	 * </ul>
	 *
	 * @param parser       to be used to stringify the given object
	 * @param value        to write the output from stringing it
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	@Deprecated
	public void write(Synchronizer<?, ?> synchronizer, Format parser, Object value) {
		this.write(synchronizer, parser.format(value));
	}

	/**
	 * Write a java serial text of the given {@link Serializable} in this file.
	 *
	 * <ul>
	 *     Exception may applied to the catcher:
	 *     <li>{@link FileException#NOT_DIRECTORY} the parent of this is not a directory.</li>
	 *     <li>{@link FileException#IS_DIRECTORY} this file is a directory.</li>
	 *     <li>{@link FileException#CANT_WRITE} can't write on this file.</li>
	 *     <li>
	 *         {@link SecurityException} if a security manager exists and its checkWrite method denies
	 *         write access to the file. Or if untrusted subclass illegally overrides security-sensitive methods.
	 *     </li>
	 *     <li>{@link InvalidClassException}  Something is wrong with a class used by serialization.</li>
	 *     <li>{@link IOException} if an I/O error occurs while writing stream header</li>
	 *     <li>{@link NotSerializableException} Some object to be serialized does not implement the java.io.Serializable interface.</li>
	 * </ul>
	 *
	 * @param value        to write
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	@Deprecated
	public void writeSerial(Synchronizer<?, ?> synchronizer, Serializable value) {
		java.io.File parent = this.parent();

		if (synchronizer.handle(!parent.isDirectory(), NOT_DIRECTORY, parent) <= PROCESS_FAILED ||
			synchronizer.handle(this.isDirectory(), IS_DIRECTORY, this) <= PROCESS_FAILED)
			return;

		try (FileOutputStream fos = new FileOutputStream(this);
			 ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			//<editor-fold desc="synchronizer.bind()">
			this.setMaxProgress(1L);
			this.setProgress(0L);
			synchronizer.out(this);
			synchronizer.tick();
			//</editor-fold>
			oos.writeObject(value);
			//<editor-fold desc="synchronizer.bind()">
			this.progressed();
			synchronizer.tick();
			//</editor-fold>
		} catch (IOException | SecurityException e) {
			if (synchronizer.handle(e.getClass().getName(), e, this) >= PROCESS_CONTINUED)
				this.writeSerial(synchronizer, value);
		}
	}

	/**
	 * Synchronizer version for files.
	 */
	@Deprecated
	public static class Synchronizer<K, V> extends Instructor {
		/**
		 * Errors that have been occurred during the process.
		 */
		final public ArrayList<Throwable> errors = new ArrayList<>();
		/**
		 * Processed/Processing input files.
		 */
		final public List<File> in = new ArrayList<>();
		/**
		 * Processed/Processing output files.
		 */
		final public List<File> out = new ArrayList<>();
		/**
		 * The size of bytes to be processed before this synchronizer get bound again.
		 */
		public int byteBufferSize = 1024;
		/**
		 * The function to be used by the method {@link #handle} to handle exceptions.
		 */
		public Function<FileException, Integer> handler = HANDLER_DEFAULT;
		/**
		 * Results.
		 */
		public Integer status = PROCESS_CONTINUED;
		/**
		 * A text field for text transaction between threads.
		 */
		public StringBuilder text = new StringBuilder();

		/**
		 * Default constructor.
		 */
		public Synchronizer() {
		}

		/**
		 * Initialize this.
		 *
		 * @param listeners get called when this synchronizer get bound
		 */
		@SafeVarargs
		public Synchronizer(BiFunction<Instructor, Loop<?, ?>, Boolean>... listeners) {
			this.posts.addAll(java.util.Arrays.asList(listeners));
		}

		/**
		 * Handle the given error/exception and return whether the caller should {@link #PROCESS_CONTINUED continue}, {@link #PROCESS_FAILED fail} or
		 * {@link #PROCESS_CANCELED cancel}.
		 *
		 * @param condition weather to handle the exception or just return {@link #PROCESS_CONTINUED continue}.
		 * @param error     the error/exception name
		 * @param cause     the throwable object to be handled
		 * @param causes    the files cases the exception (input always first)
		 * @return whether the caller shall continue, fail or cancel
		 */
		public int handle(boolean condition, String error, Throwable cause, java.io.File... causes) {
			if (!condition)
				return PROCESS_CONTINUED;

			FileException exception = new FileException(error, cause, causes);
			int i = this.handler.apply(exception);

			if (i <= PROCESS_FAILED) {
				this.errors.add(exception);
				this.status = Math.min(i, this.status);
				this.tick();
			}

			return i;
		}

		/**
		 * Handle the given error/exception and return whether the caller should {@link #PROCESS_CONTINUED continue}, {@link #PROCESS_FAILED fail} or
		 * {@link #PROCESS_CANCELED cancel}.
		 *
		 * @param condition weather to handle the exception or just return {@link #PROCESS_CONTINUED continue}.
		 * @param error     the error/exception name
		 * @param causes    the files cases the exception (input always first)
		 * @return whether the caller shall continue, fail or cancel
		 */
		public int handle(boolean condition, String error, java.io.File... causes) {
			return this.handle(condition, error, null, causes);
		}

		/**
		 * Handle the given error/exception and return whether the caller should {@link #PROCESS_CONTINUED continue}, {@link #PROCESS_FAILED fail} or
		 * {@link #PROCESS_CANCELED cancel}.
		 *
		 * @param condition weather to handle the exception or just return {@link #PROCESS_CONTINUED continue}.
		 * @param error     the error/exception name
		 * @param cause     the throwable object to be handled
		 * @return whether the caller shall continue, fail or cancel
		 */
		public int handle(boolean condition, String error, Throwable cause) {
			return this.handle(condition, error, cause, (java.io.File[]) null);
		}

		/**
		 * Handle the given error/exception and return whether the caller should {@link #PROCESS_CONTINUED continue}, {@link #PROCESS_FAILED fail} or
		 * {@link #PROCESS_CANCELED cancel}.
		 *
		 * @param condition weather to handle the exception or just return {@link #PROCESS_CONTINUED continue}.
		 * @param error     the error/exception name
		 * @return whether the caller shall continue, fail or cancel
		 */
		public int handle(boolean condition, String error) {
			return this.handle(condition, error, null, (java.io.File[]) null);
		}

		/**
		 * Handle the given error/exception and return whether the caller should {@link #PROCESS_CONTINUED continue}, {@link #PROCESS_FAILED fail} or
		 * {@link #PROCESS_CANCELED cancel}.
		 *
		 * @param error  the error/exception name
		 * @param cause  the throwable object to be handled
		 * @param causes the files cases the exception (input always first)
		 * @return whether the caller shall continue, fail or cancel
		 */
		public int handle(String error, Throwable cause, java.io.File... causes) {
			return handle(true, error, cause, causes);
		}

		/**
		 * Handle the given error/exception and return whether the caller should {@link #PROCESS_CONTINUED continue}, {@link #PROCESS_FAILED fail} or
		 * {@link #PROCESS_CANCELED cancel}.
		 *
		 * @param error  the error/exception name
		 * @param causes the files cases the exception (input always first)
		 * @return whether the caller shall continue, fail or cancel
		 */
		public int handle(String error, java.io.File... causes) {
			return this.handle(true, error, null, causes);
		}

		/**
		 * Handle the given error/exception and return whether the caller should {@link #PROCESS_CONTINUED continue}, {@link #PROCESS_FAILED fail} or
		 * {@link #PROCESS_CANCELED cancel}.
		 *
		 * @param error the error/exception name
		 * @param cause the throwable object to be handled
		 * @return whether the caller shall continue, fail or cancel
		 */
		public int handle(String error, Throwable cause) {
			return this.handle(true, error, cause, (java.io.File[]) null);
		}

		/**
		 * Handle the given error/exception and return whether the caller should {@link #PROCESS_CONTINUED continue}, {@link #PROCESS_FAILED fail} or
		 * {@link #PROCESS_CANCELED cancel}.
		 *
		 * @param error the error/exception name
		 * @return whether the caller shall continue, fail or cancel
		 */
		public int handle(String error) {
			return this.handle(true, error, null, (java.io.File[]) null);
		}

		/**
		 * Register the given file as an input processing file.
		 *
		 * @param file to be registered
		 */
		public void in(File file) {
			this.in.add(file);
		}

		/**
		 * Register the given file as an output processing file.
		 *
		 * @param file to be registered
		 */
		public void out(File file) {
			this.out.add(file);
		}

		/**
		 * Set the size of bytes to be processed before this synchronizer get bound again.
		 *
		 * @param byteBufferSize the size of bytes to be processed before this synchronizer get bound again
		 * @param <S>            this
		 * @return this
		 */
		public <S extends Synchronizer<K, V>> S setByteBufferSize(int byteBufferSize) {
			this.byteBufferSize = byteBufferSize;
			return (S) this;
		}

		/**
		 * Set the exception handler to handle exceptions thrown to this.
		 *
		 * @param handler the exception handler to handle exceptions thrown to this
		 * @param <S>     this
		 * @return this
		 */
		public <S extends Synchronizer<K, V>> S setHandler(Function<FileException, Integer> handler) {
			this.handler = handler;
			return (S) this;
		}
	}
}
