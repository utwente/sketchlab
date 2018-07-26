package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.NotificationDetailsDao;
import nl.javalon.sketchlab.dto.notification.NotificationDto;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lukas Miedema
 */
@SketchlabResource
@RequestMapping(ApiConfig.NOTIFICATION)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
@Api(description = "Returns an ordered list of all objects for which the user has received events.")
public class NotificationResource {

	private final NotificationDetailsDao notificationDetailsDao;

	@ApiOperation("Retrieve all notifications for the current user")
	@GetMapping
	public List<NotificationDto> getAll(@AuthenticationPrincipal User user) {
		return notificationDetailsDao.getNotificationsForUser(user.getId());
	}

	@ApiOperation("Delete a particular notification")
	@DeleteMapping("/{notificationId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@AuthenticationPrincipal User user, @PathVariable int notificationId) {
		notificationDetailsDao.deleteUserAndById(user.getId(), notificationId);
	}
}
