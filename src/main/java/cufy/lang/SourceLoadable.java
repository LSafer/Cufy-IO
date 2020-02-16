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
package cufy.lang;

/**
 * A loadable that depends on a source.
 *
 * @param <P> the type of the path of the loadable
 * @author LSaferSE
 * @version 1 release (16-Feb-2020)
 * @since 16-Feb-2020
 */
public interface SourceLoadable<P> extends Loadable {
	/**
	 * Get the source of this loadable.
	 *
	 * @return the source of this loadable.
	 */
	P getSource();
	/**
	 * Set the source of this loadable to a new source.
	 *
	 * @param path the new source to be set
	 */
	default void setSource(P path) {
		throw new UnsupportedOperationException("setPath(P path)");
	}
}
