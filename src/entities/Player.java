package entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import main.GamePanel;

import tilemap.TileMap;

@SuppressWarnings("unused")
public class Player extends MapObject {

	private int health;
	private int maxHealth;
	private int pee;
	private int maxPee;

	private int fire;
	private int maxFire;

	private boolean dead;
	private boolean flinching;
	private long flinchTime;

	private boolean peeing;
	private int fireCost;
	private int fireBallDamage;

	private int peeCost;
	private int peeDamage;
	
	private double peeArcX;
	private double peeArcY;
	
	private int experience;
	private int nextLevel;

	private ArrayList<Pee> peeList;

	private boolean scratching;
	private int scratchDamage;
	private int scratchRange;

	private double lastWalkableX;
	private double lastWalkableY;
	private boolean lastFacing;


	private boolean gliding;

	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {2, 8, 1, 2, 4, 2, 5 };

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
		cWidth = 20;
		cHeight = 20;

		moveSpeed = 0.2;
		maxSpeed = 1.9;
		stopSpeed = 0.3;
		fallSpeed = 0.09;
		maxFallSpeed = 4.2;
		jumpStart = -5.2;
		stopJumpSpeed = 0.3;
		
		peeArcX = 0;
		peeArcY = 0;
		
		experience = 0;
		nextLevel = 500;
		
		dead = false;

		facingRight = true;

		health = maxHealth = 100;
		pee = maxPee = 25000;

		peeCost = 30;
		peeDamage = 2;
		peeList = new ArrayList<Pee>();

		scratchDamage = 8;
		scratchRange = 40;

		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/playersprites3.png"));
			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 7; i++) {
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for (int j = 0; j < numFrames[i]; j++) {
					if (i != 6) {
						bi[j] = spritesheet.getSubimage(j * width, i * height, width, height);
					} else {
						bi[j] = spritesheet.getSubimage(j * width * 2, i * height, width * 2, height);
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

	private void getNextPosition() {
		
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
		if ((currentAction == SCRATCHING || currentAction == FIREBALL) && !(jumping || falling)) {
			dx = 0;
		}

		if (!jumping && !falling) {
			lastWalkableX = x;
			lastWalkableY = y;
			lastFacing = facingRight;
		}

		if (jumping  && !falling) {
			dy = jumpStart;
			falling = true;
		}

		if (falling) {
			if (dy > 0 && gliding) {
				dy += fallSpeed * 0.05;
				if (this.y > tilemap.getHeight() + 30) {
					setGliding(false);
					if (lastFacing) {
						setPosition(lastWalkableX - 20, lastWalkableY - 40);
					} else {
						setPosition(lastWalkableX + 20, lastWalkableY - 40);
					}
				}
			} else {
				dy += fallSpeed;
				if (this.y > tilemap.getHeight() + 100) {
					if (lastFacing) {
						setPosition(lastWalkableX - 20, lastWalkableY - 40);
					} else {
						setPosition(lastWalkableX + 20, lastWalkableY - 40);
					}
				}
			}

			if (dy > 0) jumping = false;
			if (dy < 0 && !jumping) dy += stopJumpSpeed;

			if (dy > maxFallSpeed) dy = maxFallSpeed;

		}

	}

	public void update() {
		// uppdater positioner
		getNextPosition();
		checkTileMapCollision();
		setPosition(xTemp, yTemp);

		// check attack has stopped

		if (currentAction == SCRATCHING) {
			if (animation.hasPlayedOnce()) scratching = false;
		}

		// eldboll

		if (peeing) {
			if (pee > peeCost) {
				Pee p = new Pee(tilemap, facingRight, this);
				p.setPosition(x, y + 6);
				peeList.add(p);
				pee -= peeCost;
				if (pee < 0) pee = 0;
			}
		}

		if (!peeing) {
			
			if (pee > maxPee) {
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

		// ange vilken animation som ska genomf�ras beroende p� currentAction
		if (scratching) {
			if (currentAction != SCRATCHING) {
				currentAction = SCRATCHING;
				animation.setFrames(sprites.get(SCRATCHING));
				animation.setDelay(50);
				width = 60;
			}
		} 
		//		else if (firing) {
		//			if (currentAction != FIREBALL) {
		//				currentAction = FIREBALL;
		//				animation.setFrames(sprites.get(FIREBALL));
		//				animation.setDelay(100);
		//				width = 30;
		//			}
		//		} 
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

		if (currentAction != SCRATCHING && currentAction != FIREBALL) {
			if (right) facingRight = true;
			if (left) facingRight = false;
		}
	}

	public void draw(Graphics2D g) {
		setMapPosition();

		// draw player
		if (flinching) {
			long elapsed = (System.nanoTime() - flinchTime) / 1000000;
			if (elapsed / 100 % 2 == 0) {
				return;
			}
		}

		super.draw(g);

		for (Pee p : peeList) {
			p.draw(g);
		}

	}

	public void checkAttack(ArrayList<Enemy> enemies) {
		synchronized (enemies) {
			for (Enemy e : enemies) {
		
				// scratch
				if (scratching) {
					if (facingRight) {
						if (e.getX() > x && e.getX() < x + scratchRange &&
								e.getY() > y - height / 2 &&
								e.getY() < y + height / 2) {
							e.hit(scratchDamage);
						}
					} else {
						if (e.getX() < x && e.getX() > x - scratchRange &&
								e.getY() > y - height / 2 &&
								e.getY() < y + height / 2) {
							e.hit(scratchDamage);
						}
					}
				}

				for (int i = 0; i < peeList.size(); i++) {
					if (peeList.get(i).intersects(e)) {
						e.hit(peeDamage + ((new Random().nextInt(2)) * (new Random().nextInt(2) + 1)) + new Random().nextInt(2));
						peeList.get(i).setHit();
						break;
					}
				}

			}
		}




	}
	
	public int getHealth() {return health;}
	public int getMaxHealth() {return maxHealth;}
	public int getPee() {return pee;}
	public int getMaxPee() {return maxPee;}
	public boolean isMoving() {return left || right;};

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
	
	public boolean isPeeing() {return peeing;}
	
	public int getExperience() {return experience;}
	public int getNextLevel() {return nextLevel;}
	public void setExperience(int amount) {experience += amount;}
	

}
