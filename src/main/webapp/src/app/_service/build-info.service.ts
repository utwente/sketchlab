import {HttpClient} from "@angular/common/http";
import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {BuildInfo} from "../_dto/build-info";
import {AppConfig} from "../app.config";

@Injectable()
export class BuildInfoService {

	constructor(private http: HttpClient, private config: AppConfig) {
	}

	public get(): Observable<BuildInfo> {
		return this.http.get<BuildInfo>(`${this.config.apiUrl}/build`);
	}

}
