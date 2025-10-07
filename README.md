# Batalja Bot – Decision Tree AI Player

This project is an AI bot developed for the **Batalja** strategy game, created as part of a competitive event at UP FAMNIT.
The bot uses a **decision tree algorithm (J48 in Weka)** trained on data from over 500 recorded games to decide its in-game actions.

## Overview

Initially, a reinforcement learning approach was considered, but due to the huge number of possible states, a **supervised decision tree model** was used instead.
Game logs were parsed into a dataset, trained in Weka, and the resulting tree was manually implemented as `if-else` logic inside the bot.

## Decision Logic

For the first 50 turns, the bot focuses on expanding by attacking the closest neutral planets.
After that, it follows the trained decision tree logic based on attributes such as:

* `fleetReinforced`
* `numFleetReinforced`
* `turnsPlayed`
* `largestAttack`

These attributes determine whether the bot should **attack the nearest enemy** or **reinforce its nearest allied planet**.

The final J48 model achieved **94.25% accuracy** using 10-fold cross-validation.

## Code

The main implementation is in `Player.java`, which:

* Parses game state input each turn.
* Tracks planets, fleets, and performance statistics.
* Executes actions according to the decision tree outcomes.
