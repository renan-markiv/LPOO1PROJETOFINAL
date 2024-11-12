public class Triangulo extends Forma {
    private static final int LADO_TRIANGULO_MIN = 20;
    private static final int LADO_TRIANGULO_MAX = 60;

    private double lado;

    public Triangulo(double lado) {
        this.lado = lado;
    }

    public double calcularLadoTriangulo(double area) {
        return Math.sqrt((4 * area) / Math.sqrt(3));
    }

    public static int getTrianguloMin() {
        return LADO_TRIANGULO_MIN;
    }
    public static int getTrianguloMax() {
        return LADO_TRIANGULO_MAX;
    }
    @Override
    public double calcularArea() {
        return (Math.sqrt(3) / 4) * lado * lado; // Área de um triângulo equilátero
    }
}
