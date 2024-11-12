public class PrecoPizza {
    private static PrecoPizza instance;
    private double precoSimples;
    private double precoEspecial;
    private double precoPremium;

    // Construtor privado para o Singleton
    private PrecoPizza() {
        // Valores iniciais dos preços por centímetro quadrado (em reais)
        this.precoSimples = 0.05; // Exemplo: R$ 0.05 por cm²
        this.precoEspecial = 0.08; // Exemplo: R$ 0.08 por cm²
        this.precoPremium = 0.12; // Exemplo: R$ 0.12 por cm²
    }

    // Método estático para obter a instância única (Singleton)
    public static PrecoPizza getInstance() {
        if (instance == null) {
            instance = new PrecoPizza();
        }
        return instance;
    }

    // Métodos para obter os preços
    public double getPrecoSimples() {
        return precoSimples;
    }

    public double getPrecoEspecial() {
        return precoEspecial;
    }

    public double getPrecoPremium() {
        return precoPremium;
    }

    // Métodos para atualizar os preços
    public void setPrecoSimples(double precoSimples) {
        this.precoSimples = precoSimples;
    }

    public void setPrecoEspecial(double precoEspecial) {
        this.precoEspecial = precoEspecial;
    }

    public void setPrecoPremium(double precoPremium) {
        this.precoPremium = precoPremium;
    }
}
