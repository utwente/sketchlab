import {
	Component,
	ElementRef,
	Input,
	OnChanges,
	OnInit,
	SimpleChanges,
	ViewChild
} from '@angular/core';
import {SafeUrl} from '@angular/platform-browser';
import {
	AnnotationDetailsDto,
	AnnotationLineSegment,
	PencilPreferences,
	Point
} from '../../_dto/submission';

@Component({
	selector: 'image-with-overlay',
	templateUrl: './image-with-overlay.component.html',
	styleUrls: ['./image-with-overlay.component.scss']
})
export class ImageWithOverlayComponent implements OnInit, OnChanges {
	/**
	 * The submission image, as a data URL.
	 */
	@Input() submissionDataUrl: SafeUrl;

	/**
	 * Sets whether or not to allow creating a new annotation.
	 * @type {boolean}
	 */
	@Input() allowEditing: boolean = false;

	/**
	 * The annotation to be shown, also sets the transformation operations.
	 * @type {AnnotationDetailsDto}
	 */
	@Input() set annotation(annotation: AnnotationDetailsDto) {
		if (annotation) {
			this.setDrawing(
				JSON.parse(annotation.drawing),
				annotation.invertX,
				annotation.invertY,
				annotation.flipXy
			);
		}
	}

	/**
	 * Setter for the Canvas HTML element.
	 */
	@ViewChild('canvas') set canvas(canvasRef: ElementRef) {
		this.canvasElement = canvasRef.nativeElement;
	};

	/**
	 * The canvas HTML element.
	 */
	private canvasElement: HTMLCanvasElement;

	/**
	 * The Canvas rendering context.
	 */
	private canvasContext: CanvasRenderingContext2D;

	/**
	 * Points are stored as a value between 0 and 1, 0 being the most left and 1 being the most
	 * right coordinate. WidthFactor makes it possible to convert back to absolute canvas pixels.
	 */
	private widthFactor: number;
	/**
	 * Points are stored as a value between 0 and 1, 0 being the highest and 1 being the lowest
	 * coordinate. HeightFactor makes it possible to convert back to absolute canvas pixels.
	 */
	private heightFactor: number;

	/**
	 * The annotation's drawing, consisting of line segments which are drawn onto the canvas.
	 * @type {any[]}
	 */
	public annotationSegments: AnnotationLineSegment[] = [];

	/**
	 * Boolean flag indicating whether the user is currently drawing line segments.
	 */
	private cursorIsDown: boolean;

	/**
	 * The line segment the user is currently adding coordinates to.
	 */
	private currentSegment: AnnotationLineSegment;

	/**
	 * The current pencil settings. This object is stored along with the coordinates to create
	 * colored lines of variable size. Note that operation indicates whether
	 * @type {PencilPreferences}
	 */
	private pencil: PencilPreferences = {
		color: '#000000',
		lineWidth: 5,
		operation: 'source-over'
	};

	/**
	 * Stack representing the history of line segments which are undone using the "undo" button.
	 * This stack can be used to "redo" these segments. The stack is cleared when the user draws a
	 * new linesegment.
	 * @type {any[]}
	 */
	private undoneHistory: AnnotationLineSegment[] = [];

	/**
	 * List of transformations to be applied on the given annotation. Possible transformations are
	 * inverting the x or y coordinates (e.g. 1 - x, 1 - y) or flipping the x and y coordinates.
	 * These transformations are necessary for rotating submissions.
	 * @type {any[]}
	 */
	private annotationTransformations: ((Point) => Point)[] = [];

	constructor() {
	}

	ngOnInit() {
	}

	ngOnChanges(changes: SimpleChanges): void {
		//Store the canvas context when possible. It is not a problem if this is done more than once
		//we do have to be sure it exists though..
		this.canvasContext = this.canvasElement.getContext('2d');

		//Update the canvas size when possible. Since images are loaded asynchronously, it is
		//possible the size of this component alters after a while, check this as much as possible.
		this.setCanvasSize();

		//When there are annotationSegments present, redraw the canvas to show them.
		if (this.annotationSegments && this.annotationSegments.length > 0) {
			this.redrawCanvas()
		}

		//When the user wants to create a new annotation. Remove all old annotations and start
		//creating a new one. Also register mouse and touch events.
		if (this.allowEditing && this.canvasContext) {
			this.annotationSegments = [];
			this.annotationTransformations = [];
			this.redrawCanvas();
			this.registerMouseEvents();
			this.registerTouchEvents();
		}
	}

	/**
	 * Sets the canvas size by checking the current elements viewport width/height.
	 */
	setCanvasSize() {
		if (this.canvasElement) {
			this.canvasElement.height = this.canvasElement.clientHeight;
			this.canvasElement.width = this.canvasElement.clientWidth;

			this.widthFactor = this.canvasElement.clientWidth;
			this.heightFactor = this.canvasElement.clientHeight;
		}
	}

	/**
	 * Sets the canvas size and redraws the current line segments according to the new sizes.
	 */
	resizeCanvas() {
		if (this.canvasContext) {
			this.setCanvasSize();
			this.redrawCanvas();
		}
	}

	/**
	 * Sets the annotation's line segments as this canvas' path. Also sets the transform operations
	 * to apply.
	 * @param {AnnotationLineSegment[]} segments
	 * @param {boolean} invertX
	 * @param {boolean} invertY
	 * @param {boolean} flipXY
	 */
	setDrawing(
		segments: AnnotationLineSegment[],
		invertX: boolean,
		invertY: boolean,
		flipXY: boolean
	) {
		this.annotationSegments = segments;
		this.annotationTransformations = [];
		if (flipXY) {
			this.annotationTransformations.push(this.FLIP_XY_TRANSFORMATION);
		}
		if (invertX) {
			this.annotationTransformations.push(this.INVERT_X_TRANSFORMATION);
		}
		if (invertY) {
			this.annotationTransformations.push(this.INVERT_Y_TRANSFORMATION);
		}
	}

	/**
	 * Get the pencil color.
	 * @returns {string}
	 */
	get color(): string {
		this.updatePencil();
		return this.pencil.color;
	}

	/**
	 * Sets the pencil color
	 * @param {string} c
	 */
	set color(c: string) {
		this.pencil.color = c;
		this.updatePencil();
	}

	/**
	 * Gets the linewidth
	 * @returns {number}
	 */
	get lineWidth(): number {
		this.updatePencil();
		return this.pencil.lineWidth;
	}

	/**
	 * Sets the linewidth.
	 * @param {number} lw
	 */
	set lineWidth(lw: number) {
		this.pencil.lineWidth = lw;
		this.updatePencil();
	}

	/**
	 * Returns whether the redo button is disabled. This is the case when there is no undone
	 * history.
	 * @returns {boolean}
	 */
	get isRedoDisabled(): boolean {
		return !this.undoneHistory || this.undoneHistory.length == 0;
	}

	/**
	 * Returns whether the undo button is disabled, which is the case when there are no annotation
	 * line segments present (e.g. the drawing is empty).
	 * @returns {boolean}
	 */
	get isUndoDisabled(): boolean {
		return this.annotationSegments && this.annotationSegments.length == 0;
	}

	/**
	 * Returns whether the user has the eraser active.
	 * @returns {boolean}
	 */
	get isEraserActive(): boolean {
		return this.pencil.operation === 'destination-out';
	}

	/**
	 * Returns whether the user may draw over the submission.
	 * @returns {boolean}
	 */
	get editingEnabled(): boolean {
		return this.allowEditing;
	}

	/**
	 * Toggles the eraser state.
	 */
	toggleEraser() {
		this.pencil.operation = this.pencil.operation === 'source-over'
			? 'destination-out'
			: 'source-over';
		this.updatePencil();
	}

	/**
	 * Updates the canvas with the current pencil preferences.
	 */
	private updatePencil() {
		this.canvasContext.strokeStyle = this.pencil.color;
		this.canvasContext.lineWidth = this.pencil.lineWidth;
		this.canvasContext.globalCompositeOperation = this.pencil.operation;
		this.canvasContext.lineJoin = this.canvasContext.lineCap = 'round';
	}

	/**
	 * Sets the cursorIsDown flag to the given value and prepares/finishes a line segment for the
	 * new annotation.
	 * @param {MouseEvent} event
	 * @param {boolean} cursorIsDown
	 */
	setDrawingEnabled(event: MouseEvent, cursorIsDown: boolean) {
		this.cursorIsDown = cursorIsDown;

		//Get the coordinates for this event.
		const current = this.getRelativeEventCoordinates(event);

		if (this.cursorIsDown) {
			//If a new segment is started, clear the old undone history since it is no longer
			//relevant.
			this.undoneHistory = [];

			//Creates a new linesegment.
			this.currentSegment = {
				'pencil': Object.assign({}, this.pencil),
				'points': [current]
			};

			//Pushes the new segment onto the existing drawing.
			this.annotationSegments.push(this.currentSegment);

			//Begin a new canvas path, which is represented by our line segment.
			this.canvasContext.beginPath();

			//Move the starting point of the new line segment to the given point, multiplied by
			//the current width/height of the canvas.
			this.canvasContext.moveTo(current.x * this.widthFactor, current.y * this.heightFactor);
		} else {
			//On letting the mouse/finger go, redraw the canvas and "finalize" the new line segment.
			this.redrawCanvas();
		}
	}

	/**
	 * Handles a "move" MouseEvent and creates a new point for the current line segment.
	 * @param {MouseEvent} event
	 */
	handleDrawing(event: MouseEvent): void {
		if (this.cursorIsDown && this.currentSegment.points.length != 0) {
			//Get the previous point in the line segment.
			const previous: Point = this.currentSegment.points.slice(-1).pop();
			//Get the current event point.
			const current = this.getRelativeEventCoordinates(event);
			//Draw a line between the given points, using a bezier curve.
			this.drawSegment(previous, current);
			//Add the current point to the line segment.
			this.currentSegment.points.push(current);
			//Redraw the canvas to make it semi-permanent.
			this.redrawCanvas();
		}
	}

	/**
	 * Returns the coordinates of the event, relative to the viewport and represented as numbers
	 * between 0 and 1. With (0,0) being top-left, and (1,1) being bottom-right.
	 * @param {MouseEvent} event
	 * @returns {Point}
	 */
	getRelativeEventCoordinates(event: MouseEvent) {
		const rect = this.canvasElement.getBoundingClientRect();
		return <Point> {
			x: (event.clientX - rect.left) / this.widthFactor,
			y: (event.clientY - rect.top) / this.heightFactor
		};
	}

	/**
	 * Redraws the canvas by following the line segments in annotationLineSegments.
	 */
	redrawCanvas(): void {
		//First, clear the whole canvas. We do not want old data on there.
		this.canvasContext.clearRect(
			0,
			0,
			this.canvasContext.canvas.width,
			this.canvasContext.canvas.height
		);

		if (!this.annotationSegments) {
			return;
		}

		//Now draw all segments one by one.
		this.annotationSegments
			.forEach((segment: AnnotationLineSegment) => {
				//Set the pencil to the values in the current line segment..
				this.canvasContext.strokeStyle = segment.pencil.color;
				this.canvasContext.lineWidth = segment.pencil.lineWidth;
				this.canvasContext.globalCompositeOperation = segment.pencil.operation;
				this.canvasContext.lineJoin = this.canvasContext.lineCap = 'round';

				//Edit all points to a better representable state.
				const points: Point[] = segment
					.points
					//First apply all transformation operations, so the rotation of the annotation
					// is
					//correct.
					.map((p: Point) => this.annotationTransformations.reduce((pp, f) => f(pp), p))
					//Then multiply each point by the current width-/heightFactor so the annotation
					//is of the right size.
					.map((p: Point) => <Point>{
						x: p.x * this.widthFactor,
						y: p.y * this.heightFactor
					});

				//Move the canvas path to the first point.
				let p1: Point = points[0];
				this.canvasContext.beginPath();
				this.canvasContext.moveTo(p1.x, p1.y);

				//Draw all segments using the current and previous point using a bezier curve.
				points.reduce((p1, p2) => {
					this.drawSegment(p1, p2);
					return p2;
				});

				//Fill the path with the current pencil settings.
				this.canvasContext.stroke();
			});
	}

	/**
	 * Creates a path between two given points using a bezier curve.
	 * @param {Point} p1
	 * @param {Point} p2
	 */
	drawSegment(p1: Point, p2: Point): void {
		const midPoint = ImageWithOverlayComponent.calculateMidPoint(p1, p2);
		this.canvasContext.quadraticCurveTo(midPoint.x, midPoint.y, p2.x, p2.y);
	}

	/**
	 * Calculates the middle point between the two given points. This is needed as a control
	 * point for a bezier curve, allowing smoother and more curved lines.
	 * @param {Point} p1
	 * @param {Point} p2
	 * @returns {Point}
	 */
	private static calculateMidPoint(p1: Point, p2: Point): Point {
		return <Point>{
			'x': p1.x + (p2.x - p1.x) / 2,
			'y': p1.y + (p2.y - p1.y) / 2,
		}
	}

	/**
	 * Undos the last created line segment, when present.
	 */
	undoLastSegment() {
		if (this.annotationSegments.length > 0) {
			this.undoneHistory.push(this.annotationSegments.pop());

			this.redrawCanvas();
		}
	}

	/**
	 * Redos the last undone line segment, when possible.
	 */
	redoLastSegment() {
		if (this.undoneHistory.length > 0) {
			this.annotationSegments.push(this.undoneHistory.pop());

			this.redrawCanvas();
		}
	}

	/**
	 * Registers the mouse events on the canvas.
	 */
	private registerMouseEvents() {
		this.canvasElement.addEventListener('mousedown', event => this.setDrawingEnabled(event, true));
		this.canvasElement.addEventListener('mouseup', event => this.setDrawingEnabled(event, false));
		this.canvasElement.addEventListener('mousemove', event => this.handleDrawing(event));
	}

	/**
	 * Registers the touch events by emulating a mouse event.
	 */
	private registerTouchEvents() {
		//Creates a converter which creates a new given event.
		const touchToMouseHandler = (eventName: string) => {
			const e = (event: TouchEvent) => {
				//First, prevent scrolling when drawing. This is annoying.
				event.preventDefault();

				//Create a new MouseEventInit object, which contains coordinates for a new
				// MouseEvent.
				let initObject: MouseEventInit;
				if (
					event.touches && event.touches[0]
					&& event.touches[0].clientX && event.touches[0].clientY
				) {
					//Calculate the coordinates for the event..
					initObject = {
						clientX: event.touches[0].clientX,
						clientY: event.touches[0].clientY,
						bubbles: false
					};
				} else {
					//Since the "touchend" event does not send coordinates, simply use the last
					//point in the current line segment.
					const p = this.currentSegment.points.slice(-1).pop();
					initObject = {
						clientX: p.x,
						clientY: p.y,
						bubbles: false
					};
				}

				//Create and dispatch the newly created event.
				const newEvent: MouseEvent = new MouseEvent(eventName, initObject);
				this.canvasElement.dispatchEvent(newEvent);
				event.stopPropagation();
			};

			//Bind this component as "this" variable.
			e.bind(this);
			return e;
		};

		//Register the touch events
		this.canvasElement.addEventListener('touchstart', touchToMouseHandler('mousedown'));
		this.canvasElement.addEventListener('touchend', touchToMouseHandler('mouseup'));
		this.canvasElement.addEventListener('touchmove', touchToMouseHandler('mousemove'));
	}


	/**
	 * Inverts the x coordinate of a point by applying a "1 - point.x" operation.
	 * @param {Point} p
	 * @returns {Point}
	 * @constructor
	 */
	private readonly INVERT_X_TRANSFORMATION = (p: Point) => <Point> {x: 1 - p.x, y: p.y};
	/**
	 * Inverts the y coordinate of a point by applying a "1 - point.y" operation.
	 * @param {Point} p
	 * @returns {Point}
	 * @constructor
	 */
	private readonly INVERT_Y_TRANSFORMATION = (p: Point) => <Point> {x: p.x, y: 1 - p.y};

	// noinspection JSSuspiciousNameCombination
	/**
	 * Inverts a coordinate by flipping the x and y coordinate.
	 * @param {Point} p
	 * @returns {Point}
	 * @constructor
	 */
	private readonly FLIP_XY_TRANSFORMATION = (p: Point) => <Point> {x: p.y, y: p.x};
}
