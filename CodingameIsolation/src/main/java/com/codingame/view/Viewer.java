package com.codingame.view;

import com.codingame.game.Player;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.RoundedRectangle;
import com.codingame.gameengine.module.toggle.ToggleModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.codingame.model.Board;
import com.codingame.model.Point;
import com.codingame.parameter.Constant;

public class Viewer {

  private RoundedRectangle[][] rectangles;
  private Circle[] pawns = new Circle[2];
  private PlayerUI[] playerUIS = new PlayerUI[2];
  private int CIRCLE_COORDINATE_DELTA;

  public Viewer(
      GraphicEntityModule graphics,
      Board board,
      MultiplayerGameManager<Player> gameManager,
      ToggleModule toggleModule,
      TooltipModule tooltips) {
    int VIEWER_WIDTH = graphics.getWorld().getWidth();
    int VIEWER_HEIGHT = graphics.getWorld().getHeight();
    int height = Constant.HEIGHT;
    int width = Constant.WIDTH;
    rectangles = new RoundedRectangle[height][width];
    graphics
        .createRectangle()
        .setWidth(1920)
        .setHeight(1080)
        .setFillColor(ViewConstant.BACK_GROUND_COLOR);
    int rectangleSize = VIEWER_HEIGHT / (height + 1);
    int fontSize = rectangleSize / 2;

    int startX = (VIEWER_WIDTH - rectangleSize * width + fontSize) / 2;
    int d = 11;
    graphics
        .createRectangle()
        .setX(startX - d)
        .setY(rectangleSize / 2 - fontSize / 2 - d)
        .setWidth(width * rectangleSize + 2 * d)
        .setHeight(width * rectangleSize + 2 * d)
        .setFillColor(ViewConstant.BOARD_COLOR);

    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        int xRectangle = startX + x * rectangleSize;
        int yRectangle = rectangleSize / 2 + y * rectangleSize - fontSize / 2;
        RoundedRectangle rectangle =
            graphics
                .createRoundedRectangle()
                .setWidth(rectangleSize)
                .setHeight(rectangleSize)
                .setX(xRectangle)
                .setY(yRectangle)
                .setFillColor(ViewConstant.RECTANGLE_COLOR)
                .setLineWidth(ViewConstant.TILE_GAP)
                .setLineColor(ViewConstant.BOARD_COLOR);
        rectangles[y][x] = rectangle;
        tooltips.setTooltipText(rectangle, "(" + x + ", " + y + ")");
        if (x == 0) {
          int xText = xRectangle - (int) (rectangleSize / 1.25) + fontSize / 2;
          int yText = yRectangle + fontSize / 2;
          graphics
              .createText(Integer.toString(y))
              .setX(xText)
              .setY(yText)
              .setFillColor(ViewConstant.BOARD_COLOR)
              .setFontFamily(ViewConstant.FONT)
              .setFontSize(fontSize);
        }
        if (y == height - 1) {
          int xText = xRectangle + (int) (fontSize / 1.5);
          int yText = yRectangle + rectangleSize + fontSize / 4;
          graphics
              .createText(Integer.toString(x))
              .setX(xText)
              .setY(yText)
              .setFillColor(ViewConstant.BOARD_COLOR)
              .setFontFamily(ViewConstant.FONT)
              .setFontSize(fontSize);
        }
      }
    }
    int circleGape = ViewConstant.TILE_GAP + ViewConstant.PAWN_GAP;
    int circleRadius = rectangleSize / 2 - circleGape;
    CIRCLE_COORDINATE_DELTA = circleRadius + circleGape;
    for (int i = 0; i < 2; i++) {
      Point teamPosition = board.getTeamPosition(i);
      pawns[i] =
          graphics.createCircle().setRadius(circleRadius).setFillColor(ViewConstant.PAWN_COLORS[i]);
      setPawnPosition(i, teamPosition);
    }
    for (int i = 0; i < 2; ++i) {
      playerUIS[i] = new PlayerUI(gameManager.getPlayer(i), graphics, this, board);
    }
  }

  public void applyAction(
      GraphicEntityModule graphics,
      MultiplayerGameManager<Player> gameManager,
      Point move,
      Point tile,
      int player) {
    playerUIS[player].group.setAlpha(1);
    playerUIS[1 - player].group.setAlpha(0.5);
    playerUIS[player].update(graphics, gameManager.getPlayer(player).getMessage(), move, tile);
    graphics.commitEntityState(0, playerUIS[player].group);
    graphics.commitEntityState(0, playerUIS[1 - player].group);

    setPawnPosition(player, move);
    graphics.commitEntityState(0.4, pawns[player]);

    getRectangle(tile).setFillColor(ViewConstant.BOARD_COLOR);
    graphics.commitEntityState(0.9, getRectangle(tile));
  }

  private RoundedRectangle getRectangle(Point p) {
    return rectangles[p.getY()][p.getX()];
  }

  private void setPawnPosition(int teamId, Point teamPosition) {
    RoundedRectangle rectangle = getRectangle(teamPosition);
    pawns[teamId]
        .setX(rectangle.getX() + CIRCLE_COORDINATE_DELTA)
        .setY(rectangle.getY() + CIRCLE_COORDINATE_DELTA);
  }
}
