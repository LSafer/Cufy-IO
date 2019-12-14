package cufy.io;

import java.io.Serializable;
import java.util.Map;

/**
 * A map linked to a file is it's original source. And use {@link java.io.Serializable java serialization} as a way to translate that source.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author LSaferSE
 * @version 1 release (03-Nov-2019)
 * @since 03-Oct-2019
 */
@Deprecated
public interface SerialFileMap<K, V> extends FileMap<K, V>, Serializable {
	@Override
	default Map<K, V> read(File.Synchronizer<?, ?> synchronizer) {
		Serializable serializable = this.getFile().readSerial(synchronizer, Serializable.class);
		return serializable instanceof Map ? (Map<K, V>) serializable : null;
	}

	@Override
	default void write(File.Synchronizer<?, ?> synchronizer, Map<K, V> map) {
		this.getFile().writeSerial(synchronizer, this);
	}
}
