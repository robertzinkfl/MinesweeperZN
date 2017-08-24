MinesweeperZN
Version RC-1
By Robert Zink (rjz11@my.fsu.edu)

MinesweeperZN is a modern day version of the popular game Minesweeper
that was included with all versions of Microsoft Windows until Windows 7.

The object of the game is to correctly flag all of the mines in the grid
without detonation.

Every tile must be correctly flagged or revealed in order to win.
Revealing a mine causes a detonation - BOOM! (You lose)

Controls:
    - Left click:   Reveals a grid tile
    - Right click:  Flags/unflags a tile

If a tile is revealed that does not contain a mine,
    one of two things will happen:
        - If the tile has mines neighboring it,
          the number of neighboring mines will be displayed.
        - If the tile does not have neighboring mines,
          adjacent tiles are revealed until number squares are reached.

Any tile may be flagged, but you cannot win if safe tiles are flagged.
Flagging a tile prevents accidentally revealing it.

---------------------------------------------------------------------------

Game options:

Grid Size:
    - Small
    - Medium
    - Large

Game Difficulty (chance of tile having a mine):
    - Easy
    - Normal
    - Hard

---------------------------------------------------------------------------

Game created by Robert Zink as a final course project for COP3252
~~~~~~~ Internet Applications Programming with Java ~~~~~~~
    at Florida State University's Computer Science department
    Summer 2017, taught by Sharanya Jayaraman.

Programmed in JavaFX, based on core code presented in a YouTube tutorial
by Almas Baimagambetov. (github.com/AlmasB)

Emoji source:   emojistickers.com (non-commercial use license)
Mine image:     thenounproject.com (CC license) - Edward Boatman, US
Flag image:     icons-land.com (Free for commerical use licence)