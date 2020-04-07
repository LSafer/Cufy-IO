/*
 *	Copyright 2020 Cufyorg
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package cufy.loadable;

import cufy.concurrent.Instructor;
import cufy.text.Format;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * An object that can be loaded from a container. And that container provides an unformatted data that requires that loadable to format it.
 *
 * @author LSaferSE
 * @version 3 release (16-Feb-2020)
 * @since 13-Feb-2020
 */
public interface FormatLoadable extends Loadable {
	@Override
	default void load() throws IOException {
		try (Reader reader = this.getReader()) {
			this.getFormat().parse(reader, this);
		}
	}
	@Override
	default void load(Instructor instructor) throws IOException {
		try (Reader reader = this.getReader(instructor)) {
			this.getFormat().parse(reader, this);
		}
	}

	@Override
	default void save() throws IOException {
		try (Writer writer = this.getWriter()) {
			this.getFormat().format(this, writer);
		}
	}
	@Override
	default void save(Instructor instructor) throws IOException {
		try (Writer writer = this.getWriter(instructor)) {
			this.getFormat().format(this, writer);
		}
	}

	/**
	 * Get the format used to parse/format the source of this loadable.
	 *
	 * @return the format of this loadable
	 */
	Format getFormat();
}
