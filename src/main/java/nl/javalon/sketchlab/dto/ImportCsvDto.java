package nl.javalon.sketchlab.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Backing DTO class for generic CSV file uploads. Provides field to explicitly set the delimiter 
 * and quote character (if applicable) used in the uploaded CSV file.
 *
 * @author Melcher Stikkelorum
 */
@Data
public class ImportCsvDto {
	@NotNull
	private MultipartFile csvFile;

	@NotNull
	@Size(min = 1, max = 1)
	private String delimiter = ",";

	@NotNull
	@Size(min = 1, max = 1)
	private String quote = "\"";
}
