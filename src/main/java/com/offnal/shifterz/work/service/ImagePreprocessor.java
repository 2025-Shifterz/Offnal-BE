package com.offnal.shifterz.work.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

public final class ImagePreprocessor {

	private ImagePreprocessor() {}

	public static ProcessedImage preprocess(byte[] originalBytes, String targetFormat, int maxSizePx) {
		try (var in = new ByteArrayInputStream(originalBytes)) {
			BufferedImage src = ImageIO.read(in);
			if (src == null) throw new IllegalArgumentException("Unsupported image or invalid bytes");

			int w = src.getWidth();
			int h = src.getHeight();

			double scale = Math.min(1.0, (double) maxSizePx / Math.max(w, h));
			int nw = (int) Math.round(w * scale);
			int nh = (int) Math.round(h * scale);

			boolean toJpg = "jpg".equalsIgnoreCase(targetFormat) || "jpeg".equalsIgnoreCase(targetFormat);

			BufferedImage resized = new BufferedImage(
				nw, nh,
				toJpg ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB
			);

			Graphics2D g = resized.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawImage(src, 0, 0, nw, nh, null);
			g.dispose();

			try (var out = new ByteArrayOutputStream()) {
				String fmt = toJpg ? "jpg" : "png";
				ImageIO.write(resized, fmt, out);

				byte[] processed = out.toByteArray();
				String base64 = Base64.getEncoder().encodeToString(processed);

				return new ProcessedImage(processed, fmt, base64);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to preprocess image", e);
		}
	}

	public record ProcessedImage(byte[] bytes, String format, String base64) {}
}
