# Deadwood: "The Cheapass Game of Acting Badly"

---
### Authors:

---
* Andrew Cox
* Peter Hafner

### Course:

---

* CSCI 345: Object Oriented Design
* Western Washington University
* _Spring 2023_

---

## Introduction:

---

Deadwood is a fast-paced board game about actors, acting, and the thrill-filled life of a
wandering bit player. It's perfect for 2 to 6 players, and still decent for 7 or 8. Play
time is about 60 minutes.

---

## Compiling and Running:

---

To compile the game from the terminal, navigate to Deadwood and run
the following command:

```javac *.java```

To run the game from the terminal, run the following command:

```java Deadwood```

To compile and run the game from an IDE, use your IDE's built-in run command or run button.

The external files used for initial game setup have been converted to input streams, which
should allow them to be correctly read in from the terminal or an IDE.

If issues arise while attempting to run the game via a build tool or IDE, please first attempt
compiling and running via the terminal. If issues persist, please contact the authors.

---

## Gameplay:

---

When the game has started, the user will be prompted to select the number of players from a dropdown menu.
If the user wishes to exit the game without starting, they may simply click the "Cancel" button provided in 
the game setup prompt. 

Once the desired number of players has been selected, clicking the "Start Game" button will begin the game 
setup. The user will be given the option to rename the players or keep the default names. This selection 
is entirely discretionary and may be bypassed by clicking "No" when prompted to rename the players. If "Yes" 
is selected, the user will be prompted to enter a name for each player. 

Once player names have either been entered or bypassed, the game will begin and the board will be populated.

The current player's stats will be displayed at the top of the screen. The current player's name will be 
displayed in the same color as their player token. The remaining stats will be displayed underneath in 
black text, and a tracker for the number of days remaining will be displayed directly underneath the stats.

The game is designed to restrict the players actions to only those that are allowed by the rules of the game. 
Action buttons will be enabled or disabled based on the current player's location and the actions they have 
already taken. If a button is disabled, it will be grayed out and unclickable. If a button is enabled, it will
be colored and clickable.

When ```End Turn``` is the only available action remaining, select it to end the current player's turn. The 
next player will then be prompted to begin their turn.

Upon reaching the Casting Office, the player will be allowed to upgrade their rank. As long as a player is 
on the Casting Office location, the ```Upgrade``` button will be enabled. When the button is clicked, the
player will be prompted to select a rank to upgrade to. The player will be allowed to select any rank that
is higher than their current rank. If the player does not have enough money to upgrade to the selected rank,
the upgrade will not be allowed. If the player has enough money to upgrade, the upgrade will be allowed and
the player's rank will be updated. The player's dollars or credits will be deducted by the cost of the upgrade.

The game will continue until all rounds (days) have been completed. At the end of the game, the scores will 
be calculated and displayed. When the "OK" button is clicked, the game will close. 

---

## Gameplay Restrictions:

---

* If a player is at the Trailer, their only option is to ```Move```.
* If a player is at the Casting Office, they may ```Upgrade``` and/or ```Move```.
* If a player is at a Set location and have not yet taken a role, they may ```Take Role``` if they so choose.
* If a player is at a Set location and has taken a role:
    * If the role is a starring role (on the card), they may ```Rehearse``` or ```Act```.
    * If the role is an extra role (off the card), they may only ```Act```.

---

## Universal Functions:

---

If at any time during the game the user wishes to exit and quit, they may do so by simply clicking the "X"
in the top right corner of the window. A confirmation prompt will appear, allowing the user to confirm or
cancel their exit.

At any time during gameplay, the user may hover their cursor over the player's stats to temporarily display
the current standings (scoreboard), showing the player's name and their current score.

As the card images are small and difficult to read, the user may hover their cursor over a card to display
a larger version of the card. The card will remain displayed until the cursor is moved away from the card.
_(This functionality is disabled when the scene has wrapped and the card has been flipped.)_

---

## Known Issues:

---

* On smaller monitors, the enlarged card image may extend beyond the bounds of the screen.