
package robotrace;

/**
 * Implementation of RaceTrack, creating a track from a parametric formula
 */
public class ParametricTrack extends RaceTrack {
    
    @Override
    protected Vector getPoint(double t) {
        return new Vector(10*Math.cos(2*Math.PI*t),14*Math.sin(2*Math.PI*t),1);

    }

    @Override
    protected Vector getTangent(double t) {
        Vector tangent = new Vector(-10*2*Math.PI*Math.sin(2*Math.PI*t),14*2*Math.PI*Math.cos(2*Math.PI*t),1).normalized(); 
        return tangent;
    }
}
