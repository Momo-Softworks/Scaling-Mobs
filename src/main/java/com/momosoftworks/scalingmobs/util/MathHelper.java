package com.momosoftworks.scalingmobs.util;

public class MathHelper
{
    public static double blend(double blendFrom, double blendTo, double factor, double rangeMin, double rangeMax)
    {
        if (rangeMin > rangeMax) return blend(blendTo, blendFrom, factor, rangeMax, rangeMin);

        if (factor <= rangeMin) return blendFrom;
        if (factor >= rangeMax) return blendTo;
        return (blendTo - blendFrom) / (rangeMax - rangeMin) * (factor - rangeMin) + blendFrom;
    }
}
