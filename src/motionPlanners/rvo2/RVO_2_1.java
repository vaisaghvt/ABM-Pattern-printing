package motionPlanners.rvo2;

import agent.Agent;
import utility.Line;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import motionPlanners.VelocityCalculator;
import sim.util.Bag;
import utility.Geometry;

/**
 * TWContextBuilder
 *
 * @author michaellees, vaisagh Created: Dec 1, 2010
 *
 * Copyright michaellees
 *
 *
 * Description: implementation of RVO2 library
 *
 */
public class RVO_2_1 implements VelocityCalculator {

    /**
     * Stores the orcalines for calculation
     */
    List<Line> orcaLines;
    /**
     * TIME_HORIZON float (time) The minimal amount of time for which the
     * agent's velocities that are computed by the simulation are safe with
     * respect to other agents. The larger this number, the sooner this agent
     * will respond to the presence of other agents, but the less freedom the
     * agent has in choosing its velocities. Must be positive.
     */
    public static double TIME_HORIZON =2.0;


    /* Search for the best new velocity. */
    public RVO_2_1() {
        super();
        orcaLines = new ArrayList<Line>();
    }

    public List<Line> getOrcaLines() {
        return orcaLines;
    }

    @Override
    public Vector2d calculateVelocity(Agent me,
            Bag guys, Vector2d preferredVelocity, double timeStep) {


//        preferredVelocity.normalize();
        orcaLines.clear();





        final Point2d agentPosition = new Point2d(me.getCurrentPosition());


        Vector2d newVelocity = new Vector2d(preferredVelocity);




        final double invTimeHorizon = 1.0f / TIME_HORIZON;

        /* Create agent ORCA lines. */
        for (int i = 0; i < guys.size(); i++) {
            final Agent otherAgent = (Agent) (guys.get(i));
            if (otherAgent.equals(me)) {
                continue;
            }

            Vector2d relativePosition = new Vector2d(otherAgent.getCurrentPosition());
            relativePosition.sub(agentPosition);


            Vector2d relativeVelocity = new Vector2d(me.getVelocity());
            relativeVelocity.sub(otherAgent.getVelocity());

            double distSq = relativePosition.dot(relativePosition);
            double combinedRadius = me.getRadius() + otherAgent.getRadius();

            double combinedRadiusSq = Math.pow(combinedRadius, 2.0f);

            Line line = new Line();
            Vector2d u;

            if (distSq > combinedRadiusSq) {
                /* No collision. */
                Vector2d w = new Vector2d(relativePosition);
                w.scale(invTimeHorizon);
                w.sub(relativeVelocity);
                w.negate();

                /* Vector from cutoff center to relative velocity. */
                final double wLengthSq = w.dot(w);

                final double dotProduct1 = w.dot(relativePosition);

                if (dotProduct1 < 0.0f && Math.pow(dotProduct1, 2.0f) > combinedRadiusSq * wLengthSq) {
                    /* Project on cut-off circle. */
                    final double wLength = Math.sqrt(wLengthSq);
                    Vector2d unitW = new Vector2d(w);
                    unitW.scale(1.0f / wLength);


                    line.direction = new Vector2d(unitW.y, -unitW.x);
                    u = new Vector2d(unitW);
                    u.scale((combinedRadius * invTimeHorizon) - wLength);
                } else {
                    /* Project on legs. */

//                    final double LEG = ((distSq - combinedRadiusSq) > 0) ? Math.sqrt(distSq - combinedRadiusSq) : 0;
                    final double LEG = Math.sqrt(Math.abs(distSq - combinedRadiusSq));

                    if (Geometry.det(relativePosition, w) > 0.0f) {
                        /* Project on left LEG. */

                        line.direction = new Vector2d(
                                relativePosition.x * LEG - relativePosition.y * combinedRadius,
                                relativePosition.x * combinedRadius + relativePosition.y * LEG);
                        line.direction.scale(1.0f / distSq);
                    } else {
                        /* Project on right LEG. */

                        line.direction = new Vector2d(
                                relativePosition.x * LEG + relativePosition.y * combinedRadius,
                                -relativePosition.x * combinedRadius + relativePosition.y * LEG);
                        line.direction.scale(-1.0f / distSq);
                    }

                    final double dotProduct2 = relativeVelocity.dot(line.direction);
                    u = new Vector2d(line.direction);
                    u.scale(dotProduct2);
                    u.sub(relativeVelocity);

                }
            } else {
                /* Collision. */
//                System.out.println("Collision!!!");

                final double invTimeStep = 1.0f / timeStep;
                assert relativePosition.length() != 0;
                Vector2d w = new Vector2d(relativePosition);
                w.scale(invTimeStep);
                w.sub(relativeVelocity);

                w.negate();

                double wLength = w.length();

                Vector2d unitW = new Vector2d(w);
                unitW.scale(1.0 / wLength);
                assert wLength != 0;

                line.direction = new Vector2d(unitW.y, -unitW.x);
                u = new Vector2d(unitW);
                u.scale((combinedRadius * invTimeStep) - wLength);
                assert !Double.isNaN(
                        line.direction.y) && !Double.isInfinite(line.direction.y);


            }
            Vector2d newU = new Vector2d(u);
            newU.scale(0.5f);
            newU.add(me.getVelocity());

            line.point = new Point2d(newU);

            orcaLines.add(line);
            assert !Double.isNaN(
                    line.direction.y) && !Double.isInfinite(line.direction.y);
            assert !Double.isNaN(
                    line.direction.x) && !Double.isInfinite(line.direction.x);
            assert !Double.isNaN(
                    line.point.y) && !Double.isInfinite(line.point.y);
            assert !Double.isNaN(
                    line.point.x) && !Double.isInfinite(line.point.x);



        }
        //These function should return the new velocity based on linear programming solution

        int lineFail = linearProgram2(orcaLines, me.getMaxSpeed(), preferredVelocity, false, newVelocity);

        if (lineFail < orcaLines.size()) {
            linearProgram3(orcaLines, 0, lineFail, me.getMaxSpeed(), newVelocity);
        }

        if (Double.isNaN(
                newVelocity.x) || Double.isInfinite(newVelocity.x)) {
            System.out.println(orcaLines.size());
        }

        assert !Double.isNaN(
                newVelocity.x) && !Double.isInfinite(newVelocity.x);
        assert !Double.isNaN(
                newVelocity.y) && !Double.isInfinite(newVelocity.y);
        return newVelocity;

    }



    boolean linearProgram1(List<Line> lines, int lineNo, double radius, Vector2d optVelocity, boolean directionOpt, Vector2d result) {


        Vector2d lineNoPoint = new Vector2d(lines.get(lineNo).point);
        Vector2d lineNoDirection = new Vector2d(lines.get(lineNo).direction);
        double dotProduct = lineNoPoint.dot(lineNoDirection);
        assert !Double.isNaN(
                lineNoDirection.y) && !Double.isInfinite(lineNoDirection.y);
        assert !Double.isNaN(
                lineNoPoint.x) && !Double.isInfinite(lineNoPoint.x);

        //   final double detProduct = det(lines.get(lineNo).direction, lineNoPoint);
        //final double detProduct2 = lineNoPoint.dot(lineNoPoint);
        final double discriminant = Math.pow(dotProduct, 2.0) + Math.pow(radius, 2.0f) - lineNoPoint.dot(lineNoPoint);

        if (Double.compare(discriminant, Geometry.EPSILON) < 0) {
            /* Max speed circle fully invalidates line lineNo. */
            return false;
        }

        final double sqrtDiscriminant = Math.sqrt(discriminant);
        double tLeft = -(dotProduct) - sqrtDiscriminant;
        double tRight = -(dotProduct) + sqrtDiscriminant;

        for (int i = 0; i < lineNo; ++i) {
            final double denominator = Geometry.det(lineNoDirection, lines.get(i).direction);

            Vector2d tempVector = new Vector2d(lineNoPoint);
            tempVector.sub(new Vector2d(lines.get(i).point));
            final double numerator = Geometry.det(lines.get(i).direction, tempVector);

            if (Double.compare(
                    Math.abs(denominator), Geometry.EPSILON) <= 0) {
                /* Lines lineNo and i are (almost) parallel. */

                if (Double.compare(numerator, Geometry.EPSILON) < 0) {
                    /* Line i fully invalidates line lineNo. */
                    return false;
                } else {
                    /* Line i does not impose constraint on line lineNo. */
                    continue;
                }
            }

            final double t = numerator / denominator;
            if (denominator >= 0) {
                /* Line i bounds line lineNo on the right. */
                tRight = Math.min(tRight, t);
            } else {
                /* Line i bounds line lineNo on the left. */
                tLeft = Math.max(tLeft, t);
            }

            if (tLeft > tRight) {
                return false;
            }
        }

        if (directionOpt) {
            /* Optimize direction. */
            Vector2d tempLineNoDirection = new Vector2d(lineNoDirection);
            if (Double.compare(optVelocity.dot(tempLineNoDirection), -Geometry.EPSILON) > 0) {
                /* Take right extreme. */
                tempLineNoDirection.scale(tRight);
            } else {
                /* Take left extreme. */
                tempLineNoDirection.scale(tLeft);
            }
            tempLineNoDirection.add(new Vector2d(lineNoPoint));
            result.x = tempLineNoDirection.x;
            result.y = tempLineNoDirection.y;
            assert !Double.isNaN(
                    result.x) && !Double.isInfinite(result.x);
            assert !Double.isNaN(
                    result.y) && !Double.isInfinite(result.y);
        } else {
            /* Optimize closest point. */

            assert !Double.isNaN(
                    optVelocity.x) && !Double.isInfinite(optVelocity.x);
            assert !Double.isNaN(
                    optVelocity.y) && !Double.isInfinite(optVelocity.y);
            assert !Double.isNaN(
                    lineNoDirection.x) && !Double.isInfinite(lineNoDirection.x);
            assert !Double.isNaN(
                    lineNoDirection.y) && !Double.isInfinite(lineNoDirection.y);
            assert !Double.isNaN(
                    lineNoPoint.x) && !Double.isInfinite(lineNoPoint.x);
            assert !Double.isNaN(
                    lineNoPoint.y) && !Double.isInfinite(lineNoPoint.y);
            Vector2d tempOptVector = new Vector2d(optVelocity);
            tempOptVector.sub(lineNoPoint);
            final double t = lineNoDirection.dot(tempOptVector);


            assert !Double.isNaN(t) && !Double.isInfinite(t);
            assert !Double.isNaN(tLeft) && !Double.isInfinite(tLeft);
            assert !Double.isNaN(tRight) && !Double.isInfinite(tRight);


            Vector2d tempLineNoDirection = new Vector2d(lineNoDirection);
            if (Double.compare(t, tLeft) < 0) {
                tempLineNoDirection.scale(tLeft);
            } else if (Double.compare(t, tRight) > 0) {
                tempLineNoDirection.scale(tRight);
            } else {
                tempLineNoDirection.scale(t);
            }
            tempLineNoDirection.add(new Vector2d(lineNoPoint));
            result.x = tempLineNoDirection.x;
            result.y = tempLineNoDirection.y;
            assert !Double.isNaN(
                    result.x) && !Double.isInfinite(result.x);
            assert !Double.isNaN(
                    result.y) && !Double.isInfinite(result.y);

        }

        return true;
    }

    int linearProgram2(List<Line> lines, double radius, Vector2d optVelocity, boolean directionOpt, Vector2d result) {


        if (directionOpt) {
            /*
             * Optimize direction. Note that the optimization velocity is of unit
             * length in this case.
             */
            if (Double.compare(Math.abs(optVelocity.length() - 1), Geometry.EPSILON) > 0) {
//                System.out.println("what?? how??");
            }
            Vector2d tempOpt = new Vector2d(optVelocity);

            result.x = tempOpt.x;
            result.y = tempOpt.y;
            result.scale(radius);
            assert !Double.isNaN(
                    result.x) && !Double.isInfinite(result.x);
            assert !Double.isNaN(
                    result.y) && !Double.isInfinite(result.y);
        } else if (optVelocity.dot(optVelocity) > Math.pow(radius, 2.0f)) {
            /* Optimize closest point and outside circle. */

            result.x = optVelocity.x;
            result.y = optVelocity.y;
            result.normalize();//mhl: why normalize

            result.scale(radius);
            assert !Double.isNaN(
                    result.x) && !Double.isInfinite(result.x);
            assert !Double.isNaN(
                    result.y) && !Double.isInfinite(result.y);
        } else {
            /* Optimize closest point and inside circle. */

            result.x = optVelocity.x;
            result.y = optVelocity.y;
            assert !Double.isNaN(
                    result.x) && !Double.isInfinite(result.x);
            assert !Double.isNaN(
                    result.y) && !Double.isInfinite(result.y);
        }

        for (int i = 0; i < lines.size(); ++i) {

            Vector2d tempPoint = new Vector2d(lines.get(i).point);
            tempPoint.sub(new Vector2d(result));


            if (Double.compare(
                    Geometry.det(lines.get(i).direction, tempPoint), 0) > 0) {
                /* Result does not satisfy constraint i. Compute new optimal result. */
                Vector2d tempResult = new Vector2d(result);
                if (!linearProgram1(lines, i, radius, optVelocity, directionOpt, result)) {
                    result.x = tempResult.x;
                    result.y = tempResult.y;
                    assert !Double.isNaN(
                            result.x) && !Double.isInfinite(result.x);
                    assert !Double.isNaN(
                            result.y) && !Double.isInfinite(result.y);
                    return i;
                }
            }
        }

        return lines.size();
    }

    void linearProgram3(List<Line> lines, int numObstLines, int beginLine, double radius, Vector2d result) {

        double distance = 0.0f;

        for (int i = beginLine; i < lines.size(); i++) {
            Vector2d tempPoint = new Vector2d(lines.get(i).point);
            tempPoint.sub(result);

            if (Geometry.det(lines.get(i).direction, tempPoint) > distance) {
                /* Result does not satisfy constraint of line i. */
                List<Line> projLines = new ArrayList<Line>();
                for (int j = 0; j < numObstLines; j++) {
                    projLines.add(new Line(lines.get(j)));

                }

                for (int j = numObstLines; j < i; j++) {
                    Line line = new Line();

                    double determinant = Geometry.det(lines.get(i).direction, lines.get(j).direction);
                    if (Double.compare(Math.abs(determinant), Geometry.EPSILON) <= 0) {
                        /* Line i and line j are (almost) parallel. */
                        if (Double.compare(lines.get(i).direction.dot(lines.get(j).direction), -Geometry.EPSILON) > 0) {
                            /* Line i and line j point in the same direction. */
                            continue;
                        } else {
                            /* Line i and line j point in opposite direction. */
                            line.point = new Point2d(lines.get(j).point);
                            line.point.add(lines.get(i).point);
                            line.point.scale(0.5f);

                        }
                    } else {

                        Vector2d tempVector = new Vector2d(lines.get(i).point);
                        tempVector.sub(new Vector2d(lines.get(j).point));
                        Vector2d newTempVector = new Vector2d(lines.get(i).direction);
                        newTempVector.scale(Geometry.det(lines.get(j).direction, tempVector) / determinant);

                        line.point = new Point2d(lines.get(i).point);
                        line.point.add(newTempVector);


                    }
                    line.direction = new Vector2d(lines.get(j).direction);
                    line.direction.sub(lines.get(i).direction);
                    line.direction.normalize();

                    projLines.add(line);
                }

                final Vector2d tempResult = new Vector2d(result);

                if (linearProgram2(projLines, radius, new Vector2d(-lines.get(i).direction.y, lines.get(i).direction.x), true, result) < projLines.size()) {
                    /* This should in principle not happen.  The result is by definition
                     * already in the feasible region of this linear program. If it fails,
                     * it is due to small floating point error, and the current result is
                     * kept.
                     */
//
                    result.x = tempResult.x;
                    result.y = tempResult.y;

//                    result.x = 0.0f;
//                    result.y = 0.0f;

                }

                Vector2d tempVector = new Vector2d(lines.get(i).point);
                tempVector.sub(result);
                distance = Geometry.det(lines.get(i).direction, tempVector);
            }
        }
    }
}
