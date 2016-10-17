package com.example.karl.ltwkarl;
public final class Utilities {

    /*
    * Fetches angle relative to screen centre point
    * where 3 O'Clock is 0 and 12 O'Clock is 270 degrees
    *
            * @param screenPoint
    * @return angle in degress from 0-360.
    *
            */

    public static String getAngle(double xVector, double yVector)
    {
        // Minus to correct for coord re-mapping
        double inRads = Math.atan2(yVector, -xVector);
        // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
        if (inRads < 0)
            inRads = Math.abs(inRads);
        else
            inRads = 2*Math.PI - inRads;
        inRads = Math.toDegrees(inRads);

        if ((inRads >= 337.5 && inRads < 360) || (inRads >= 0 && inRads < 22.5)) {
            return "W";
        }
        else if (inRads >= 22.5 && inRads < 67.5) {
            return "NW";
        }
        else if (inRads >= 67.5 && inRads < 112.5) {
            return "N";
        }
        else if (inRads >= 112.5 && inRads < 157.5) {
            return "NE";
        }
        else if (inRads >= 157.5 && inRads < 202.5) {
            return "E";
        }
        else if (inRads >= 202.5 && inRads < 247.5) {
            return "SE";
        }
        else if (inRads >= 247.5 && inRads < 292.5) {
            return "S";
        }
        else if (inRads >= 292.5 && inRads < 337.5) {
            return "SW";
        }
        return "W";
    }
}