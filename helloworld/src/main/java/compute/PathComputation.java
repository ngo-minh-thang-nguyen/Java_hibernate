package compute;

import dto.BountyHunter;
import model.RouteTable;

import java.util.List;

public class PathComputation {

    private static List<BountyHunter> bountyHunters;

    public static void setBountyHunters(List<BountyHunter> bountyHunters) {
        PathComputation.bountyHunters = bountyHunters;
    }

    public static boolean bountyDay(String planet, int actualDay) {
        for (BountyHunter bountyHunter : bountyHunters) {
            if (bountyHunter.getPlanet().equals(planet) && bountyHunter.getDay() == actualDay) {
                return true;
            }
        }

        return false;
    }

    public static double DFS(List<RouteTable> routeTableList, String departure, String arrival, int autonomy, int maxAutonomy,
                             int actualDay, int countdown, int nbBounty) {
        double odd = 0F;

        for (int j = 0; j < routeTableList.size(); j++) {
            RouteTable aRouteTable = routeTableList.get(j);

            // we look for a route whose origin is our departure
            if (aRouteTable.getOrigin().equals(departure)) {

                if (bountyDay(departure, actualDay)) {
                    nbBounty++;
                }

                // the destination of the route is our arrival
                if (aRouteTable.getDestination().equals(arrival)) {

                    // the maxAutonomy allows to travel to this arrival
                    if (aRouteTable.getTravelTime() <= maxAutonomy) {

                        // the time delay allows to travel to this arrival
                        if (countdown >= aRouteTable.getTravelTime()) {
                            double caught = 0F;

                            // first case, the autonomy allows to travel to this arrival
                            if (aRouteTable.getTravelTime() <= autonomy) {
                                for (int i = 0; i < nbBounty; i++) {
                                    caught += Math.pow(9, i) / Math.pow(10, i+1);
                                }
                            }
                            // otherwise, we need to refuel, and it takes one day
                            else {
                                if (countdown - 1 < aRouteTable.getTravelTime())
                                    continue;

                                actualDay++;
                                countdown--;

                                if (bountyDay(departure, actualDay))
                                    nbBounty++;

                                for (int i = 0; i < nbBounty; i++) {
                                    caught += Math.pow(9, i) / Math.pow(10, i+1);
                                }
                            }

                            odd = Math.max(odd, 1 - caught);

                        }
                    }
                }
                // otherwise, the destination of the route is NOT our arrival, we continue this route
                else {
                    // if countdown is less than travelTime, the actual route is not feasible
                    if (countdown < aRouteTable.getTravelTime() || maxAutonomy < aRouteTable.getTravelTime()) {
                        continue;
                    }

                    // if we can get be caught by bounty hunters while arriving to arrival, we should wait at departure
                    // for 1 or many days if possible
                    int maxWaitingDays = countdown - aRouteTable.getTravelTime();
                    if (aRouteTable.getTravelTime() > autonomy)
                        maxWaitingDays = maxWaitingDays - 1;

                    int waitingDays = 0;
                    if (maxWaitingDays > 0) {
                        while (waitingDays < maxWaitingDays) {

                            // if autonomy allows to do the travel without refuelling
                            if (aRouteTable.getTravelTime() <= autonomy) {
                                odd = Math.max(odd, DFS(routeTableList, aRouteTable.getDestination(), arrival, autonomy - aRouteTable.getTravelTime(),
                                        maxAutonomy, actualDay + aRouteTable.getTravelTime() + waitingDays,
                                        countdown - aRouteTable.getTravelTime() - waitingDays,
                                        nbBounty));
                            }
                            // otherwise, autonomy does NOT allow to do the travel, we need to refuel
                            else {
                                if (countdown < aRouteTable.getTravelTime())
                                    continue;

                                if (bountyDay(departure, actualDay)) {
                                    nbBounty++;
                                }

                                odd = Math.max(odd, DFS(routeTableList, aRouteTable.getDestination(), arrival, maxAutonomy - aRouteTable.getTravelTime(),
                                        maxAutonomy, actualDay + 1 + aRouteTable.getTravelTime() + waitingDays,
                                        countdown - 1 - aRouteTable.getTravelTime() - waitingDays,
                                        nbBounty));
                            }

                            waitingDays++;
                        }
                    }





                }
            }
        }

        return odd;
    }
}
