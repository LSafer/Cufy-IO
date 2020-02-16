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
package org.cufy.lang;

import cufy.lang.Loadable;

/**
 * A loadable that depends on a path to locate where the data is stored.
 *
 * @param <P> the type of the path of the loadable
 * @author LSaferSE
 * @version 1 release (16-Feb-2020)
 * @since 16-Feb-2020
 */
public interface PathImplLoadable<P> extends Loadable {
	/**
	 * Get the path of this loadable.
	 *
	 * @return the path of this loadable.
	 */
	P getPath();
	/**
	 * Set the path of this loadable to a new path.
	 *
	 * @param path the new path to be set
	 */
	default void setPath(P path) {
		throw new UnsupportedOperationException("setPath(P path)");
	}
}
