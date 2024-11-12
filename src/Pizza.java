public class Pizza {
    private Forma forma;
    private Sabor[] sabores; // Recebe um array de Sabores
    private double precoPorCm2;

    public Pizza(Forma forma, Sabor[] sabores) {
        this.forma = forma;
        this.sabores = sabores;
        definirPrecoPorCm2();
    }

    private void definirPrecoPorCm2() {
        if (sabores.length == 1) {
            precoPorCm2 = getPrecoPorSabor(sabores[0]);
        } else if (sabores.length == 2) {
            double preco1 = getPrecoPorSabor(sabores[0]);
            double preco2 = getPrecoPorSabor(sabores[1]);
            precoPorCm2 = (preco1 + preco2) / 2;
        } else {
            throw new IllegalArgumentException("Número de sabores inválido.");
        }
    }

    private double getPrecoPorSabor(Sabor sabor) {
        switch (sabor.getTipo().toLowerCase()) {
            case "simples":
                return PrecoPizza.getInstance().getPrecoSimples();
            case "especial":
                return PrecoPizza.getInstance().getPrecoEspecial();
            case "premium":
                return PrecoPizza.getInstance().getPrecoPremium();
            default:
                throw new IllegalArgumentException("Sabor inválido.");
        }
    }

    public double calcularPreco() {
        return forma.calcularArea() * precoPorCm2;
    }

    @Override
    public String toString() {
        String tipoStr = sabores.length == 1 ? sabores[0].toString() : sabores[0] + " e " + sabores[1];
        return "\n    - Forma: " + forma.getClass().getSimpleName() +
               "\n    - Sabores: " + tipoStr +
               "\n    - Preço total: R$" + String.format("%.2f", calcularPreco());
    }
}
