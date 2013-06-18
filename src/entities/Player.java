package entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.Sound;

import tilemap.TileMap;

@SuppressWarnings("unused")
public class Player extends MapObject {

	private int health;
	private int maxHealth;
	private int pee;
	private int maxPee;

	private long peeReminderTimer;
	private int peeReminderDuration;
	private boolean knockback;

	private int fire;
	private int maxFire;

	private boolean dead;

	private boolean peeing;
	private boolean throwingPeeball;
	private int fireCost;
	private int fireBallDamage;

	private int peeCost;
	private int peeDamage;
	private int peeBallDamage;
	private int timeBetweenPeeBalls;
	private int peeBallCounter;

	private double peeArcX;
	private double peeArcY;

	private int experience;
	private int nextLevel;

	private ArrayList<Pee> peeList;
	private ArrayList<Blood> bloodList;
	private ArrayList<PeeBall> peeballs;
	private PeeBallTrajectory pbTrajectory;
	private ArrayList<Enemy> enemies;
	private boolean drawTrajectory;

	private boolean scratching;
	private int scratchDamage;
	private int scratchRange;

	private double lastWalkableX;
	private double lastWalkableY;
	private boolean lastFacing;

	private boolean gliding;

	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 2, 8, 1, 2, 4, 2, 5 };

	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int FIREBALL = 5;
	private static final int SCRATCHING = 6;

	public Player(TileMap tm) {
		super(tm);
		width = 30;
		height = 30;
		cWidth = 16;
		cHeight = 20;

		moveSpeed = 0.2;
		maxSpeed = 1.9;
		stopSpeed = 0.3;
		fallSpeed = 0.13;
		maxFallSpeed = 8.2;
		jumpStart = -5.2;
		stopJumpSpeed = 0.2;

		throwingPeeball = false;
		drawTrajectory = false;

		peeReminderTimer = 0;
		peeReminderDuration = 10000;

		peeBallCounter = timeBetweenPeeBalls = 40;

		peeArcX = 0;
		peeArcY = 0;

		experience = 0;
		nextLevel = 500;

		dead = false;

		facingRight = true;

		health = maxHealth = 3;
		pee = maxPee = 25000;

		peeCost = 30;
		peeDamage = 5;
		peeBallDamage = 18;
		peeList = new ArrayList<Pee>();
		bloodList = new ArrayList<Blood>();
		peeballs = new ArrayList<PeeBall>();
		pbTrajectory = new PeeBallTrajectory(tilemap, facingRight, this);
		scratchDamage = 8;
		scratchRange = 40;

		try {
			BufferedImage spritesheet = ImageIO.read(getClass()
					.getResourceAsStream("/Sprites/Player/playersprites3.png"));
			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 7; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for (int j = 0; j < numFrames[i]; j++) {
					if (i != 6) {
						bi[j] = spritesheet.getSubimage(j * width, i * height,
								width, height);
					} else {
						bi[j] = spritesheet.getSubimage(j * width * 2, i
								* height, width * 2, height);
					}
				}

				sprites.add(bi);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
	}

	public void init(ArrayList<Enemy> enemies) {
		this.enemies = enemies;
	}

	private void getNextPosition() {

		if (knockback) {
			dy += fallSpeed * 0.2;
			if (!falling)
				knockback = false;
			return;
		}

		if (dx != 0 || dy != 0) {
			resetPeeArc();
		}

		// movement
		if (left) {
			dx -= moveSpeed;
			if (dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		} else if (right) {
			dx += moveSpeed;
			if (dx > maxSpeed) {
				dx = maxSpeed;
			}
		} else {
			if (dx > 0) {
				dx -= stopSpeed;
				if (dx < 0) {
					dx = 0;
				}
			} else if (dx < 0) {
				dx += stopSpeed;
				if (dx > 0) {
					dx = 0;
				}
			}
		}

		// cannot attack while moving
		if ((currentAction == SCRATCHING || currentAction == FIREBALL)
				&& !(jumping || falling)) {
			dx = 0;
		}

		if (!jumping && !falling) {
			lastWalkableX = x;
			lastWalkableY = y;
			lastFacing = facingRight;
		}

		if (jumping && !falling) {
			dy = jumpStart;
			falling = true;
		}

		if (falling) {
			if (dy > 0 && gliding) {
				dy += fallSpeed * 0.05;
				if (this.y > tilemap.getHeight() + 30) {
					setGliding(false);
					hit(10);
					if (lastFacing) {
						setPosition(lastWalkableX - 20, lastWalkableY - 40);
					} else {
						setPosition(lastWalkableX + 20, lastWalkableY - 40);
					}
				}
			} else {
				dy += fallSpeed;
				if (this.y > tilemap.getHeight() + 100) {
					hit(10);
					if (lastFacing) {
						setPosition(lastWalkableX - 20, lastWalkableY - 40);
					} else {
						setPosition(lastWalkableX + 20, lastWalkableY - 40);
					}
				}
			}

			if (dy > 0) {
				jumping = false;
			}

			if (dy < 0 && !jumping) {
				dy += stopJumpSpeed;
			}

			if (dy > maxFallSpeed) {
				dy = maxFallSpeed;
			}

		}

	}

	public void update() {
		// uppdater positioner
		if (!dead) {
			getNextPosition();
			updatePosition();
			checkTileMapCollision();
			setPosition(xTemp, yTemp);

			checkAttack();
		}
		for (int i = 0; i < peeballs.size(); i++) {
			peeballs.get(i).update();
			if (peeballs.get(i).shouldRemove()) {
				peeXplosion(peeballs.get(i).getX(), peeballs.get(i).getY(),
						peeballs.get(i).getThrownFrom());
				// Sound.ballon.play();
				peeballs.remove(i);
				i--;
			}
		}

		if (currentAction == SCRATCHING) {
			if (animation.hasPlayedOnce())
				scratching = false;
		}

		checkPee();

		if (peeing) {
			if (pee > peeCost) {
				Pee p = new Pee(tilemap, facingRight, this, facingRight, null);
				p.setPosition(x, y + 6);
				peeList.add(p);
				pee -= peeCost;
				if (pee < 0)
					pee = 0;
			}
		}

		if (throwingPeeball) {
			if (peeBallCounter >= timeBetweenPeeBalls) {
				PeeBall ball = new PeeBall(tilemap, facingRight, this,
						facingRight);
				ball.setPosition(this.getX(), this.getY());
				peeballs.add(ball);
				peeBallCounter = 0;
			}
		}
		peeBallCounter++;

		if (!peeing) {

			if (pee >= maxPee) {
				pee = maxPee;
			} else {
				pee += 2;
			}
		}

		for (int i = 0; i < peeList.size(); i++) {
			peeList.get(i).update();
			if (peeList.get(i).shouldRemove()) {
				peeList.remove(i);
				i--;
			}
		}

		for (int i = 0; i < bloodList.size(); i++) {
			bloodList.get(i).update();
			if (bloodList.get(i).shouldRemove()) {
				bloodList.remove(i);
				i--;
			}
		}

		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
			if (elapsed > 1000) {
				flinching = false;
			}
		}

		// ange vilken animation som ska genomf�ras beroende p� currentAction
		if (scratching) {
			if (currentAction != SCRATCHING) {
				currentAction = SCRATCHING;
				animation.setFrames(sprites.get(SCRATCHING));
				animation.setDelay(50);
				width = 60;
			}
		}

		else if (dy > 0) {
			if (gliding) {
				if (currentAction != GLIDING) {
					currentAction = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					width = 30;
				}
			} else if (currentAction != FALLING) {
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 30;
			}
		} else if (dy < 0) {
			if (currentAction != JUMPING) {
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width = 30;
			}
		} else if (left || right) {
			if (currentAction != WALKING) {
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 30;
			}
		} else {
			if (currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 30;
			}
		}

		animation.update();

		if (drawTrajectory)
			pbTrajectory.update();

		if (currentAction != SCRATCHING && currentAction != FIREBALL) {
			if (right)
				facingRight = true;
			if (left)
				facingRight = false;
		}
	}

	public void checkPee() {
		if (pee < (maxPee / 10)) {
			long elapsed = (System.nanoTime() - peeReminderTimer) / 1000000;
			if (elapsed > peeReminderDuration) {
				this.addText("Måste ha öl för att kunna kissa mer!", x
						- (cWidth + 50), y, 2000, new Color(255, 125, 0), 0.16,
						true);
				peeReminderTimer = System.nanoTime();
			}
		}
	}

	public void draw(Graphics2D g) {
		setMapPosition();

		// draw player
		// if (flinching) {
		// long elapsed = (System.nanoTime() - flinchTimer) / 1000000;
		// if (elapsed / 100 % 2 == 0) {
		// return;
		// }
		// }
		if (!dead) {
			super.draw(g);
		}

		for (Pee p : peeList) {
			p.draw(g);
		}

		for (Blood b : bloodList) {
			b.draw(g);
		}

		for (PeeBall p : peeballs) {
			p.draw(g);
		}

		if (drawTrajectory)
			pbTrajectory.draw(g);

	}

	public void checkAttack() {
		synchronized (enemies) {
			for (Enemy e : enemies) {

				for (int i = 0; i < peeList.size(); i++) {
					if (peeList.get(i).intersects(e)) {
						if (peeList.get(i).isFromPeeBall()) {
							e.hit(peeBallDamage
									+ ((new Random().nextInt(2)) * (new Random()
									.nextInt(2) + 1))
									+ new Random().nextInt(2), peeList.get(i)
									.getFromDirectionRight());
						} else {
							e.hit(peeDamage
									+ ((new Random().nextInt(2)) * (new Random()
									.nextInt(2) + 1))
									+ new Random().nextInt(2), peeList.get(i)
									.getFromDirectionRight());
						}
						peeList.get(i).setHit();
						break;
					}
				}

				for (int i = 0; i < peeballs.size(); i++) {
					if (peeballs.get(i).intersects(e)) {
						e.hit(peeballs.get(i).getDirectHitDamage()
								+ ((new Random().nextInt(2)) * (new Random()
								.nextInt(2) + 1))
								+ new Random().nextInt(2), false);
						this.addText("Direct hit!", e.getX(), e.getY(), 400,
								new Color(255, 0, 125), 0.28);

						peeballs.get(i).setHit();
						break;
					}
				}

				if (intersects(e)) {
					if (e.contains(x, (y + (cHeight / 2)))
							|| e.contains(x - 1, (y + (cHeight / 2)))
							|| e.contains(x - 2, (y + (cHeight / 2)))
							|| e.contains(x - 3, (y + (cHeight / 2)))
							|| e.contains(x - 4, (y + (cHeight / 2)))
							|| e.contains(x - 5, (y + (cHeight / 2)))
							|| e.contains(x - 6, (y + (cHeight / 2)))
							|| e.contains(x + 1, (y + (cHeight / 2)))
							|| e.contains(x + 2, (y + (cHeight / 2)))
							|| e.contains(x + 3, (y + (cHeight / 2)))
							|| e.contains(x + 4, (y + (cHeight / 2)))
							|| e.contains(x + 5, (y + (cHeight / 2)))
							|| e.contains(x + 6, (y + (cHeight / 2)))) {
						if (falling && !gliding && !jumping && !e.falling) {
							e.hit(5000, false);
						}
					} else {
						if (!e.isDying()) {
							hit(e.getDamage());
						}
					}
				} else {
				}
			}

		}
	}

	public void hit(int damage) {
		if (dead) return;
		if (flinching)
			return;
		addText("" + damage, x, y - 10, 600, new Color(255, 0, 0));
		health -= damage;
		if (health < 0) {
			health = 0;
		}
		if (health == 0) {
			dead = true;
			peeXplosion(x, y, facingRight, new Color(0xff0000));
		}
		flinching = true;
		flinchTimer = System.nanoTime();
	}

	public void peeXplosion(double x, double y, boolean b) {
		for (int i = 0; i < 40; i++) {
			Pee p = new Pee(tilemap, b, null, facingRight, null);
			p.setPosition(x, y);
			peeList.add(p);
		}
	}

	public void peeXplosion(double x, double y, boolean b, Color color) {
		for (int i = 0; i < 1000; i++) {
			Blood bl = new Blood(tilemap, b, facingRight, color);
			bl.setPosition(x, y);
			bloodList.add(bl);
		}
	}

	public boolean getFacingRight() {
		return facingRight;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getPee() {
		return pee;
	}

	public int getMaxPee() {
		return maxPee;
	}

	public void setMaxPee(int mp) {
		maxPee = mp;
		pee = maxPee;
	}

	public boolean isMoving() {
		return left || right;
	};

	@Override
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setFiring(boolean b) {
		peeing = b;
		active = peeing;
	}

	public void setScratching() {
		scratching = true;
	}

	public void setGliding(boolean b) {
		gliding = b;
	}

	public int getLSWX() {
		return (int) lastWalkableX;
	}

	public int getLSWY() {
		return (int) lastWalkableY;
	}

	public boolean isJumping() {
		return jumping;
	}

	public boolean isGliding() {
		return gliding;
	}

	public boolean isFalling() {
		return falling;
	}

	public double getPeeArcX() {
		return peeArcX;
	}

	public void changePeeArcX(double peeArcX) {
		this.peeArcX += peeArcX;
	}

	public double getPeeArcY() {
		return peeArcY;
	}

	public void changePeeArcY(double peeArcY) {
		this.peeArcY += peeArcY;
		changePeeArcX(-peeArcY * 0.6);
	}

	public void resetPeeArc() {
		peeArcX = 0;
		peeArcY = 0;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDrawTrajectory(boolean b) {
		drawTrajectory = b;
	}

	public boolean isPeeing() {
		return peeing;
	}

	public int getExperience() {
		return experience;
	}

	public int getNextLevel() {
		return nextLevel;
	}

	public void setExperience(int amount) {
		experience += amount;
	}

	public boolean isThrowingPeeball() {
		return throwingPeeball;
	}

	public void setThrowingPeeball(boolean throwingPeeball) {
		this.throwingPeeball = throwingPeeball;
	}

	public int getTileStandingOn() {
		return standingOnTile;
	}

	public void addBeer(int amount) {
		if (pee >= maxPee)
			return;
		pee += amount;
		this.addText("+25 beer", x, y - 10, 1700, new Color(125, 255, 109),
				0.13, true);
	}

}
