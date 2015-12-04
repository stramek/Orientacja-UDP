package pl.xdcodes.stramek.orientacjaudp.algorithms;

import pl.xdcodes.stramek.orientacjaudp.UDP;

public class Complementary {

    private float[] values = new float[9];
    private double[] newRotation = new double[3];
    private float[] zbierane_dane = new float[8];
    double[] gA;

    public Complementary(float[] values, double[] newRotation) {
        this.values = values;
        this.newRotation = newRotation;
        gA = new double[3];
    }

    public Angles getRadian() {

        final double K = 0.98;
        final double dt = UDP.REFRESH_RATE / 1000.0;

        if(values != null) {
            double s1 = Math.sin(newRotation[0]);
            double s2 = Math.sin(newRotation[1]);
            double c1 = Math.cos(newRotation[0]);
            double c2 = Math.cos(newRotation[1]);
            gA[0] = ((c2 * -values[6]) + (s1 * s2 * -values[7]) + (c1 * s2 * -values[8]) + (c1 * s2 * -values[8])) / c2;
            gA[1] = ((c1 * c2 * -values[7]) - (s1 * c2 * -values[8])) / c2;
            gA[2] = ((s1 * -values[7]) + (c1 * -values[8])) / c2;
            double norm = Math.sqrt(Math.pow(values[0], 2) + Math.pow(values[1], 2) + Math.pow(values[2], 2));
            double accelBeta = Math.asin(values[0] / norm);
            double accelAlpha = -Math.atan2(values[1], values[2]);
            double magnGamma = Math.atan2((values[4] * c1) + (values[5] * s1), (values[3] * c2) + (values[4] * s1 * s2) - (values[5] * c1 * s2));
            /*newRotation[0] = K * (newRotation[0] - values[6] * dt) + (1 - K) * accelAlpha;
            newRotation[1] = K * (newRotation[1] - values[7] * dt) + (1 - K) * accelBeta;
            newRotation[2] = K * (newRotation[2] - values[8] * dt) + (1 - K) * magnGamma;*/
            newRotation[0] = K * (newRotation[0] + gA[0] * dt) + (1 - K) * accelAlpha;
            newRotation[1] = K * (newRotation[1] + gA[1] * dt) + (1 - K) * accelBeta;
            newRotation[2] = K * (newRotation[2] + gA[2] * dt) + (1 - K) * magnGamma;
        }

        float alpha = (float) newRotation[0];
        float betta = (float) newRotation[1];
        float gamma = (float) newRotation[2];

        return new Angles(alpha, betta, gamma);
    }

    public TestDanych daneDoAnalizy() {

        final double K = 0.98;
        final double dt = UDP.REFRESH_RATE / 1000.0;

        if(values != null) {
            double s1 = Math.sin(newRotation[0]);
            double s2 = Math.sin(newRotation[1]);
            double c1 = Math.cos(newRotation[0]);
            double c2 = Math.cos(newRotation[1]);
            gA[0] = ((c2 * -values[6]) + (s1 * s2 * -values[7]) + (c1 * s2 * -values[8]) + (c1 * s2 * -values[8])) / c2;
            gA[1] = ((c1 * c2 * -values[7]) - (s1 * c2 * -values[8])) / c2;
            gA[2] = ((s1 * -values[7]) + (c1 * -values[8])) / c2;
            double norm = Math.sqrt(Math.pow(values[0], 2) + Math.pow(values[1], 2) + Math.pow(values[2], 2));
            double accelBeta = Math.asin(values[0] / norm);
            double accelAlpha = -Math.atan2(values[1], values[2]);
            double magnGamma = Math.atan2((values[4] * c1) + (values[5] * s1), (values[3] * c2) + (values[4] * s1 * s2) - (values[5] * c1 * s2));

            zbierane_dane[0] = (float) newRotation[0];
            zbierane_dane[1] = (float) newRotation[1];

//            newRotation[0] = K * (newRotation[0] - values[6] * dt) + (1 - K) * accelAlpha;
//            newRotation[1] = K * (newRotation[1] - values[7] * dt) + (1 - K) * accelBeta;
//            newRotation[2] = K * (newRotation[2] - values[8] * dt) + (1 - K) * magnGamma;
            newRotation[0] = K * (newRotation[0] + gA[0] * dt) + (1 - K) * accelAlpha;
            newRotation[1] = K * (newRotation[1] + gA[1] * dt) + (1 - K) * accelBeta;
            newRotation[2] = K * (newRotation[2] + gA[2] * dt) + (1 - K) * magnGamma;

            zbierane_dane[2] = (float) newRotation[0];
            zbierane_dane[3] = (float) newRotation[1];

            zbierane_dane[4] = (float) gA[0];
            zbierane_dane[5] = (float) gA[1];

            zbierane_dane[6] = (float) dt;
            zbierane_dane[7] = (float) K;
        }

        return new TestDanych(zbierane_dane);
    }

}
