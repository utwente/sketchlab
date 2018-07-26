import {Directive, ElementRef, Input} from '@angular/core';

@Directive({
	selector: '[autofocus]'
})
export class AutoFocusDirective {
	private focus = true;

	constructor(private el: ElementRef) {
	}

	ngOnInit() {
		if (this.focus) {
			//Otherwise Angular throws error: Expression has changed after it was checked.
			window.setTimeout(() => {
				this.el.nativeElement.focus();
			});
		}
	}

	@Input() set autofocus(condition: boolean) {
		this.focus = condition !== false;
	}
}
