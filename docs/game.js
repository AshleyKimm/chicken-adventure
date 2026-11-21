/*
 * ChickenAdventure (web version) - game.js
 * Purpose: browser port of the JavaFX game. Each Java class has a matching
 *          JavaScript class below, with the same fields, methods and logic.
 * Author: Ashley Kim
 */
"use strict";

// ---------------------------------------------------------------------------
// Helpers that stand in for the JavaFX classes the game used
// ---------------------------------------------------------------------------

// Image cache (stands in for javafx.scene.image.Image). Everything is
// preloaded before the first scene runs, so width/height are always known.
const IMAGE_FILES = [
	"images/bean1.png",
	"images/chickenLeft1.png",
	"images/chickenRight.png",
	"images/heart3.png",
	"images/raindrop1.png",
	"images/raindrop1 (2).png",
	"images/raindrop1 (3).png",
	"images/rainyBackground1.png",
	"images/seed1.png",
	"images/skateboard1.png",
];
const images = {};

function loadImage(name) {
	return new Promise((resolve, reject) => {
		const img = new Image();
		img.onload = () => { images[name] = img; resolve(); };
		img.onerror = () => reject(new Error("Could not load " + name));
		img.src = encodeURI(name);
	});
}

function getImage(name) {
	return images[name];
}

// javafx.geometry.Rectangle2D (only what the game uses)
class Rectangle2D {
	constructor(x, y, width, height) {
		this.minX = x;
		this.minY = y;
		this.maxX = x + width;
		this.maxY = y + height;
	}

	intersects(r) {
		return r.minX < this.maxX && r.maxX > this.minX &&
			r.minY < this.maxY && r.maxY > this.minY;
	}
}

// javafx.animation.AnimationTimer. JavaFX calls handle() 60 times a second.
// Browsers on 120/144 Hz screens fire requestAnimationFrame faster than that,
// so frames are stepped at a fixed 60 per second to keep the original speeds.
class AnimationTimer {
	static FRAME_MS = 1000 / 60;

	constructor(handle) {
		this.handle = handle;
		this.running = false;
		this.rafId = 0;
	}

	start() {
		this.running = true;
		this.lastTime = performance.now();
		this.accumulated = AnimationTimer.FRAME_MS; // draw a frame right away
		const loop = (now) => {
			if (!this.running) return;
			// Cap the catch-up so a backgrounded tab doesn't run hundreds of frames
			this.accumulated += Math.min(now - this.lastTime, 250);
			this.lastTime = now;
			while (this.running && this.accumulated >= AnimationTimer.FRAME_MS) {
				this.accumulated -= AnimationTimer.FRAME_MS;
				this.handle(now);
			}
			this.rafId = requestAnimationFrame(loop);
		};
		this.rafId = requestAnimationFrame(loop);
	}

	stop() {
		this.running = false;
		cancelAnimationFrame(this.rafId);
	}
}

// Shared canvas (every FXML scene had its own 1700x956 canvas)
const gameCanvas = {
	element: document.getElementById("gameCanvas"),
	getWidth() { return this.element.width; },
	getHeight() { return this.element.height; },
};
const gc = gameCanvas.element.getContext("2d");

// Keyboard input. JavaFX KeyCode.toString() gives "LEFT"/"RIGHT".
const KEY_NAMES = { ArrowLeft: "LEFT", ArrowRight: "RIGHT", ArrowUp: "UP", ArrowDown: "DOWN" };

function keyCodeString(e) {
	return KEY_NAMES[e.key] || e.code;
}

// ---------------------------------------------------------------------------
// Bean.java
// ---------------------------------------------------------------------------
class Bean {
	// static variable of the numbers of beans collected
	static score = 0;

	constructor(gc, gameCanvas, imageName = "images/bean1.png") {
		this.x = 0;
		// Allows the bean to appear on same y axis (ground)
		this.y = 700;
		this.imageName = imageName;
		this.image = getImage(this.imageName);
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		this.randomBean();
	}

	// Sets the random position of bean in the game canvas (same x axis)
	randomBean() {
		this.x = Math.trunc(Math.random() * (this.gameCanvas.getWidth() - this.image.width));
		this.y = 700;
	}

	// Displays the bean image at corresponding random position
	drawBean() {
		this.gc.drawImage(this.image, this.x, this.y);
	}

	// gets the x, y, width and height of image to set collision with other images
	getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.width, this.image.height);
	}
}

// ---------------------------------------------------------------------------
// Chicken.java
// ---------------------------------------------------------------------------
class Chicken {
	static speed = 3.0;
	static lives = 3;

	constructor(gc, gameCanvas, input, imageName = "images/chickenRight.png") {
		// The chicken is at the center when the game begins
		this.x = 850;
		this.y = 700;
		this.dx = 0;
		this.dy = 0;
		this.imageName = imageName;
		this.image = getImage(this.imageName);
		this.left = "LEFT";
		this.right = "RIGHT";
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		// Array of user's key input
		this.input = input;
	}

	move() {
		// left & right button
		if (this.input.includes(this.left)) {
			this.dx = -Chicken.speed;
		} else if (this.input.includes(this.right)) {
			this.dx = Chicken.speed;
		} else {
			this.dx = 0;
		}
		// save old position
		const x = this.x;

		// Adds the new dx value to current position to make it move
		this.x += this.dx;

		// If this.x is bigger than the width of the canvas, it is kept inside the window
		if (this.x < 0 || this.x > this.gameCanvas.getWidth() - this.image.width) {
			this.x = x;
		}

		// displays the image in corresponding position
		this.gc.drawImage(this.image, this.x, this.y);
	}

	// gets the x, y, width and height of image to set collision with other images
	getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.width, this.image.height);
	}

	// Checks for the collision with other images, and return true
	collisionRain(rain) {
		return this.getBoundary().intersects(rain.getBoundary());
	}

	collisionBean(bean) {
		return this.getBoundary().intersects(bean.getBoundary());
	}

	collisionSeed(seed) {
		return this.getBoundary().intersects(seed.getBoundary());
	}
}

// ---------------------------------------------------------------------------
// Rain.java
// ---------------------------------------------------------------------------
class Rain {
	static numRain = 5;
	static speed = 3.0;

	constructor(gc, gameCanvas, imageName = ["images/raindrop1.png", "images/raindrop1 (2).png", "images/raindrop1 (3).png"]) {
		// Allows the raindrops to only move vertically
		this.x = 0;
		// Places the raindrops at random position of x axis
		this.y = Math.random() * 200;
		this.dy = -Rain.speed;

		// Chooses raindrop images with random shades from the array
		this.imageName = imageName;
		this.randomRain = Math.trunc(Math.random() * this.imageName.length);
		this.image = getImage(this.imageName[this.randomRain]);

		this.gc = gc;
		this.gameCanvas = gameCanvas;
		this.randomStart();
	}

	// Makes the raindrops appear in random position
	randomStart() {
		this.dy = Rain.speed;

		// Generates random x value within the size of gameCanvas
		// Generates random y position within 300 y value
		this.x = Math.trunc(Math.random() * (this.gameCanvas.getWidth() - this.image.width));
		this.y = Math.trunc(Math.random() * 300);
	}

	setDy(dy) {
		this.dy = dy;
	}

	move() {
		// Add dy value (speed) to the current y position
		this.y += this.dy;

		// If this.y value is equal or bigger than the width or height
		// of the image, then they are placed back to the top at random x value.
		if (this.y <= 0 || this.y >= this.gameCanvas.getHeight() - this.image.height) {
			this.x = Math.random() * (this.gameCanvas.getWidth() - this.image.width);
			this.y = Math.trunc(Math.random() * 300);
		}

		// displays the image in corresponding position
		this.gc.drawImage(this.image, this.x, this.y);
	}

	// gets the x, y, width and height of image to set collision with other images
	getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.width, this.image.height);
	}
}

// ---------------------------------------------------------------------------
// Seed.java
// ---------------------------------------------------------------------------
class Seed {
	constructor(gc, gameCanvas, imageName = "images/seed1.png") {
		this.x = 0;
		this.y = 0;
		// Same speed as raindrops
		this.dy = -Rain.speed;
		this.imageName = imageName;
		this.image = getImage(this.imageName);
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		this.randomStart();
	}

	// Displays the seed at random position
	randomStart() {
		this.dy = Rain.speed;

		// Generates random x value within the size of gameCanvas
		this.x = Math.trunc(Math.random() * (this.gameCanvas.getWidth() - this.image.width));
		this.y = 0;
	}

	move() {
		// Add dy value (speed) to the current y position
		this.y += this.dy;

		// If this.y value is bigger than the width or height
		// of the image, then it is sent back to the top
		if (this.y <= 0 || this.y >= this.gameCanvas.getHeight() - this.image.height) {
			this.x = Math.random() * (this.gameCanvas.getWidth() - this.image.width);
			this.y = 0;
		}
		// displays the image in corresponding position
		this.gc.drawImage(this.image, this.x, this.y);
	}

	// gets the x, y, width and height of image to set collision with other images
	getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.width, this.image.height);
	}
}

// ---------------------------------------------------------------------------
// Skateboard.java
// ---------------------------------------------------------------------------
class Skateboard {
	constructor(gc, gameCanvas, input, imageName = "images/skateboard1.png") {
		// The starting position
		this.x = 830;
		this.y = 770;
		this.dx = 0;
		this.dy = 0;
		this.imageName = imageName;
		this.image = getImage(this.imageName);
		this.left = "LEFT";
		this.right = "RIGHT";
		this.gc = gc;
		this.gameCanvas = gameCanvas;
		// Array of user's key input
		this.input = input;
	}

	move() {
		// left & right button
		if (this.input.includes(this.left)) {
			this.dx = -Chicken.speed;
		} else if (this.input.includes(this.right)) {
			this.dx = Chicken.speed;
		} else {
			this.dx = 0;
		}

		// save old position
		const x = this.x;

		// Adds the new dx and dy value to current position to make it move
		this.x += this.dx;
		this.y += this.dy;

		// If this.x value is bigger than the width or height
		// of the image, then it is kept inside the width of canvas
		if (this.x < 0 || this.x > this.gameCanvas.getWidth() - this.image.width) {
			this.x = x;
		}
		// displays the image in corresponding position
		this.gc.drawImage(this.image, this.x, this.y);
	}

	getBoundary() {
		return new Rectangle2D(this.x, this.y, this.image.width, this.image.height);
	}
}

// ---------------------------------------------------------------------------
// DisplayText.java
// ---------------------------------------------------------------------------
const WHITE = "#FFFFFF";
const RED = "#FF0000";

class DisplayText {
	static higherScore = 0;

	constructor(gc, gameCanvas) {
		this.gc = gc;
		this.gameCanvas = gameCanvas;

		// Assign the same font with different sizes
		this.font = "100px pixel";
		this.fontGameOver = "200px pixel";
		this.fontOpening = "150px pixel";
		this.fontHowTo = "80px pixel";

		// Assign the images
		this.heart = getImage("images/heart3.png");
		this.chickenRight = getImage("images/chickenRight.png");
		this.chickenLeft = getImage("images/chickenLeft1.png");
		this.bean = getImage("images/bean1.png");
		this.seed = getImage("images/seed1.png");
	}

	// Displays the lives and the score (number of beans collected)
	display(chicken) {
		const gc = this.gc;
		gc.drawImage(this.heart, 1100, 30);

		const lives = "X " + Chicken.lives;
		gc.font = this.font;
		gc.fillStyle = WHITE;
		gc.fillText(lives, 1200, 100);

		gc.drawImage(this.bean, 1350, 30);

		const score = "X " + Bean.score;
		gc.font = this.font;
		gc.fillStyle = WHITE;
		gc.fillText(score, 1450, 100);
	}

	// Displays the game title
	gameOpening() {
		const gc = this.gc;
		gc.font = this.fontGameOver;
		gc.fillStyle = WHITE;
		gc.fillText("CHICKEN", 450, 190);
		gc.fillText("ADVENTURE", 380, 350);
	}

	// Displays the 'How To Play' instructions
	howToPlay() {
		const gc = this.gc;
		gc.font = this.fontOpening;
		gc.fillStyle = WHITE;
		gc.fillText("How To Play", 450, 120);

		gc.font = this.fontHowTo;
		gc.fillText("Move the chicken with arrow keys", 100, 300);

		// Chicken images
		gc.drawImage(this.chickenRight, 1400, 220);
		gc.drawImage(this.chickenLeft, 1500, 220);

		gc.fillText("Collect as many beans as possible", 100, 400);

		// Bean image
		gc.drawImage(this.bean, 1450, 330);

		gc.fillText("Collect seeds to speed up", 100, 500);

		// Seed image
		gc.drawImage(this.seed, 1110, 440);

		gc.fillText("You get three lives", 100, 600);

		// Heart (lives) image
		gc.drawImage(this.heart, 880, 540);

		gc.fillText("Good luck", 100, 800);
	}

	// Displays the ending text (score) when game ends (when lives = 0)
	displayEnding() {
		if (GameController.newScore <= GameController.oldScore)
			DisplayText.higherScore = GameController.oldScore;
		else DisplayText.higherScore = GameController.newScore;

		const gc = this.gc;
		gc.font = this.fontGameOver;
		gc.fillStyle = RED;
		gc.fillText("GAME OVER", 400, 400);

		gc.font = this.font;
		gc.fillStyle = WHITE;
		gc.fillText("YOUR SCORE ", 580, 570);
		gc.fillText("" + Bean.score, 820, 650);
		gc.fillText("HIGH SCORE ", 580, 720);
		gc.fillText("" + DisplayText.higherScore, 820, 800);
	}
}

// ---------------------------------------------------------------------------
// Scene switching (replaces FXMLLoader + stage.setScene)
// ---------------------------------------------------------------------------
const sceneElements = {
	start: document.getElementById("startScene"),
	game: document.getElementById("gameScene"),
	howTo: document.getElementById("howToScene"),
};

// Only the current scene's timer runs. In the JavaFX version the old scene's
// AnimationTimer was never stopped and kept running in the background.
let currentTimer = null;

function runTimer(timer) {
	if (currentTimer) currentTimer.stop();
	currentTimer = timer;
	timer.start();
}

function showScene(name) {
	for (const [key, el] of Object.entries(sceneElements)) {
		el.hidden = key !== name;
	}
	// Each scene sets its own key handlers, like scene.setOnKeyPressed
	window.onkeydown = null;
	window.onkeyup = null;
	window.onblur = null;
	focusGame();
}

// Gives keyboard focus to the game. This moves focus off the clicked button
// (so Space/Enter don't press it again) while keeping it inside this page.
// Calling blur() instead lets browsers move focus to the parent page when the
// game is embedded in an iframe, and then the arrow keys never reach the game.
const stageElement = document.getElementById("stage");

function focusGame() {
	stageElement.focus({ preventScroll: true });
}

// Clicking anywhere on the game focuses it again
stageElement.addEventListener("pointerdown", (e) => {
	if (!e.target.closest("button")) focusGame();
});

// "CLICK TO PLAY" hint, shown on the game scene while the page doesn't
// have keyboard focus (e.g. the player clicked elsewhere on the website)
const focusHint = document.getElementById("focusHint");

function updateFocusHint() {
	focusHint.hidden = sceneElements.game.hidden || document.hasFocus();
}

window.addEventListener("focus", updateFocusHint);
window.addEventListener("blur", updateFocusHint);
setInterval(updateFocusHint, 500);

const background = () => getImage("images/rainyBackground1.png");

// ---------------------------------------------------------------------------
// StartController.java
// ---------------------------------------------------------------------------
const StartController = {
	// Button click handler for 'START' button
	startButtonHandler() {
		showScene("game");
		GameController.gameLoop();
	},

	// Button click handler for 'HOW TO PLAY' button
	howToPlayButtonHandler() {
		showScene("howTo");
		HowToController.gameLoop();
	},

	welcome() {
		const displayText = new DisplayText(gc, gameCanvas);

		runTimer(new AnimationTimer(() => {
			// draw background image
			gc.drawImage(background(), 0, 0);

			// Displays the game title
			displayText.gameOpening();
		}));
	},
};

// ---------------------------------------------------------------------------
// HowToController.java
// ---------------------------------------------------------------------------
const HowToController = {
	// A button click handler for 'BACK TO MENU' button
	backToMenuButtonHandler() {
		showScene("start");
		StartController.welcome();
	},

	gameLoop() {
		const displayText = new DisplayText(gc, gameCanvas);

		runTimer(new AnimationTimer(() => {
			// draw background image
			gc.drawImage(background(), 0, 0);

			// Displays the instructions
			displayText.howToPlay();
		}));
	},
};

// ---------------------------------------------------------------------------
// GameController.java
// ---------------------------------------------------------------------------
const GameController = {
	// Boolean variables to check for collision
	collidedRain: false,
	collidedBean: false,
	collidedSeed: false,
	newScore: 0,
	oldScore: 0,

	// A Button Click Handler for 'MENU' button on game scene
	menuButtonHandler() {
		showScene("start");
		StartController.welcome();
	},

	// Resets all the variables and restart the game when 'RESTART' button is clicked
	restartButtonClickHandler() {
		Chicken.lives = 3;
		Bean.score = 0;
		Chicken.speed = 3;
		Rain.speed = 3;
		Rain.numRain = 5;
		// Runs the gameLoop to start the game
		GameController.gameLoop();
	},

	gameLoop() {
		const self = GameController;

		// Resets the variables so that whenever gameLoop() is exited,
		// the game automatically restarts
		Chicken.lives = 3;
		Bean.score = 0;
		Chicken.speed = 3;
		Rain.speed = 3;
		// The JavaFX version only reset this in the RESTART handler, so going
		// MENU -> START kept the extra raindrops from the last game
		Rain.numRain = 5;

		// Array that holds the raindrops
		const rainList = [];

		const displayText = new DisplayText(gc, gameCanvas);

		// Adds the key input to the array when the key is pressed
		const input = [];

		window.onkeydown = (e) => {
			// Converts the key to a string, and adds it to the array
			const code = keyCodeString(e);
			if (code === "LEFT" || code === "RIGHT") e.preventDefault();
			if (!input.includes(code)) {
				input.push(code);
			}
		};

		// Removes the key input from the array when the key is released
		window.onkeyup = (e) => {
			const code = keyCodeString(e);
			const i = input.indexOf(code);
			if (i !== -1) input.splice(i, 1);
		};

		// Browsers don't send keyup if the window loses focus mid-press
		window.onblur = () => { input.length = 0; };
		updateFocusHint();

		// Create the raindrops; adds the starting number of raindrops to the array
		for (let i = 0; i < Rain.numRain; i++) {
			rainList.push(new Rain(gc, gameCanvas));
		}

		const chicken = new Chicken(gc, gameCanvas, input);
		const skateboard = new Skateboard(gc, gameCanvas, input);
		const bean = new Bean(gc, gameCanvas);
		const seed = new Seed(gc, gameCanvas);

		runTimer(new AnimationTimer(() => {
			// draw background image
			gc.drawImage(background(), 0, 0);

			skateboard.move();
			chicken.move();
			bean.drawBean();
			seed.move();

			// Displays lives and score
			displayText.display(chicken);

			// Moves the individual raindrops
			for (let i = 0; i < Rain.numRain; i++) {
				rainList[i].move();
			}

			self.collidedBean = chicken.collisionBean(bean);

			// Increase score when bean is collected
			if (self.collidedBean) {
				Bean.score++;
				bean.randomBean();

				// Raindrops' speed increases for every 5 beans collected
				if (Bean.score % 5 === 0 && Bean.score !== 0)
					Rain.speed += 0.5;

				// Number of raindrops increase for every 6 beans collected
				if (Bean.score % 6 === 0 && Bean.score !== 0) {
					Rain.numRain++;
					rainList.push(new Rain(gc, gameCanvas));
				}
			}

			self.collidedSeed = chicken.collisionSeed(seed);

			// Increase the chicken's speed when seed collides
			if (self.collidedSeed) {
				seed.randomStart();
				Chicken.speed += 0.5;
			}

			// Check collision for each raindrop in the array
			for (let i = 0; i < Rain.numRain; i++) {
				const r = rainList[i];

				self.collidedRain = chicken.collisionRain(r);

				// When collided with raindrop, lives decrease and the raindrop
				// is randomly placed
				if (self.collidedRain) {
					Chicken.lives -= 1;
					rainList[i].randomStart();
				}
			}

			// When the chicken dies, displays the ending string and final score
			// Stops everything by making the speed 0
			// (<= instead of ==: two drops hitting on the same frame used to
			// skip past 0 and the game never ended)
			if (Chicken.lives <= 0) {
				Chicken.lives = 0;
				// stores the current score
				self.newScore = Bean.score;
				displayText.displayEnding();
				Chicken.speed = 0;
				Rain.speed = 0;
				seed.dy = 0;

				// Sets the speed to 0 for every raindrop
				for (let i = 0; i < Rain.numRain; i++) {
					rainList[i].setDy(Rain.speed);
				}
				// stores the higher score for the next game's comparison
				self.oldScore = DisplayText.higherScore;
			}
		}));
	},
};

// ---------------------------------------------------------------------------
// Main.java
// ---------------------------------------------------------------------------

// Scales the 1700x956 game to fit the available width. When the page is
// opened directly (not inside an iframe) it also fits the window height.
function fitStage() {
	const frame = document.getElementById("frame");
	const stage = document.getElementById("stage");
	const width = document.documentElement.clientWidth;
	let scale = width / 1700;
	if (window.self === window.top) {
		scale = Math.min(scale, window.innerHeight / 956);
	}
	stage.style.transform = "scale(" + scale + ")";
	frame.style.width = Math.floor(1700 * scale) + "px";
	frame.style.height = Math.floor(956 * scale) + "px";
}

async function main() {
	fitStage();
	window.addEventListener("resize", fitStage);
	new ResizeObserver(fitStage).observe(document.documentElement);

	document.getElementById("startButton").onclick = StartController.startButtonHandler;
	document.getElementById("howToPlayButton").onclick = StartController.howToPlayButtonHandler;
	document.getElementById("menuButton").onclick = GameController.menuButtonHandler;
	document.getElementById("restartButton").onclick = GameController.restartButtonClickHandler;
	document.getElementById("backToMenuButton").onclick = HowToController.backToMenuButtonHandler;

	try {
		await Promise.all([
			...IMAGE_FILES.map(loadImage),
			document.fonts.load("100px pixel"),
		]);
	} catch (e) {
		console.error(e);
	}

	showScene("start");
	StartController.welcome();
}

main();
