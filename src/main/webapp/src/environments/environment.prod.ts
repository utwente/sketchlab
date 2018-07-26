/**
 * Settings for production environment.
 *
 * When attempting to run this with "ng serve", include the
 * <code>--proxy-config proxy.conf.json</code> flag to ensure the API is reachable at /api/v1.
 *
 * @type {{production: boolean; hostUrl: string; apiUrl: string}}
 */
export const environment = {
	production: true,
	hostUrl: '',
	apiUrl: '/api/v1'
};
