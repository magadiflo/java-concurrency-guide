package dev.magadiflo.app.immutability;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase inmutable que representa un punto 2D.
 * Los valores se establecen en el constructor y nunca cambian.
 */
@Slf4j
public final class ImmutablePoint {
    private final int x;
    private final int y;


    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Solo getters. No existe forma de cambiar el estado
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImmutablePoint{");
        sb.append("x=").append(x);
        sb.append(", y=").append(y);
        sb.append('}');
        return sb.toString();
    }

    public static void main(String[] args) {
        ImmutablePoint point = new ImmutablePoint(5, 10);
        log.info("{}", point);
    }
}
