import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';

@Component({
	selector: 'thumbnailed-image',
	templateUrl: './thumbnailed-image.component.html',
	styleUrls: ['./thumbnailed-image.component.scss']
})
export class ThumbnailedImageComponent implements OnInit, AfterViewInit {

	@Input() thumbnailSrc: string;
	@Input() enlargable: boolean = true;
	@Input() src: string;
	@Input() alt: string = '';

	@ViewChild('cover', {read: ElementRef}) cover: ElementRef;

	enlarged: boolean = false;

	constructor() {
	}

	ngOnInit() {
	}

	click() {
		if (this.enlargable) {
			this.enlarged = true;
		}
		this.enlarged = true;
	}

	closeEnlargement() {
		this.enlarged = false;
	}

	ngAfterViewInit(): void {

	}
}
