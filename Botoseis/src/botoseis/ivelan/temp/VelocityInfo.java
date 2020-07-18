/*
 * VelocityInfo.java
 *
 * Copyright (c) 2010-2010 CPGeo, LTDA. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of 
 * Centro Potiguar de Geociências LTDA - CPGeo. You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with CPGeo.
 *
 * CPGeo MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. CPGEO SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * Created: 1 September 2010
 * Last modified: 6 September 2010
 * 
 *
 */

package botoseis.ivelan.temp;

/**
 * This auxiliary class hold velocity picks coordinates.
 *
 */
public class VelocityInfo {
    public VelocityInfo(float t, float v){
	time = t;
	vel = v;
    }
    
    float time;  /** Time value. */
    float vel;   /** Velocity value. */
    
    // The variables px and py are the coordinate values, in pixels, of
    // the point given by the coordinates (vel, time). They are used
    // to check for picks selection over the velocity graphics panel.
    int px;      /** Position X (pixels) on the velocities graphics panel. */
    int py;      /** Position Y (pixels) on the velocities graphics panel. */
}

