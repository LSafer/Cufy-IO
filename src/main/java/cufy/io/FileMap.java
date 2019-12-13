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

import org.cufy.annotation.Beta;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A {@link Map} that is linked to {@link File} as it's IO-Container.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 14 release (02-Nov-2019)
 * @since 11 Jun 2019
 */
@Beta(value = "ideas flow")
public interface FileMap<K, V> extends Map<K, V> {
	/**
	 * Load this from the linked {@link File}. Using the given {@link cufy.io.File.Synchronizer}.
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @param <F>          this
	 * @return this
	 */
	default <F extends FileMap<K, V>> F load(File.Synchronizer<?, ?> synchronizer) {
		return this.load(synchronizer, null, null);
	}

	/**
	 * Load this from the linked {@link File}. Using the given {@link cufy.io.File.Synchronizer}. Then accept the given 'removeAction' foreach key
	 * have been removed. Then accept the given 'addAction' foreach key have been added.
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @param removeAction the action to do with the removed keys
	 * @param addAction    the action to do with the added keys
	 * @param <F>          this
	 * @return this
	 */
	default <F extends FileMap<K, V>> F load(File.Synchronizer<?, ?> synchronizer, BiConsumer<K, V> removeAction, BiConsumer<K, V> addAction) {
		Map<K, V> map = this.read(synchronizer);
		if (map == null)
			return (F) this;

		Set<K> added = map.keySet(), removed = new HashSet<>();

		this.keySet().forEach(k -> {
			if (added.contains(k)) {
				this.put(k, map.get(k));
				added.remove(k);
			} else {
				removed.add(k);
			}
		});

		added.forEach(key -> {
			V value = map.get(key);
			this.put(key, value);
			if (addAction != null)
				addAction.accept(key, value);
		});
		removed.forEach(key -> {
			V value = this.remove(key);
			if (removeAction != null)
				removeAction.accept(key, value);
		});
		return (F) this;
	}

	/**
	 * Move the targeted file to the given output file.
	 *
	 * @param output       the destination to move the targeted file to
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	default void move(File.Synchronizer<?, ?> synchronizer, java.io.File output) {
		this.getFile().move(synchronizer, output);
		this.setFile(output);
	}

	/**
	 * Rename the linked {@link File} to the given name.
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @param name         to rename to
	 */
	default void rename(File.Synchronizer<?, ?> synchronizer, String name) {
		File output = this.getFile().sibling(name);
		this.getFile().move(synchronizer, output);
		this.setFile(output);
	}

	/**
	 * Save the content of this to the targeted file.
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 */
	default void save(File.Synchronizer<?, ?> synchronizer) {
		this.write(synchronizer, this);
	}

	/**
	 * Set the targeted file to the give file. (the instance will not be used unless it extends 'lsafer.io.File').
	 *
	 * @param file the file to be set
	 * @return the previous file set
	 */
	default File setFile(java.io.File file) {
		return this.setFile(file instanceof File ? (File) file : new File(file));
	}

	/**
	 * Set the targeted file from the output of the given function. The function will be applied with the current targeted file.
	 *
	 * @param function to get the new file from
	 * @param <F>      this
	 * @return this
	 */
	default <F extends FileMap<K, V>> F setFile(Function<File, File> function) {
		this.setFile(function.apply(this.getFile()));
		return (F) this;
	}

	/**
	 * Set the targeted file to the given pathname.
	 *
	 * @param file the pathname of the file
	 * @return the previous file set
	 */
	default File setFile(String file) {
		return this.setFile(new File(file));
	}

	/**
	 * Get the current targeted file.
	 *
	 * @return the current targeted file
	 */
	File getFile();

	/**
	 * Read the contents of the targeted file as a map then return it.
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @return a map of contents of this file
	 */
	Map<K, V> read(File.Synchronizer<?, ?> synchronizer);

	/**
	 * Set the targeted file to the given file.
	 *
	 * @param file the file to be set
	 * @return the previous file set
	 */
	File setFile(File file);

	/**
	 * Save the given map to the targeted file.
	 *
	 * @param synchronizer used for: a-creating long loops b-pass information c-report exceptions
	 * @param map          to be written
	 */
	void write(File.Synchronizer<?, ?> synchronizer, Map<K, V> map);
}
