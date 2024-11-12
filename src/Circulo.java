public class Circulo extends Forma {

    private static final int LADO_CIRCULO_MIN = 7;
    private static final int LADO_CIRCULO_MAX = 23;

    private double raio;

    public Circulo(double raio) {
        this.raio = raio;
    }

    public double calcularRaioCirculo(double area) {
        return Math.sqrt(area / Math.PI);
    }

    public static int getCirculoMin() {
        return LADO_CIRCULO_MIN;
    }

    public static int getCirculoMax() {
        return LADO_CIRCULO_MAX;
    }
    @Override
    public double calcularArea() {
        return Math.PI * raio * raio;
    }
}
