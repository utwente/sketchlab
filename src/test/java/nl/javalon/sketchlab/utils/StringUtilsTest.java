package nl.javalon.sketchlab.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jelle Stege
 */
public class StringUtilsTest {
	@Test
	public void testHumanize() {
		Assert.assertEquals("Lowercase", StringUtils.humanize("lowercase"));
		Assert.assertEquals("Student Id", StringUtils.humanize("studentId"));
		Assert.assertEquals("Student ID", StringUtils.humanize("studentID"));
		Assert.assertEquals("HTML", StringUtils.humanize("HTML"));
		Assert.assertEquals("HTML 5", StringUtils.humanize("HTML5"));
		Assert.assertEquals("May 10", StringUtils.humanize("may10"));
		Assert.assertEquals("XML Parser", StringUtils.humanize("XMLParser"));
		Assert.assertEquals("XML", StringUtils.humanize("XML"));
		Assert.assertEquals("1", StringUtils.humanize("1"));
		Assert.assertEquals(" HTML", StringUtils.humanize(" HTML"));
		Assert.assertEquals(" html", StringUtils.humanize(" html"));
		Assert.assertEquals("HTML ", StringUtils.humanize("HTML "));
		Assert.assertEquals("Html ", StringUtils.humanize("html "));
		Assert.assertEquals("", StringUtils.humanize(""));
		Assert.assertEquals(" ", StringUtils.humanize(" "));
	}
}
