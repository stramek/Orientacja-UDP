package pl.xdcodes.stramek.orientacjaudp.algorithms;

public class Accelerometer {

    public Accelerometer() {
    }

    public Angles getRadian(float[] values) {

        float alpha = 0;
        float betta = 0;
        float gamma = 0;

        if(values[0] != 0) {
            float norm = (float)Math.sqrt(Math.pow(values[0], 2.0) + Math.pow(values[1], 2.0) + Math.pow(values[2], 2.0));
            alpha = (float)Math.asin(values[0] / norm);
            betta = (float)Math.atan2(values[1], values[2]);
        }

        return new Angles(-betta, alpha, gamma);
    }
}
