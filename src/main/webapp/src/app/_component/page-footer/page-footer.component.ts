import {Component, OnInit} from '@angular/core';
import {BuildInfoService} from "../../_service/build-info.service";

@Component({
	selector: 'page-footer',
	templateUrl: './page-footer.component.html',
	styleUrls: ['./page-footer.component.scss']
})
export class PageFooterComponent implements OnInit {

	buildString: string;
	currentYear: number = new Date().getFullYear();

	constructor(private buildInfoService: BuildInfoService) {
		buildInfoService.get().subscribe(info => {
			this.buildString = info.buildString
		});
	}

	ngOnInit() {
	}

}
