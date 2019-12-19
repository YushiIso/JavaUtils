package com.github.yi.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author YushiIso
 *
 */
public class GZIPCompressor {

	public static InputStream deCompress(final InputStream in) throws IOException {
		return new GZIPInputStream(in);
	}

	public static OutputStream commpress(final OutputStream out) throws IOException {
		return new GZIPOutputStream(out);
	}

}
