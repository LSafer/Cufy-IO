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
 * A loadable that depends on an interpreter.
 *
 * @param <D> get the decoder of this loadable
 * @author LSaferSE
 * @version 1 release (16-Feb-2020)
 * @since 16-Feb-2020
 */
public interface InterpreterLoadable<D> extends Loadable {
	/**
	 * Get the interpreter of this loadable.
	 *
	 * @return the decoder of this loadable
	 */
	D getInterpreter();
	/**
	 * Set the interpreter of this loadable.
	 *
	 * @param decoder the new interpreter to be set
	 */
	default void setInterpreter(D decoder) {
		throw new UnsupportedOperationException("setDecoder(D decoder)");
	}
}
