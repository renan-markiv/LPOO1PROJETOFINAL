public class Quadrado extends Forma {
    private double lado;
    private static final int LADO_QUADRADO_MIN = 10;
    private static final int LADO_QUADRADO_MAX = 40;

    public Quadrado(double lado) {
        this.lado = lado;
    }

    public double calcularLadoQuadrado(double area) {
        return Math.sqrt(area);
    }    

    public static int getQuadradoMin() {
        return LADO_QUADRADO_MIN;
    }

    public static int getQuadradoMax() {
        return LADO_QUADRADO_MAX;
    }

    @Override
    public double calcularArea() {
        return lado * lado;
    }
}
