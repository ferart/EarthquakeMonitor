package fer.mobileapps.com.earthquakemonitor.views.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import fer.mobileapps.com.earthquakemonitor.R;


/**
 * Created by Fernando on 08/06/2015.
 */
public class    ImageLoader {
    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;

    /**
     * Constructor de la clase
     *
     * @param context
     *            Contexto de la aplicación
     */
    public ImageLoader(Context context) {
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
    }

    final int stub_id = R.drawable.ic_launcher;

    /**
     * Método que dewspliega la imagen como un mapa de bits
     *
     * @param url
     * @param imageView
     */
    public void DisplayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null)
            imageView.setImageBitmap(bitmap);
        else {
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }
    }

    /**
     * Método que dewspliega la imagen como un mapa de bits
     * @param imageView
     */
    public void DisplayImage(int res, ImageView imageView) {
        imageView.setImageResource(res);
    }

    /**
     * Método que encola la foto en un objeto PhotoLoader
     *
     * @param url
     * @param imageView
     */
    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    /**
     * Método que obtiene el mapa de bits de la imagen
     *
     * @param url
     * @return
     */
    private Bitmap getBitmap(String url) {
        File f = fileCache.getFile(url);

        // del cache de la SD
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;

        // de la web
        try {
            Bitmap bitmap = null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            copyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Método que decodifica la imagen y la ajusta para reducir el consumo de
     * memoria
     *
     * @param f
     * @return
     */
    private Bitmap decodeFile(File f) {
        try {
            // decodifica la imagen
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // se encuentra el mejor valor de escala, debe ser potencia de 2
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decodifica con inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * Clase encargada de implementar la cola de imágenes
     *
     * @author rcarventepc
     *
     */
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    /**
     * Clase encargada de manejar el cargador de imágenes
     *
     */
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    /**
     * Clase encargada de desplegar un mapa de bits en el hilo de la aplicación
     *
     */
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

    /**
     * Método que copia un stream de entrada a un stream de salida
     *
     * @param is
     * @param os
     */
    public static void copyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {

        }
    }
}
