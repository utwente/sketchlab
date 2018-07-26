import {environment} from "../environments/environment";

export class AppConfig {
	public hostUrl = environment.hostUrl ? environment.hostUrl : window.location.origin;
	public apiUrl = environment.apiUrl ? environment.apiUrl : this.hostUrl + '/api/v1';
}