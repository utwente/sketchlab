package nl.javalon.sketchlab.resource;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.entity.tables.daos.UserAvatarDao;
import nl.javalon.sketchlab.entity.tables.pojos.UserAvatar;
import nl.javalon.sketchlab.exception.MethodNotAllowedException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.security.SecurityService;
import nl.javalon.sketchlab.service.FileService;
import nl.javalon.sketchlab.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Resource dealing with user's avatars.
 *
 * @author Lukas Miedema.
 */
@SketchlabResource
@RequestMapping(ApiConfig.USER_AVATAR)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserAvatarResource {

	private final UserAvatarDao userAvatarDao;
	private final ImageService imageService;
	private final FileService fileService;

	/**
	 * Retrieves an avatar for the given user ID.
	 *
	 * @param userId   The ID of the user.
	 * @param response The HTTP response to use.
	 * @throws IOException When the given HTTP response can not be written to.
	 */
	@ApiOperation("Retrieves a user's avatar.")
	@GetMapping
	public void get(@PathVariable UUID userId, HttpServletResponse response) throws IOException {
		UserAvatar avatar = NoSuchEntityException.checkNull(
				userAvatarDao.findById(userId),
				"No such user or avatar");
		fileService.write(avatar.getImage(), ImageService.AVATAR_TYPE.getMimeType(), response);
	}

	/**
	 * Sets a new avatar for the given user.
	 *
	 * @param userId The ID of the user.
	 * @param file   The avatar to set.
	 * @throws IOException When the given HTTP response can not be written to.
	 */
	@ApiOperation("Sets a new avatar for the given user.")
	@PostMapping
	public void post(@PathVariable UUID userId, @RequestBody MultipartFile file)
			throws IOException {
		if (userId.equals(SecurityService.ANONYMOUS_USER_ID)) {
			throw new MethodNotAllowedException("Cannot set user avatar for the anonymous user");
		}

		imageService.detectImageMime(file);
		byte[] image = imageService.createAvatar(file.getBytes());
		UserAvatar avatar = new UserAvatar(userId, image);
		if (userAvatarDao.existsById(userId)) {
			userAvatarDao.update(avatar);
		} else {
			userAvatarDao.insert(avatar);
		}
	}

	/**
	 * Alters a user's avatar by flipping or rotating it.
	 *
	 * @param userId         The ID of the user
	 * @param transformation The transformation to apply on the avatar.
	 * @throws IOException When the transformation could not be applied.
	 */
	@ApiOperation(value = "Alter the user's avatar by rotating or flipping it.",
			notes = "This allows images to be displayed correctly.")
	@PutMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void rotate(
			@PathVariable UUID userId, @RequestParam ImageService.Transformation transformation)
			throws IOException {
		UserAvatar avatar = NoSuchEntityException.checkNull(
				userAvatarDao.fetchOneByUserId(userId),
				"User has no avatar");

		byte[] transformedImage = imageService.transformImage(
				avatar.getImage(),
				transformation,
				ImageService.AVATAR_TYPE.getFormat()
		);
		avatar.setImage(transformedImage);
		userAvatarDao.update(avatar);
	}

	/**
	 * Deletes a user's avatar
	 *
	 * @param userId The user for which to delete the avatar.
	 */
	@ApiOperation(value = "Deletes a user's avatar",
			notes = "Only available for owner and teacher.")
	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID userId) {
		UserAvatar avatar = NoSuchEntityException.checkNull(
				userAvatarDao.fetchOneByUserId(userId),
				"User has no avatar"
		);
		this.userAvatarDao.delete(avatar);
	}
}
