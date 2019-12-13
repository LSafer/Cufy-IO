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
import cufy.text.Format;

import java.util.Map;

/**
 * A map linked to a file is it's original source. And use {@link Format} as a way to translate that source.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 2 release (02-Nov-2019)
 * @since 30-Oct-2019
 */
@Beta("idea flow")
public interface FormatFileMap<K, V> extends FileMap<K, V> {
	@Override
	default Map<K, V> read(File.Synchronizer<?, ?> synchronizer) {
		return this.getFile().read(synchronizer, this.formatter(), Map.class);
	}

	@Override
	default void write(File.Synchronizer<?, ?> synchronizer, Map<K, V> map) {
		this.getFile().write(synchronizer, this.formatter(), map);
	}

	/**
	 * Get the parser to be used by this.
	 *
	 * @return the parser of this
	 */
	Format formatter();
}
