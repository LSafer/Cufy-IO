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
 * A loadable that depends on a codec to encode and decode data.
 *
 * @param <C> the Codec of this loadable
 * @author LSaferSE
 * @version 1 release (16-Feb-2020)
 * @since 16-Feb-2020
 */
public interface CodecImplLoadable<C> extends Loadable {
	/**
	 * Get the codec of this loadable.
	 *
	 * @return the codec of this loadable
	 */
	C getCodec();
	/**
	 * Set the codec of this loadable.
	 *
	 * @param codec the new codec to be set
	 */
	default void setCodec(C codec) {
		throw new UnsupportedOperationException("setCodec(C codec))");
	}
}
