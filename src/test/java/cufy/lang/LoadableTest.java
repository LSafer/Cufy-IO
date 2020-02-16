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

import cufy.beans.Bean;
import cufy.text.Format;
import org.cufy.io.FileLoadable;
import org.cufy.io.URLLoadable;
import org.cufy.lang.JSONConverter;
import org.cufy.text.FormatLoadable;
import org.cufy.text.JSON;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("JavaDoc")
public class LoadableTest {
	@Test
	public void file_format_bean_json_load_save() throws IOException {
		File temp = new File("/Aspacex/file-format-loadable-bean-test-temp");
		temp.delete();

		class TestBean implements Bean, FileLoadable, FormatLoadable {
			@Property(key = @Value(value = "list", converter = JSONConverter.class))
			final public List<String> list = new ArrayList<>();
			@Override
			public Format getInterpreter() {
				return JSON.global;
			}
			@Override
			public File getSource() {
				return temp;
			}
		}

		{
			TestBean bean = new TestBean();

			bean.list.add("Success");

			while (true)
				try {
					bean.save();
					break;
				} catch (IOException e) {
					if (temp.isDirectory()) {
						temp.delete();
					} else if (!temp.exists()) {
						temp.createNewFile();
					}
				}
		}
		{
			TestBean bean = new TestBean();

			bean.load();

			Assert.assertEquals("Wrong value stored", "Success", bean.list.get(0));
		}

		temp.delete();
	}

	@Test
	public void url_format_bean_json_load() throws IOException {
		URL target = new URL("https://api.fixer.io/");

		try {
			target.openStream();
		} catch (UnknownHostException e) {
			//Possible: no internet
			System.err.println("TEST failed: " + e.getMessage());
			return;
		}

		class TestBean implements FormatLoadable, URLLoadable, Bean {
			@Property(key = @Value("1"))
			public String one;
			@Property(key = @Value("7"))
			public String seven;
			@Property(key = @Value("0"))
			public String zero;

			@Override
			public Format getInterpreter() {
				return JSON.global;
			}
			@Override
			public URL getSource() {
				return target;
			}

			@Override
			public Object put(Object key, Object value) {
				//Allow unhandled keys :)
				VirtualEntry entry = this.getEntry(key);
				return entry == null ? null : entry.setValue(value);
			}
		}

		TestBean bean = new TestBean();

		bean.load();

		Assert.assertNotNull("Property missing, Or website changed it's content", bean.zero);
		Assert.assertNotNull("Property missing, Or website changed it's content", bean.one);
		Assert.assertNotNull("Property missing, Or website changed it's content", bean.seven);
	}
}
