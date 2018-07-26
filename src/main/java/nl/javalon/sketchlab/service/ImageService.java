package nl.javalon.sketchlab.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Flip;
import net.coobird.thumbnailator.filters.ImageFilter;
import net.coobird.thumbnailator.filters.Rotation;
import net.coobird.thumbnailator.resizers.configurations.Rendering;
import net.coobird.thumbnailator.util.exif.ExifFilterUtils;
import net.coobird.thumbnailator.util.exif.ExifUtils;
import net.coobird.thumbnailator.util.exif.Orientation;
import nl.javalon.sketchlab.exception.MalformedRequestException;
import nl.javalon.sketchlab.utils.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.StreamSupport;

/**
 * For managing files in the database.
 *
 * @author Lukas Miedema
 */
@Service
public class ImageService {

	public final static int THUMBNAIL_WIDTH = 500;
	public final static int THUMBNAIL_HEIGHT = 375;
	public final static ImageFormat THUMBNAIL_TYPE = ImageFormat.JPG;

	public final static int AVATAR_WIDTH = 500;
	public final static int AVATAR_HEIGHT = 500;
	public final static ImageFormat AVATAR_TYPE = ImageFormat.JPG;

	public final static int MAX_SUBMISSION_WIDTH = 2048;
	public final static int MAX_SUBMISSION_HEIGHT = 2048;


	/**
	 * Detects the mime type. When the mime type is missing or not either PNG or JPEG, an exception
	 * is thrown.
	 *
	 * @param file The file to get the mime type for.
	 * @return the mime type as string.
	 * @throws MalformedRequestException When the mime type can not be determined as an allowed
	 *                                   image type.
	 */
	public String detectImageMime(MultipartFile file) throws MalformedRequestException {
		String receivedType = file.getContentType();

		if (receivedType == null) {
			throw new MalformedRequestException("Unacceptable mime type");
		}
		switch (receivedType) {
			case "image/png":
				return ImageFormat.PNG.getMimeType();
			case "image/jpeg":
			case "image/jpg":
				return ImageFormat.JPG.getMimeType();
			default:
				throw new MalformedRequestException("Unacceptable mime type: " + receivedType);
		}
	}

	/**
	 * Create a thumbnail image. The image must be of the given mime type. This method performs no
	 * mime type detection.
	 *
	 * @param unsafeImageBytes bytes of the image, unsanitized.
	 * @return a byte array of JPEG image data.
	 * @throws IOException When an error occurs during resizing.
	 */
	public byte[] createThumbnail(byte[] unsafeImageBytes) throws IOException {
		return this.resizeImage(
				unsafeImageBytes,
				ImageFormat.JPG,
				THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, true);
	}

	/**
	 * Create a JPEG image with a max width and height from the provided byte array.
	 *
	 * @param unsafeImageBytes bytes of the image, unsanitized.
	 * @return a byte array of JPEG image data.
	 * @throws IOException When an error occurs during resizing.
	 */
	public byte[] createAvatar(byte[] unsafeImageBytes) throws IOException {
		return this.resizeImage(
				unsafeImageBytes,
				ImageFormat.JPG,
				AVATAR_WIDTH, AVATAR_HEIGHT, true);
	}

	/**
	 * Resize the image such that either the width or the height matches the max width or height.
	 * Also removes all EXIF data and rotates/flips the image according to it's EXIF orientation.
	 * The output is specified by the outputFormat parameter.
	 *
	 * @param unsafeImageBytes  The image, as a byte array.
	 * @param outputFormat      The output format of the resize action.
	 * @param maxWidth          The maximum width of the image.
	 * @param maxHeight         The maximum height of the image.
	 * @param enlargeWhenNeeded Whether the resize action should enlarge the image when needed. If
	 *                          false, the image will not resize the image to the given maximum
	 *                          width and height.
	 * @return The resized image.
	 * @throws IOException When no readers are available, or an error occurs during converting the
	 *                     file.
	 */
	public byte[] resizeImage(
			byte[] unsafeImageBytes,
			ImageFormat outputFormat,
			int maxWidth, int maxHeight,
			boolean enlargeWhenNeeded) throws IOException {
		final ImageReader imageReader = createImageReader(unsafeImageBytes);

		final ImageReadParam readParameters = determineReadParameters(imageReader);
		final Orientation orientation = retrieveExifOrientation(imageReader);
		final BufferedImage bufferedImage = imageReaderToBufferedImage(imageReader, readParameters);
		Pair<Integer, Integer> dimensions = calculateDimensions(bufferedImage,
				maxWidth, maxHeight, enlargeWhenNeeded);
		final int newWidth = dimensions.getFirst();
		final int newHeight = dimensions.getSecond();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Thumbnails
				.of(bufferedImage)
				.useExifOrientation(true)
				.size(newWidth, newHeight)
				.addFilter(ExifFilterUtils.getFilterForOrientation(orientation))
				.imageType(BufferedImage.TYPE_INT_RGB)
				.rendering(Rendering.QUALITY)
				.outputQuality(1.0)
				.outputFormat(outputFormat.format)
				.toOutputStream(outputStream);
		return outputStream.toByteArray();
	}

	/**
	 * Creates a {@link BufferedImage} from the given {@link ImageReader} along with the given
	 * {@link ImageReadParam}s. Disposes the given reader after creating the buffered image.
	 *
	 * @param imageReader    The reader to use.
	 * @param readParameters The parameters for the reader.
	 * @return A {@link BufferedImage} instance.
	 * @throws IOException When the reader encounters a problem.
	 */
	private static BufferedImage imageReaderToBufferedImage(
			ImageReader imageReader, ImageReadParam readParameters) throws IOException {
		BufferedImage bufferedImage;
		try {
			bufferedImage = imageReader.read(0, readParameters);
		} finally {
			imageReader.dispose();
		}
		return bufferedImage;
	}

	/**
	 * Creates an {@link ImageReader} from a byte array.
	 *
	 * @param unsafeImageBytes The byte array to convert.
	 * @return An {@link ImageReader} instance.
	 * @throws IOException When a problem is encountered while reading the byte array.
	 */
	private static ImageReader createImageReader(byte[] unsafeImageBytes) throws IOException {
		try (final InputStream inputStream = new ByteArrayInputStream(unsafeImageBytes)) {
			// Try to find an image reader which can read the given image.
			final ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
			if (!readers.hasNext()) {
				throw new IllegalArgumentException("No suitable readers for this image");
			}

			// It is possible we found multiple readers, however they will all produce the same
			// result. Therefore, just pick the first one.
			final ImageReader reader = readers.next();

			reader.setInput(imageInputStream);

			return reader;
		}
	}

	/**
	 * Tries to determine the necessary read parameters for the given {@link ImageReader}.
	 * Specifically needed for images in CMYK color space.
	 *
	 * @param reader The reader to determine the parameters for.
	 * @return An {@link ImageReadParam} instance.
	 */
	private static ImageReadParam determineReadParameters(ImageReader reader) {
		// The reader needs read parameters, we start by using the default parameters
		final ImageReadParam param = reader.getDefaultReadParam();

		// Check if this image is in CMYK color space, if so, update the read parameters
		// accordingly.
		StreamSupport
				.stream(((Iterable<ImageTypeSpecifier>) () -> {
					try {
						return reader.getImageTypes(0);
					} catch (IOException e) {
						return Collections.emptyIterator();
					}
				}).spliterator(), false)
				.filter(t -> t.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_CMYK)
				.findFirst()
				.ifPresent(param::setDestinationType);
		return param;
	}

	/**
	 * Parse EXIF information for the given {@link ImageReader} and return the set orientation.
	 * When no such EXIF data is present, return the default orientation (1/TOP_LEFT)
	 *
	 * @param reader The {@link ImageReader} to use.
	 * @return The set orientation or 1/TOP_LEFT when not present.
	 * @throws IOException When the image can not be read correctly.
	 */
	private static Orientation retrieveExifOrientation(ImageReader reader) throws IOException {
		try {
			return ExifUtils.getExifOrientation(reader, 0);
		} catch (IllegalArgumentException e) {
			return Orientation.TOP_LEFT;
		}
	}

	/**
	 * Calculates new dimensions for the given {@link BufferedImage} based on the given maximum
	 * width and -height. When enlargeWhenNeeded is false, will not create dimensions larger
	 * than the already present width and height. When set to true, will also enlarge the image.
	 * Will always take the original aspectratio into account.
	 *
	 * @param in                The {@link BufferedImage} to determine new dimensions for.
	 * @param maxWidth          The new maximum width
	 * @param maxHeight         The new maximum height
	 * @param enlargeWhenNeeded True when the new dimensions may be larger than the original
	 *                          dimensions. False if the new dimensions should be smaller.
	 * @return A pair of integers, representing width and height.
	 */
	private static Pair<Integer, Integer> calculateDimensions(BufferedImage in,
															  int maxWidth, int maxHeight,
															  boolean enlargeWhenNeeded) {
		//Start with the original width and height
		int newWidth = in.getWidth();
		int newHeight = in.getHeight();
		//Only resize when either the image should be enlarged or when the image is too big.
		if (enlargeWhenNeeded || newWidth > maxWidth || newHeight > maxHeight) {
			double outAspect = 1.0 * maxWidth / maxHeight;
			double inAspect = 1.0 * newWidth / newHeight;
			if (outAspect < inAspect) {
				newHeight = (int) (maxWidth / inAspect);
				newWidth = maxWidth;
			} else {
				newHeight = maxHeight;
				newWidth = (int) (maxHeight * inAspect);
			}
		}
		return new Pair<>(newWidth, newHeight);
	}

	/**
	 * Transforms an image based on the given {@link Transformation} value.
	 *
	 * @param image          The image to transform.
	 * @param transformation The transformation to perform.
	 * @param outputFormat   The output format for the transformed image.
	 * @return The transformed image.
	 * @throws IOException When the given image can not be read.
	 */
	public byte[] transformImage(byte[] image, Transformation transformation, String outputFormat)
			throws IOException {
		final ImageReader reader = createImageReader(image);
		final ImageReadParam parameters = determineReadParameters(reader);
		final BufferedImage bufferedImage = imageReaderToBufferedImage(reader, parameters);

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		Thumbnails
				.of(bufferedImage)
				.addFilter(transformation.filter)
				.scale(1.0)
				.rendering(Rendering.QUALITY)
				.outputQuality(1.0)
				.outputFormat(outputFormat)
				.toOutputStream(outputStream);

		return outputStream.toByteArray();
	}

	/**
	 * Output format required by {@link ImageIO#write(RenderedImage, String, OutputStream)}.
	 *
	 * @author Jelle Stege
	 */
	@AllArgsConstructor
	@Getter
	public enum ImageFormat {
		JPG("jpg", "image/jpeg"), PNG("png", "image/png"), GIF("gif", "image/gif");

		/**
		 * Name of the format
		 */
		private final String format;
		/**
		 * The mimetype corresponding to this format.
		 */
		private final String mimeType;

		public static ImageFormat ofMimeType(String mimeType) {
			switch (mimeType) {
				case "image/jpeg":
					return JPG;
				case "image/png":
					return PNG;
				case "image/gif":
					return GIF;
				default:
					throw new IllegalArgumentException("Can not determine image format.");
			}
		}
	}

	/**
	 * Transformation to be performed on an image.
	 *
	 * @author Jelle Stege
	 */
	@AllArgsConstructor
	@Getter
	public enum Transformation {
		ROTATE_CLOCKWISE(Rotation.RIGHT_90_DEGREES),
		ROTATE_COUNTERCLOCKWISE(Rotation.LEFT_90_DEGREES),
		FLIP_HORIZONTAL(Flip.HORIZONTAL),
		FLIP_VERTICAL(Flip.VERTICAL);

		/**
		 * The {@link ImageFilter} that belongs to the current transformation.
		 */
		private final ImageFilter filter;
	}

	@AllArgsConstructor
	@Getter
	public enum RotationState {
		ROT0(false, false, false),
		ROT0_FLIPPED(true, false, false),
		ROT90(true, false, true),
		ROT90_FLIPPED(true, true, true),
		ROT180(true, true, false),
		ROT180_FLIPPED(false, true, false),
		ROT270(false, true, true),
		ROT270_FLIPPED(false, false, true);

		private final boolean invertX;
		private final boolean invertY;
		private final boolean flipXY;

		/**
		 * Transform a rotation based on the given transformation.
		 *
		 * @param transformation The transformation to apply.
		 * @return The new state.
		 */
		@SuppressWarnings("SuspiciousNameCombination")
		public RotationState transform(Transformation transformation) {
			switch (transformation) {
				case ROTATE_CLOCKWISE:
					return ofState(
							!invertY,
							invertX,
							!flipXY
					);
				case ROTATE_COUNTERCLOCKWISE:
					return ofState(
							invertY,
							!invertX,
							!flipXY
					);
				case FLIP_HORIZONTAL:
					return ofState(!invertX, invertY, this.flipXY);
				case FLIP_VERTICAL:
					return ofState(invertX, !invertY, this.flipXY);
			}
			throw new IllegalStateException("Moved through entire state space, impossible.");
		}

		/**
		 * Returns a {@link RotationState} based on the given state.
		 *
		 * @param invertX Whether the x values are inverted.
		 * @param invertY Whether the y values are inverted.
		 * @param flipXY  Whether to flip x and y.
		 * @return The corresponding {@link RotationState}.
		 */
		public static RotationState ofState(boolean invertX, boolean invertY, boolean flipXY) {
			if (flipXY) {
				if (invertY) {
					return invertX ? ROT90_FLIPPED : ROT270;
				} else {
					return invertX ? ROT90 : ROT270_FLIPPED;
				}
			} else {
				if (invertY) {
					return invertX ? ROT180 : ROT180_FLIPPED;
				} else {
					return invertX ? ROT0_FLIPPED : ROT0;
				}
			}
		}
	}
}
