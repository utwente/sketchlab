package nl.javalon.sketchlab.service;

import static nl.javalon.sketchlab.service.ImageService.RotationState.*;
import static nl.javalon.sketchlab.service.ImageService.Transformation;

import nl.javalon.sketchlab.service.ImageService.RotationState;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jelle Stege
 */
public class ImageServiceRotationStateTest {
	@Test
	public void testRotateClockwise() {
		RotationState r = ROT0;
		r = r.transform(Transformation.ROTATE_CLOCKWISE);
		Assert.assertEquals(ROT90, r);
		r = r.transform(Transformation.ROTATE_CLOCKWISE);
		Assert.assertEquals(ROT180, r);
		r = r.transform(Transformation.ROTATE_CLOCKWISE);
		Assert.assertEquals(ROT270, r);
		r = r.transform(Transformation.ROTATE_CLOCKWISE);
		Assert.assertEquals(ROT0, r);
		r = ROT0_FLIPPED;
		r = r.transform(Transformation.ROTATE_CLOCKWISE);
		Assert.assertEquals(ROT90_FLIPPED, r);
		r = r.transform(Transformation.ROTATE_CLOCKWISE);
		Assert.assertEquals(ROT180_FLIPPED, r);
		r = r.transform(Transformation.ROTATE_CLOCKWISE);
		Assert.assertEquals(ROT270_FLIPPED, r);
		r = r.transform(Transformation.ROTATE_CLOCKWISE);
		Assert.assertEquals(ROT0_FLIPPED, r);
	}

	@Test
	public void testRotateCounterClockwise() {
		RotationState r = ROT0;
		r = r.transform(Transformation.ROTATE_COUNTERCLOCKWISE);
		Assert.assertEquals(ROT270, r);
		r = r.transform(Transformation.ROTATE_COUNTERCLOCKWISE);
		Assert.assertEquals(ROT180, r);
		r = r.transform(Transformation.ROTATE_COUNTERCLOCKWISE);
		Assert.assertEquals(ROT90, r);
		r = r.transform(Transformation.ROTATE_COUNTERCLOCKWISE);
		Assert.assertEquals(ROT0, r);
		r = ROT0_FLIPPED;
		r = r.transform(Transformation.ROTATE_COUNTERCLOCKWISE);
		Assert.assertEquals(ROT270_FLIPPED, r);
		r = r.transform(Transformation.ROTATE_COUNTERCLOCKWISE);
		Assert.assertEquals(ROT180_FLIPPED, r);
		r = r.transform(Transformation.ROTATE_COUNTERCLOCKWISE);
		Assert.assertEquals(ROT90_FLIPPED, r);
		r = r.transform(Transformation.ROTATE_COUNTERCLOCKWISE);
		Assert.assertEquals(ROT0_FLIPPED, r);
	}

	@Test
	public void testFlipHorizontal() {
		RotationState r = ROT0;
		r = r.transform(Transformation.FLIP_HORIZONTAL);
		Assert.assertEquals(ROT0_FLIPPED, r);
		r = r.transform(Transformation.FLIP_HORIZONTAL);
		Assert.assertEquals(ROT0, r);
		r = ROT180;
		r = r.transform(Transformation.FLIP_HORIZONTAL);
		Assert.assertEquals(ROT180_FLIPPED, r);
		r = r.transform(Transformation.FLIP_HORIZONTAL);
		Assert.assertEquals(ROT180, r);
		r = ROT90;
		r = r.transform(Transformation.FLIP_HORIZONTAL);
		Assert.assertEquals(ROT270_FLIPPED, r);
		r = r.transform(Transformation.FLIP_HORIZONTAL);
		Assert.assertEquals(ROT90, r);
		r = ROT270;
		r = r.transform(Transformation.FLIP_HORIZONTAL);
		Assert.assertEquals(ROT90_FLIPPED, r);
		r = r.transform(Transformation.FLIP_HORIZONTAL);
		Assert.assertEquals(ROT270, r);
	}

	@Test
	public void testFlipVertical() {
		RotationState r = ROT0;
		r = r.transform(Transformation.FLIP_VERTICAL);
		Assert.assertEquals(ROT180_FLIPPED, r);
		r = r.transform(Transformation.FLIP_VERTICAL);
		Assert.assertEquals(ROT0, r);
		r = ROT0_FLIPPED;
		r = r.transform(Transformation.FLIP_VERTICAL);
		Assert.assertEquals(ROT180, r);
		r = r.transform(Transformation.FLIP_VERTICAL);
		Assert.assertEquals(ROT0_FLIPPED, r);
		r = ROT90;
		r = r.transform(Transformation.FLIP_VERTICAL);
		Assert.assertEquals(ROT90_FLIPPED, r);
		r = r.transform(Transformation.FLIP_VERTICAL);
		Assert.assertEquals(ROT90, r);
		r = ROT270;
		r = r.transform(Transformation.FLIP_VERTICAL);
		Assert.assertEquals(ROT270_FLIPPED, r);
		r = r.transform(Transformation.FLIP_VERTICAL);
		Assert.assertEquals(ROT270, r);
	}
}
