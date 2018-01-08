
package robotrace;

/**
 * Implementation of RaceTrack, creating a track from control points for a 
 * cubic Bezier curve
 */
public class BezierTrack extends RaceTrack {
    
    private Vector[] controlPoints;

    
    BezierTrack(Vector[] controlPoints) {
        this.controlPoints = controlPoints;
        


    }
    
    @Override
    protected Vector getPoint(double t) {
        Vector P0,P1,P2,P3;
        t *= 4;
        t %= 4;
        int segment = (int) Math.floor(t);
        t -= Math.floor(t);
        switch(segment){
            case 0:
                P0 = controlPoints[0];
                P1 = controlPoints[1];
                P2 = controlPoints[2];
                P3 = controlPoints[3];
                return getCubicBezierPnt(t,P0,P1,P2,P3);  
            case 1:
                P0 = controlPoints[4];
                P1 = controlPoints[5];
                P2 = controlPoints[6];
                P3 = controlPoints[7];
                return getCubicBezierPnt(t,P0,P1,P2,P3);
            case 2:
                P0 = controlPoints[8];
                P1 = controlPoints[9];
                P2 = controlPoints[10];
                P3 = controlPoints[11];
                return getCubicBezierPnt(t,P0,P1,P2,P3);
            case 3:
                P0 = controlPoints[12];
                P1 = controlPoints[13];
                P2 = controlPoints[14];
                P3 = controlPoints[15];
                return getCubicBezierPnt(t,P0,P1,P2,P3);
            default:
                return new Vector(0,0,0);
        }
    }

    @Override
    protected Vector getTangent(double t){
        Vector P0,P1,P2,P3;
        t *= 4;
        t %= 4;
        int segment = (int) Math.floor(t);
        t -= Math.floor(t);
        switch(segment){
            case 0:
                P0 = controlPoints[0];
                P1 = controlPoints[1];
                P2 = controlPoints[2];
                P3 = controlPoints[3];
                return getCubicBezierTng(t,P0,P1,P2,P3);  
            case 1:
                P0 = controlPoints[4];
                P1 = controlPoints[5];
                P2 = controlPoints[6];
                P3 = controlPoints[7];
                return getCubicBezierTng(t,P0,P1,P2,P3);
            case 2:
                P0 = controlPoints[8];
                P1 = controlPoints[9];
                P2 = controlPoints[10];
                P3 = controlPoints[11];
                return getCubicBezierTng(t,P0,P1,P2,P3);
            case 3:
                P0 = controlPoints[12];
                P1 = controlPoints[13];
                P2 = controlPoints[14];
                P3 = controlPoints[15];
                return getCubicBezierTng(t,P0,P1,P2,P3);
            default:
                return new Vector(0,0,0);
        }
    }
}
