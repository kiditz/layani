package com.overflow.libs.core;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;

public class StreamUtils {
	public static final int DEFAULT_SIZE = 8192;
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	public static final byte[] EMPTY_BYTES = new byte[0];

	public static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				throw new RuntimeException("Cannot close IO : " + e);
			}
		}
	}

	public static void copyStream(InputStream input, OutputStream output, int size) throws IOException {
		byte[] bytes = new byte[size];
		int read;
		while (((read = input.read(bytes)) != -1)) {
			output.write(bytes, 0, read);
		}
	}

	public static byte[] bitmapToByte(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	public static void copyStream(InputStream input, OutputStream output) throws IOException {
		copyStream(input, output, DEFAULT_SIZE);
	}

	public static void copyStreamToFile(InputStream input, File output) throws IOException {
        copyStream(input, new FileOutputStream(output), DEFAULT_SIZE);
    }

    public static byte[] copyStreamToBytes(InputStream input) throws IOException {
		return copyStreamToBytes(input, input.available());
	}

	public static byte[] copyStreamToBytes(InputStream input, int length) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream(Math.max(0, length));
		copyStream(input, output);
		return output.toByteArray();
	}

	/**
	 * Copy the data from an {@link InputStream} to a string using the default
	 * charset.
	 * 
	 * @param approxStringLength
	 *            Used to preallocate a possibly correct sized StringBulder to
	 *            avoid an array copy.
	 * @throws IOException
	 */
	public static String copyStreamToString(InputStream input, int approxStringLength) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringWriter w = new StringWriter(Math.max(0, approxStringLength));
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];

		int charsRead;
		while ((charsRead = reader.read(buffer)) != -1) {
			w.write(buffer, 0, charsRead);
		}

		return w.toString();
	}
	public static String copyStreamToString(InputStream input) throws IOException {
		return copyStreamToString(input, 4096);
	}

    public static String getFileNameFromIntentData(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (c != null && c.moveToFirst()) {
                    result = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                c.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
