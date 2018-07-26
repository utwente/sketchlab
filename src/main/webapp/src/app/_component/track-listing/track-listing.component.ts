import {Component, EventEmitter, Input, OnChanges, Output} from '@angular/core';
import {Chapter, ChapterGroup, ChapterGroupDetailsDto, ChapterGroupRole} from "../../_dto/chapter";
import {Task, TaskDetailsDto, Track} from "../../_dto/task";

@Component({
	selector: 'track-listing',
	templateUrl: './track-listing.component.html',
	styleUrls: ['./track-listing.component.scss']
})
export class TrackListingComponent implements OnChanges {

	// To get around angular bullshit
	@Input() updateCounter: number = 0;

	// The chapter (group) to display (the group attribute is optional)
	@Input() chapterGroup: ChapterGroupDetailsDto = null;

	// Optional -> enables task editing
	@Input() taskEditCallback: TaskEditListener = null;

	@Output() renameClick = new EventEmitter<void>();
	@Output() taskClick = new EventEmitter<Task>();

	// Processed chapters for the template
	chapterInRows: ChapterRows = null;

	ngOnChanges(): void {
		// Convert the chapter into a set of rows
		const trackMap = new Map(Object.entries(this.chapterGroup.tracks)) as Map<Track, TaskDetailsDto[]>;
		let empty = !this.taskEditCallback; // not 'empty' if there's an task callback due to the edit row
		trackMap.forEach((value) => empty = value.length == 0 && empty);

		this.chapterInRows = new ChapterRows(this.chapterGroup.chapter,
			this.chapterGroup.chapterGroup,	this.chapterGroup.role, empty,
			this.getRows(trackMap, this.chapterGroup));
	}

	protected getRows(tracks: Map<Track, TaskDetailsDto[]>, chapter: ChapterGroupDetailsDto): TrackRow[] {
		const rows: TrackRow[] = [];
		tracks.forEach((tasks, track) => {
			for (const task of tasks) {

				// Make sure the array is long enough
				while (rows.length <= task.slot) {
					rows.push(new TrackRow());
				}

				// Make sure it has a chapter id
				const baseTask = {chapterId: chapter.chapter.id, authorId: null};
				rows[task.slot][track].task = Object.assign(baseTask, task);
			}
		});

		// Only color progression if the viewer is a student, otherwise color them all
		TrackListingComponent.colorRows(rows, chapter.role === ChapterGroupRole.STUDENT);

		// If we're in task edit mode, add a whole extra row to be able to add things there
		if (this.taskEditCallback) {
			rows.push(new TrackRow());
		}

		return rows;
	}

	/**
	 * Colors the rows.
	 * @param {TrackRow[]} rows
	 * @param colorProgression
	 * @boolean colorProgression to only use track color until the 'submitted' flag is false.
	 */
	private static colorRows(rows: TrackRow[], colorProgression: boolean) {
		/*
		 * Welcome to edge case drama
		 * here the colors of the track segments need to be decided
		 * The rules are
		 *  - If a task is the first task, the segments above it are uncolored. It by itself receives topColor = NONE
		 *  - If the first task is submitted, all the following segments are "active"
		 *  - If a task is not submitted, all following segments are "inactive"
		 *  - If a task is the last task, all following segments are "none"
		 */
		for (const trackName of ["BASICS", "IDEATION", "COMMUNICATION", "FORM"]) {
			let lastTaskIndex = -1;
			let currColorMode = ColorMode.NONE;
			let index = 0;

			// From the top
			for (const row of rows) {
				const track: TrackCell = row[trackName];
				track.color.topColor = currColorMode;

				if (track.task) {
					if (lastTaskIndex === -1) {
						currColorMode = ColorMode.ACTIVE;
					}
					const taskColored = track.task.submitted || !colorProgression;
					track.color.taskColor = taskColored ? ColorMode.ACTIVE : ColorMode.INACTIVE;

					currColorMode = taskColored ? currColorMode : ColorMode.INACTIVE;
					lastTaskIndex = index;
				}

				track.color.bottomColor = currColorMode;
				index++;
			}

			// Now remove the colors from the bottom
			if (lastTaskIndex != -1) {
				rows[lastTaskIndex][trackName].color.bottomColor = ColorMode.NONE;
				for (let i = lastTaskIndex + 1; i < index; i++) {
					rows[i][trackName].color.topColor = ColorMode.NONE;
					rows[i][trackName].color.bottomColor = ColorMode.NONE;
				}
			}
		}
	}

	/**
	 * Returns a closure that can check if any given task is allowed to be placed here. It checks if the chapter id
	 * matches and if there is no existing task. Called by the dnd library.
	 * @param existingTask
	 * @returns {(task:Task)=>boolean}
	 */
	public canDropTask(existingTask: Task): (Task) => boolean {
		return (task: Task) => {
			if (existingTask) {
				return false; // cannot replace an existing task
			}
			return task.chapterId == this.chapterGroup.chapter.id;
		};
	}

	/**
	 * Delegates to the taskEditCallback callback. Called by the dnd library.
	 * @param event
	 * @param index row number
	 * @param trackName in lower case.
	 */
	public onDropTask(event: any, index: number, trackName: string): void {
		let task: Task = event.dragData;
		this.taskEditCallback.onMoveTask(task, index, trackName.toUpperCase());
	}

	/**
	 * Generates the css classes for a color configuration. Used by the template.
	 * @param {Color} color
	 * @returns {string}
	 */
	public getCssClassForColor(color: Color): string {
		const topColor = ColorMode[color.topColor].toLowerCase();
		const bottomColor = ColorMode[color.bottomColor].toLowerCase();
		return `color-top-${topColor} color-bottom-${bottomColor}`;
	}
	/**
	 * Returns true if the role is 'special' the current user has in the given item, which is when the user is
	 * not a student.
	 * @param {ChapterRows} chapter
	 * @returns {boolean}
	 */
	public hasSpecialRole(role: ChapterGroupRole): boolean {
		return ChapterGroupRole.STUDENT != role;
	}

	public clickChapterGroupName($event: MouseEvent) {
		$event.preventDefault();
		if (this.chapterInRows.role == ChapterGroupRole.TEACHER) {
			this.renameClick.emit();
		}
	}
}

export interface TaskEditListener {

	/**
	 * Invoked when the user clicks the plus to add a task.
	 * @param newIndex
	 * @param newTrack name of the track (upper case)
	 */
	onAddTask(newIndex: number, newTrack: string): void;

	/**
	 * Invoked when the user moves a task.
	 * @param task
	 * @param newIndex
	 * @param newTrack name of the track (upper case)
	 */
	onMoveTask(task: Task, newIndex: number, newTrack: string): void;

	/**
	 * Invoked when the user clicks edit on a task.
	 * @param task
	 */
	onEditTask(task: Task): void;

	/**
	 * Invoked when the user clicks delete on a task.
	 * @param task
	 */
	onDeleteTask(task: Task): void;
}

class ChapterRows {
	constructor(
		public readonly chapter: Chapter,
		public readonly chapterGroup: ChapterGroup,
		public readonly role: ChapterGroupRole,
		public readonly empty: boolean,
		public readonly rows: TrackRow[]) {
	}

	public getLabel(): string {
		let title = this.chapter.label;
		if (this.chapterGroup)
			title += " / " + this.chapterGroup.name;
		return title;
	}
}

class TrackRow {
	BASICS: TrackCell;
	IDEATION: TrackCell;
	FORM: TrackCell;
	COMMUNICATION: TrackCell;

	constructor() {
		this.BASICS = new TrackCell(null);
		this.IDEATION = new TrackCell(null);
		this.FORM = new TrackCell(null);
		this.COMMUNICATION = new TrackCell(null);
	}
}

class TrackCell {
	public color: Color;

	constructor(public task: Task & {submitted: boolean}) {
		this.color = new Color(ColorMode.NONE, ColorMode.NONE, ColorMode.NONE);
	}
}

/**
 * Indicates how to color a cell in a row, which displays how many assignments are done.
 */
class Color {
	constructor(public topColor: ColorMode, public taskColor: ColorMode, public bottomColor: ColorMode) {}

	public isTaskActive() {
		return this.taskColor === ColorMode.ACTIVE;
	}
}

enum ColorMode {
	/**
	 * This segment is uncolored (it's white)
	 */
	NONE,

	/**
	 * This segment should receive the track color
	 */
	ACTIVE,

	/**
	 * This segment should receive the inactive gray color.
	 */
	INACTIVE
}
