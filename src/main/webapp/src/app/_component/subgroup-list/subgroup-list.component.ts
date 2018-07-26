import {Component, Input, OnInit} from '@angular/core';
import {ChapterWithChapterGroupsAndSubgroups} from '../../_dto/chapter';

@Component({
	selector: 'subgroup-list',
	templateUrl: './subgroup-list.component.html',
	styleUrls: ['./subgroup-list.component.scss']
})
export class SubgroupListComponent implements OnInit {

	@Input() subgroups: ChapterWithChapterGroupsAndSubgroups[];

	constructor() {
	}

	ngOnInit() {
	}

}
