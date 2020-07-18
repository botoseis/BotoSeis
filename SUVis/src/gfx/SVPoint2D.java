/*
 * SVPoint2D.java
 *
 * Created on January 10, 2008, 12:41 PM
 * 
 * Project: BotoSeis
 *
 * Federal University of Para.
 * Department of Geophysics
 */
package gfx;

/**
 *
 * @author Williams Lima
 */
public class SVPoint2D {
    public int ix; /** X in window coordinates. */
    public int iy; /** Y in window coordinates. */
    public float fx; /** X in real user data coordinates. */
    public float fy;/** Y in real user data coordiantes. */

    @Override
    public String toString() {
        return "x: "+ix+" y: "+iy+" fx: "+fx+" fy: "+fy;
    }
}
