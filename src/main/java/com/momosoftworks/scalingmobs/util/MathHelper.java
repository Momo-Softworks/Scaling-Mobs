package com.momosoftworks.scalingmobs.util;

import net.minecraft.util.Mth;

public class MathHelper
{
    public static double blend(double blendFrom, double blendTo, double factor, double rangeMin, double rangeMax)
    {
        if (rangeMin > rangeMax) return blend(blendTo, blendFrom, factor, rangeMax, rangeMin);

        if (factor <= rangeMin) return blendFrom;
        if (factor >= rangeMax) return blendTo;
        return (blendTo - blendFrom) / (rangeMax - rangeMin) * (factor - rangeMin) + blendFrom;
    }

    public static double blendLog(double blendFrom, double blendTo, double factor, double rangeMin, double rangeMax, double intensity)
    {
        factor = Mth.clamp(factor, rangeMin, rangeMax);

        double normalizedFactor = (factor - rangeMin) / (rangeMax - rangeMin);
        double logFactor = Math.log(intensity * normalizedFactor + 1) / Math.log(intensity + 1);

        return blendFrom + (blendTo - blendFrom) * logFactor;
    }
}
