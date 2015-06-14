package fer.mobileapps.com.earthquakemonitor.views.utils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase encargada de obtener la imágen de la memoria cache del dispositivo
 *
 */
public class MemoryCache {
	private Map<String, SoftReference<Bitmap>> cache = Collections
			.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());

	/**
	 * Método encargado de obtener el mapa de bits
	 * 
	 * @param id
	 * @return
	 */
	public Bitmap get(String id) {
		if (!cache.containsKey(id))
			return null;
		SoftReference<Bitmap> ref = cache.get(id);
		return ref.get();
	}

	/**
	 * Método encargado de asignar el mapa de bits de la imagen
	 * 
	 * @param id
	 * @param bitmap
	 */
	public void put(String id, Bitmap bitmap) {
		cache.put(id, new SoftReference<Bitmap>(bitmap));
	}

	/**
	 * Método encargado de limpiar la memoria
	 */
	public void clear() {
		cache.clear();
	}
}
