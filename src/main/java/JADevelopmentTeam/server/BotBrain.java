package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;
import java.util.Random;

public abstract class BotBrain {
    public static DataPackage getOptimalMove(GameManager gameManager, boolean isBlack) {
        GameManager backup = gameManager.copy();
        ArrayList<Intersection> possibleMoves = getPossibleMoves(gameManager, isBlack, backup);
        if (possibleMoves.size() == 0)
            return getPassDataPackage();
        return getIntersectionDataPackage(getRandomMove(possibleMoves));
    }

    private static Intersection getRandomMove(ArrayList<Intersection> possibleMoves) {
        Random r = new Random();
        int randomIndex = r.nextInt(possibleMoves.size());
        return possibleMoves.get(randomIndex);
    }

    private static DataPackage getPassDataPackage() {
        return new DataPackage(null, DataPackage.Info.Pass);
    }

    private static DataPackage getIntersectionDataPackage(Intersection intersection) {
        return new DataPackage(intersection, DataPackage.Info.Stone);
    }

    public static ArrayList<Intersection> getPossibleMoves(GameManager gameManager, boolean isBlack, GameManager backup) {
        ArrayList<Intersection> possibleMoves = new ArrayList<>();
        int turn = 0;
        if (isBlack) turn = 1;
        for (int i = 0; i < gameManager.getBoard().getSize(); i++) {
            for (int j = 0; j < gameManager.getBoard().getSize(); j++) {
                Intersection intersection = new Intersection(i, j);
                if (gameManager.processMove(intersection,turn) == 0) {
                    possibleMoves.add(intersection);
                }
                gameManager.loadBackup(backup.copy());

            }
        }
        return possibleMoves;
    }
}
