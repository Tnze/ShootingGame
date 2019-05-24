package online.jdao.java;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class FrequencyLimiter {
    double last;
    double limit;

    int cps, counter;
    double lastCount;

    double getTime() {
        return glfwGetTime();
    }

    FrequencyLimiter(double limit) {
        last = getTime();
        this.limit = limit;
    }

    /**
     * Only do sth. when limit return true. Otherwise, skip it.
     *
     * @return if you should do the limited thing.
     */
    public boolean limit() {
        double now = getTime();
        if (now - last > limit) {
            last = now;

            if (now - lastCount > 1) {
                lastCount = now;
                cps = counter;
                counter = 0;
            }

            counter++;
            return true;
        }

        return false;
    }

    /**
     * @return Count per sec
     */
    public int getCPS() {
        return cps;
    }

}