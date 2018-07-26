package nl.javalon.sketchlab.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Jelle Stege
 */
@Service
public class FileService {
	/**
	 * Write the given data to the client.
	 *
	 * @param data     byte array of data.
	 * @param mimeType the mime type to include.
	 * @param response the http servlet response to write via.
	 * @throws IOException When an error occured in the input or output of generating the servlet
	 *                     response.
	 */
	public void write(
			@NonNull byte[] data, @NonNull String mimeType,
			@NonNull HttpServletResponse response) throws IOException {
		response.setContentType(mimeType);
		response.getOutputStream().write(data);
	}
}
