import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class PizzariaGUI extends JFrame {
    private JComboBox<String> formaComboBox;
    private JComboBox<String> medidaComboBox;
    private JTextField tamanhoField;
    private JLabel dimensaoLabel;
    private JComboBox<Sabor> sabor1ComboBox;
    private JComboBox<Sabor> sabor2ComboBox;
    private JTextArea pedidoArea;
    private JTextArea pedidoGerenciarArea;
    private Pedido pedido;
    private ArrayList<Cliente> clientes;
    private DefaultTableModel tableModel;
    private JTable clienteTable;
    private JTextField nomeField, sobrenomeField, telefoneField;
    private JTextField filtroSobrenomeField, filtroTelefoneField;
    private Cliente clienteSelecionado;
    private HashMap<Cliente, ArrayList<Pedido>> pedidosClientes; // Associar pedidos a clientes
    private ArrayList<Sabor> sabores = new ArrayList<>();
    private JLabel totalLabel = new JLabel();
    private JComboBox<String> pedidosComboBox;

    public PizzariaGUI() {

        inicializarSaboresPadrao();

        // Inicializando a lista de clientes
        clientes = new ArrayList<>();

        // Inicializando o mapa de pedidos de clientes
        pedidosClientes = new HashMap<>();


        // Configuração geral da janela
        setTitle("Pizzaria em cm²");
        setSize(800, 600); // Aumenta o tamanho geral da janela
        setLocationRelativeTo(null); // Centraliza a janela
        setFont(
            new Font("Arial", Font.PLAIN, 14)
        );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Gerenciar Clientes", criarTelaClientes());

        tabbedPane.addTab("Fazer Pedido", criarTelaPedido());

        tabbedPane.addTab("Gerenciar pedido", criarTelaGerenciarPedido());

        tabbedPane.addTab("Atualizar Preços", criarTelaAtualizarPrecos());

        tabbedPane.addTab("Cadastro de sabor", criarTelaCadastroSabor());

        tabbedPane.setBackground(Color.LIGHT_GRAY);

        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adiciona o JTabbedPane à janela principal
        add(tabbedPane);
        setVisible(true);
    }

    // Tela para cadastrar, atualizar e excluir clientes
    private JPanel criarTelaClientes() {
        JPanel clientePanel = new JPanel(new BorderLayout());
        // Criar painel com GridBagLayout para controle de proporções
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Definir configurações de margem e alinhamento
        gbc.insets = new Insets(5, 5, 5, 5); // margem entre os componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Adicionar campo Nome
        gbc.gridx = 0; // coluna
        gbc.gridy = 0; // linha
        gbc.weightx = 0.25; // 25% da largura para o rótulo
        formPanel.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75; // 75% da largura para o campo de texto
        nomeField = new JTextField();
        formPanel.add(nomeField, gbc);

        // Adicionar campo Sobrenome
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.25;
        formPanel.add(new JLabel("Sobrenome:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        sobrenomeField = new JTextField();
        formPanel.add(sobrenomeField, gbc);

        // Adicionar campo Telefone
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.25;
        formPanel.add(new JLabel("Telefone:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        telefoneField = new JTextField();
        formPanel.add(telefoneField, gbc);

        // Botões de ação
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // ocupa toda a linha para o botão
        gbc.weightx = 1.0; // para centralizar os botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton adicionarClienteButton = new JButton("Adicionar Cliente");
        JButton atualizarClienteButton = new JButton("Atualizar Cliente");
        JButton excluirClienteButton = new JButton("Excluir Cliente");
        buttonPanel.add(adicionarClienteButton);
        buttonPanel.add(atualizarClienteButton);
        buttonPanel.add(excluirClienteButton);
        formPanel.add(buttonPanel, gbc);

        // Tabela de clientes
        String[] colunas = {"Nome", "Sobrenome", "Telefone"};
        tableModel = new DefaultTableModel(colunas, 0);
        clienteTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(clienteTable);

        // Filtros
        JPanel filtroPanel = new JPanel(new GridLayout(1, 4));
        filtroSobrenomeField = new JTextField();
        filtroTelefoneField = new JTextField();
        JButton filtrarButton = new JButton("Filtrar");
        filtroPanel.add(new JLabel("Sobrenome:"));
        filtroPanel.add(filtroSobrenomeField);
        filtroPanel.add(new JLabel("Telefone:"));
        filtroPanel.add(filtroTelefoneField);
        filtroPanel.add(filtrarButton);

        // Adiciona as seções ao painel principal
        clientePanel.add(formPanel, BorderLayout.NORTH);
        clientePanel.add(scrollPane, BorderLayout.CENTER);
        clientePanel.add(filtroPanel, BorderLayout.SOUTH);

        // Ações dos botões
        adicionarClienteButton.addActionListener(e -> adicionarCliente());
        atualizarClienteButton.addActionListener(e -> atualizarCliente());
        excluirClienteButton.addActionListener(e -> excluirCliente());
        filtrarButton.addActionListener(e -> filtrarClientes());

        return clientePanel;
    }

    private JPanel criarTelaPedido() {
        JPanel pedidoPanel = new JPanel(new BorderLayout());

        // Campo para selecionar cliente por telefone
        JPanel clientePanel = new JPanel(new FlowLayout());
        JTextField telefoneClienteField = new JTextField(10);
        JButton buscarClienteButton = new JButton("Buscar Cliente");
        JLabel clienteNomeLabel = new JLabel("Cliente: Não selecionado");
        JButton refreshButton = new JButton("Refresh");

        clientePanel.add(refreshButton);
        clientePanel.add(new JLabel("Telefone do Cliente:"));
        clientePanel.add(telefoneClienteField);
        clientePanel.add(buscarClienteButton);
        clientePanel.add(clienteNomeLabel);

        // Configura a área de pedidos e componentes
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        JLabel formaLabel = new JLabel("Escolha a forma:");
        String[] formas = {"Quadrado", "Triângulo", "Círculo"};
        formaComboBox = new JComboBox<>(formas);

        // Adiciona o JComboBox para escolher entre Lado/Raio ou Área
        JLabel medidaLabel = new JLabel("Escolha a medida:");
        String[] medidas = {"Tamanho (lado/raio)", "Área (cm²)"};
        medidaComboBox = new JComboBox<>(medidas);

        JLabel tamanhoLabel = new JLabel("Informe o lado:");
        tamanhoField = new JTextField();
        dimensaoLabel = new JLabel("(Mínimo: " + Quadrado.getQuadradoMin() + ", máximo: " + Quadrado.getQuadradoMax() + ")");


        JLabel sabor1Label = new JLabel("Sabor 1:");
        JComboBox<Sabor> sabor1ComboBox = new JComboBox<>();

        JLabel sabor2Label = new JLabel("Sabor 2 (Opcional):");
        JComboBox<Sabor> sabor2ComboBox = new JComboBox<>();
        sabor2ComboBox.insertItemAt(null, 0);
        sabor2ComboBox.setSelectedIndex(0);

        // Adiciona um ActionListener ao formaComboBox para atualizar os labels dinamicamente
        formaComboBox.addActionListener(e -> {
            String formaSelecionada = (String) formaComboBox.getSelectedItem();
            switch (formaSelecionada) {
                case "Quadrado":
                    tamanhoLabel.setText("Informe o lado:");
                    dimensaoLabel.setText("(Mínimo: " + Quadrado.getQuadradoMin() + ", máximo: " + Quadrado.getQuadradoMax() + ")");
                    break;
                case "Triângulo":
                    tamanhoLabel.setText("Informe o lado:");
                    dimensaoLabel.setText("(Mínimo: " + Triangulo.getTrianguloMin() + ", máximo: " + Triangulo.getTrianguloMax() + ")");
                    break;
                case "Círculo":
                    tamanhoLabel.setText("Informe o raio:");
                    dimensaoLabel.setText("(Mínimo: " + Circulo.getCirculoMin() + ", máximo: " + Circulo.getCirculoMax() + ")");
                    break;
            }
        });

        // Adiciona um ActionListener ao medidaComboBox para atualizar os labels dinamicamente
        medidaComboBox.addActionListener(e -> {
            String medidaSelecionada = (String) medidaComboBox.getSelectedItem();
            String formaSelecionada = (String) formaComboBox.getSelectedItem();
            if (medidaSelecionada.equals("Tamanho (lado/raio)")) {
                switch (formaSelecionada) {
                    case "Quadrado":
                        tamanhoLabel.setText("Informe o lado:");
                        dimensaoLabel.setText("(Mínimo: " + Quadrado.getQuadradoMin() + ", máximo: " + Quadrado.getQuadradoMax() + ")");
                        break;
                    case "Triângulo":
                        tamanhoLabel.setText("Informe o lado:");
                        dimensaoLabel.setText("(Mínimo: " + Triangulo.getTrianguloMin() + ", máximo: " + Triangulo.getTrianguloMax() + ")");
                        break;
                    case "Círculo":
                        tamanhoLabel.setText("Informe o raio:");
                        dimensaoLabel.setText("(Mínimo: " + Circulo.getCirculoMin() + ", máximo: " + Circulo.getCirculoMax() + ")");
                        break;
                }
            } else {
                tamanhoLabel.setText("Informe a área em cm²:");
                dimensaoLabel.setText("(Mínimo: 100 cm², máximo: 1600 cm²)");
            }
        });

        for (Sabor sabor : sabores) {
            sabor1ComboBox.addItem(sabor);
            sabor2ComboBox.addItem(sabor);
        }

        JButton atualizarButton = new JButton("Recarregar Sabores");
        totalLabel.setText("Preço Total: R$ 0.00");

        formPanel.add(formaLabel);
        formPanel.add(formaComboBox);

        formPanel.add(medidaLabel);
        formPanel.add(medidaComboBox);

        formPanel.add(tamanhoLabel);
        formPanel.add(tamanhoField);
        formPanel.add(dimensaoLabel);
        formPanel.add(new JLabel()); // Espaço vazio para alinhamento
        formPanel.add(totalLabel);
        formPanel.add(atualizarButton);

        formPanel.add(sabor1Label);
        formPanel.add(sabor1ComboBox);
        formPanel.add(sabor2Label);
        formPanel.add(sabor2ComboBox);

        atualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Atualiza o layout
                sabor1ComboBox.removeAllItems();
                sabor2ComboBox.removeAllItems();
                sabor2ComboBox.insertItemAt(null, 0); // Deixar vazio para opcional
                sabor2ComboBox.setSelectedIndex(0);
                for (Sabor sabor : sabores) {
                    sabor1ComboBox.addItem(sabor);
                    sabor2ComboBox.addItem(sabor);
                }
                clientePanel.revalidate();
                clientePanel.repaint();
            }
        });

        // Painel de botões na lateral direita
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JButton adicionarPizzaButton = new JButton("Adicionar Pizza");
        JButton finalizarPedidoButton = new JButton("Finalizar Pedido");
        JButton atualizarPedidoButton = new JButton("Atualizar Pedido");
        JButton excluirPizzaButton = new JButton("Excluir Pizza");

        // Reduzindo o tamanho dos botões
        Dimension buttonSize = new Dimension(120, 30);
        adicionarPizzaButton.setPreferredSize(buttonSize);
        finalizarPedidoButton.setPreferredSize(buttonSize);
        atualizarPedidoButton.setPreferredSize(buttonSize);
        excluirPizzaButton.setPreferredSize(buttonSize);

        buttonPanel.add(adicionarPizzaButton);
        buttonPanel.add(finalizarPedidoButton);
        buttonPanel.add(atualizarPedidoButton);
        buttonPanel.add(excluirPizzaButton);

        // Painel superior com formulário e botões
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(clientePanel, BorderLayout.NORTH);

        // SplitPane horizontal para dividir formPanel e buttonPanel
        JSplitPane formButtonSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, buttonPanel);
        formButtonSplit.setDividerLocation(0.75); // 75% formPanel, 25% buttonPanel
        formButtonSplit.setResizeWeight(0.75);

        topPanel.add(formButtonSplit, BorderLayout.CENTER);

        // Campo para exibir o histórico de pedidos anteriores do cliente
        JTextArea pedidosAnterioresArea = new JTextArea(10, 30);
        pedidosAnterioresArea.setEditable(false);
        JScrollPane pedidosAnterioresScrollPane = new JScrollPane(pedidosAnterioresArea);

        // Campo para exibir os itens do pedido atual
        pedidoArea = new JTextArea(10, 30);
        pedidoArea.setEditable(false);
        JScrollPane pedidoScrollPane = new JScrollPane(pedidoArea);

        // Painel para histórico de pedidos e itens do pedido atual
        JPanel pedidosPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        pedidosPanel.add(pedidosAnterioresScrollPane);
        pedidosPanel.add(pedidoScrollPane);

        // SplitPane vertical para dividir o painel superior e o histórico
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, pedidosPanel);
        mainSplitPane.setDividerLocation(0.5); // 50% para cada parte
        mainSplitPane.setResizeWeight(0.5);

        // Adiciona ações aos botões
        adicionarPizzaButton.addActionListener(e -> adicionarPizza(sabor1ComboBox, sabor2ComboBox));
        finalizarPedidoButton.addActionListener(e -> finalizarPedido());
        excluirPizzaButton.addActionListener(e -> excluirPizza());
        buscarClienteButton.addActionListener(e -> {
            String telefone = telefoneClienteField.getText();
            Cliente cliente = buscarClientePorTelefone(telefone);
            if (cliente != null) {
                clienteNomeLabel.setText("Cliente: " + cliente.getNome() + " " + cliente.getSobrenome());
                clienteSelecionado = cliente;
                exibirPedidosAnteriores(cliente, pedidosAnterioresArea);
                pedido = new Pedido();
                pedidoArea.setText(""); // Limpa a área de pedido para novo pedido
            } else {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.");
                clienteSelecionado = null;
            }
        });

        refreshButton.addActionListener(e -> {
            String telefone = telefoneClienteField.getText();
            Cliente cliente = buscarClientePorTelefone(telefone);

            if (cliente != null) {
                clienteNomeLabel.setText("Cliente: " + cliente.getNome() + " " + cliente.getSobrenome());
                clienteSelecionado = cliente;
                exibirPedidosAnteriores(cliente, pedidosAnterioresArea);
                pedido = new Pedido();
                pedidoArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.");
            }
        });
        atualizarPedidoButton.addActionListener(e -> {
            if (clienteSelecionado != null) {
                atualizarPedido(clienteSelecionado);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cliente para atualizar o pedido.");
            }
        });

        // Adiciona o mainSplitPane ao painel principal
        pedidoPanel.add(mainSplitPane, BorderLayout.CENTER);

        return pedidoPanel;
    }

    private void dropdown(Cliente cliente) {
        ArrayList<Pedido> listaPedidos = pedidosClientes.get(cliente);

        if (listaPedidos == null || listaPedidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este cliente não possui pedidos anteriores para atualizar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Exibe uma lista de pedidos para o cliente selecionar qual atualizar
        String[] pedidosOptions = new String[listaPedidos.size()];
        for (int i = 0; i < listaPedidos.size(); i++) {
            Pedido p = listaPedidos.get(i);
            pedidosOptions[i] = "Pedido ID: " + p.getIdPedido() + " - R$" + p.getValorTotal();
        }

        String selectedPedidoStr = (String) JOptionPane.showInputDialog(
                this,
                "Selecione um pedido para atualizar:",
                "Atualizar Pedido",
                JOptionPane.QUESTION_MESSAGE,
                null,
                pedidosOptions,
                pedidosOptions[0]);

        if (selectedPedidoStr != null) {
            int pedidoIndex = -1;
            for (int i = 0; i < pedidosOptions.length; i++) {
                if (pedidosOptions[i].equals(selectedPedidoStr)) {
                    pedidoIndex = i;
                    break;
                }
            }

            if (pedidoIndex != -1) {
                pedido = listaPedidos.get(pedidoIndex); // Carrega o pedido selecionado
                exibirItensPedidoAtual(); // Exibe os itens do pedido atual no JTextArea
                listaPedidos.remove(pedido);
            }
        }
        atualizaPreco();
    }
    private JPanel criarTelaGerenciarPedido () {
        JPanel gerenciarPanel = new JPanel(new BorderLayout());
        JButton dropdown = new JButton("Atualizar Pedido");
        // Campo para selecionar cliente por telefone
        JPanel clientePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField telefoneClienteField = new JTextField(10);
        JButton buscarClienteButton = new JButton("Buscar Cliente");
        JLabel clienteNomeLabel = new JLabel("Cliente: Não selecionado");

        pedidosComboBox = new JComboBox<>();
        pedidosComboBox.setEnabled(false);

        clientePanel.add(new JLabel("Telefone do Cliente:"));
        clientePanel.add(telefoneClienteField);
        clientePanel.add(buscarClienteButton);
        clientePanel.add(clienteNomeLabel);
        clientePanel.add(pedidosComboBox);

        // Configura a área de pedidos e componentes
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5)); // Grid mais simples para o status
        JLabel statusLabel = new JLabel("Status do Pedido:");
        JComboBox<Pedido.EstadoPedido> statusComboBox = new JComboBox<>(Pedido.EstadoPedido.values());
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);

        // Painel de botões na lateral direita
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Alinhamento centralizado
        JButton atualizarPedidoButton = new JButton("Atualizar Pedido");
        buttonPanel.add(atualizarPedidoButton);

        // Campo para exibir os itens do pedido selecionado
        pedidoGerenciarArea = new JTextArea(10, 30);
        pedidoGerenciarArea.setEditable(false);
        //JScrollPane pedidoScrollPane = new JScrollPane(pedidoGerenciarArea);

        // Painel superior com formulário e botões
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(clientePanel, BorderLayout.NORTH);

        // tamanho dos botões
        Dimension buttonSize = new Dimension(220, 90);

        atualizarPedidoButton.setPreferredSize(buttonSize);
        atualizarPedidoButton.setFont(new Font("Arial", Font.PLAIN, 20));

        // SplitPane horizontal para dividir formPanel e buttonPanel
        JSplitPane formButtonSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, buttonPanel);
        formButtonSplit.setDividerLocation(0.75); // 75% formPanel, 25% buttonPanel
        formButtonSplit.setResizeWeight(0.75);

        topPanel.add(formButtonSplit, BorderLayout.CENTER);

        // Campo para exibir o histórico de pedidos anteriores do cliente
        JTextArea pedidosAnterioresArea = new JTextArea(10, 30);
        pedidosAnterioresArea.setEditable(false);
        JScrollPane pedidosAnterioresScrollPane = new JScrollPane(pedidosAnterioresArea);

        // Campo para exibir os itens do pedido atual
        pedidoGerenciarArea = new JTextArea(10, 30);
        pedidoGerenciarArea.setEditable(false);
        JScrollPane pedidoScrollPane = new JScrollPane(pedidoGerenciarArea);

        // Painel para histórico de pedidos e itens do pedido atual
        JPanel pedidosPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        pedidosPanel.add(pedidosAnterioresScrollPane);
        pedidosPanel.add(pedidoScrollPane);

        // SplitPane vertical para dividir o painel superior e o histórico (30% - 70%)
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, pedidosPanel);
        mainSplitPane.setDividerLocation(0.3); // 30% topPanel, 70% pedidosPanel
        mainSplitPane.setResizeWeight(0.3);

        // Adiciona ações aos botões
        buscarClienteButton.addActionListener(e -> {

            String telefone = telefoneClienteField.getText();
            Cliente cliente = buscarClientePorTelefone(telefone);

            if (cliente != null) {
                clienteNomeLabel.setText("Cliente: " + cliente.getNome() + " " + cliente.getSobrenome());
                clienteSelecionado = cliente;
                exibirPedidosAnteriores(cliente, pedidosAnterioresArea);
                //pedido = new Pedido();

                ArrayList<Pedido> listaPedidos = pedidosClientes.get(cliente);
                pedidosComboBox.removeAllItems(); // Limpa itens anteriores
                if (listaPedidos != null && !listaPedidos.isEmpty()) {
                    for (Pedido pedido : listaPedidos) {
                        pedidosComboBox.addItem("Pedido ID: " + pedido.getIdPedido() + " - R$" + pedido.getValorTotal());
                    }
                    pedidosComboBox.setEnabled(true); // Habilita o dropdown
                } else {
                    pedidosComboBox.addItem("Nenhum pedido disponível");
                    pedidosComboBox.setEnabled(false); // Desabilita o dropdown
                }
            } else {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado.");
                clienteSelecionado = null;
                pedidosComboBox.setEnabled(false); // Desabilita o dropdown
            }
        });

        atualizarPedidoButton.addActionListener(e -> {
            if (clienteSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Pedido pedido = buscarPedidoSelecionado();
            if (pedido != null) {
                Pedido.EstadoPedido estadoSelecionado = (Pedido.EstadoPedido) statusComboBox.getSelectedItem();
                pedido.setEstado(estadoSelecionado);
                pedidoGerenciarArea.setText("Pedido atualizado:\nPedido ID: " + pedido.getIdPedido() +
                        "\nNovo status: " + estadoSelecionado);
                JOptionPane.showMessageDialog(this, "Status do pedido atualizado para: " + estadoSelecionado);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um pedido válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Adiciona o mainSplitPane ao painel principal
        gerenciarPanel.add(mainSplitPane, BorderLayout.CENTER);

        return gerenciarPanel;
    }

    private Pedido buscarPedidoSelecionado() {
        int selectedIndex = pedidosComboBox.getSelectedIndex(); // Obtém o índice selecionado
        if (selectedIndex >= 0 && clienteSelecionado != null) {
            ArrayList<Pedido> listaPedidos = pedidosClientes.get(clienteSelecionado);
            if (listaPedidos != null && selectedIndex < listaPedidos.size()) {
                return listaPedidos.get(selectedIndex); // Retorna o pedido correspondente ao índice
            }
        }
        return null; // Retorna null se algo deu errado
    }
    // Tela para atualizar os preços das pizzas
    private JPanel criarTelaAtualizarPrecos() {
        JPanel precoPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        Font fieldFont = new Font("SansSerif", Font.PLAIN, 20);

        JLabel precoSimplesLabel = new JLabel("Preço Simples (R$/cm²):");
        JTextField precoSimplesField = new JTextField(Double.toString(PrecoPizza.getInstance().getPrecoSimples()));
        precoSimplesField.setFont(fieldFont);
        precoSimplesLabel.setFont(fieldFont);

        JLabel precoEspecialLabel = new JLabel("Preço Especial (R$/cm²):");
        JTextField precoEspecialField = new JTextField(Double.toString(PrecoPizza.getInstance().getPrecoEspecial()));
        precoEspecialField.setFont(fieldFont);
        precoEspecialLabel.setFont(fieldFont);

        JLabel precoPremiumLabel = new JLabel("Preço Premium (R$/cm²):");
        JTextField precoPremiumField = new JTextField(Double.toString(PrecoPizza.getInstance().getPrecoPremium()));
        precoPremiumField.setFont(fieldFont);
        precoPremiumLabel.setFont(fieldFont);

        JButton atualizarPrecosButton = new JButton("Atualizar Preços");
        Font novaFonte = new Font("SansSerif", Font.PLAIN, 22); //
        atualizarPrecosButton.setFont(novaFonte);

        // Ação do botão para atualizar os preços
        atualizarPrecosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double precoSimples = Double.parseDouble(precoSimplesField.getText());
                    double precoEspecial = Double.parseDouble(precoEspecialField.getText());
                    double precoPremium = Double.parseDouble(precoPremiumField.getText());

                    PrecoPizza.getInstance().setPrecoSimples(precoSimples);
                    PrecoPizza.getInstance().setPrecoEspecial(precoEspecial);
                    PrecoPizza.getInstance().setPrecoPremium(precoPremium);

                    JOptionPane.showMessageDialog(precoPanel, "Preços atualizados com sucesso!");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(precoPanel, "Por favor, insira valores válidos para os preços.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

            // Adiciona os componentes ao painel
            precoPanel.add(precoSimplesLabel);
            precoPanel.add(precoSimplesField);
            precoPanel.add(precoEspecialLabel);
            precoPanel.add(precoEspecialField);
            precoPanel.add(precoPremiumLabel);
            precoPanel.add(precoPremiumField);
            precoPanel.add(new JLabel());
            precoPanel.add(atualizarPrecosButton);

            return precoPanel;
        }

    private JPanel criarTelaCadastroSabor() {
        JPanel saborPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        Font fieldFont = new Font("SansSerif", Font.PLAIN, 20);

        JLabel tipoLabel = new JLabel("Tipo:");
        String[] tipos = {"Simples", "Especial", "Premium"};
        JComboBox<String> tipoComboBox = new JComboBox<>(tipos);
        tipoLabel.setFont(fieldFont);
        tipoComboBox.setFont(fieldFont);

        JLabel nomeLabel = new JLabel("Nome do Sabor:");
        JTextField nomeField = new JTextField();
        nomeLabel.setFont(fieldFont);
        nomeField.setFont(fieldFont);

        JButton adicionarSaborButton = new JButton("Adicionar Sabor");
        adicionarSaborButton.setFont(fieldFont);

        formPanel.add(tipoLabel);
        formPanel.add(tipoComboBox);
        formPanel.add(nomeLabel);
        formPanel.add(nomeField);

        saborPanel.add(formPanel, BorderLayout.NORTH);
        saborPanel.add(adicionarSaborButton, BorderLayout.SOUTH);

        sabor1ComboBox = new JComboBox<>();
        sabor2ComboBox = new JComboBox<>();

        // Ação do botão adicionar sabor
        adicionarSaborButton.addActionListener(e -> {
            String nome = nomeField.getText();
            String tipo = (String) tipoComboBox.getSelectedItem();

            if (!nome.isEmpty() && tipo != null) {
                Sabor novoSabor = new Sabor(nome, tipo);
                sabores.add(novoSabor);

                // Atualizar as comboboxes de sabores
                sabor1ComboBox.addItem(novoSabor);
                sabor2ComboBox.addItem(novoSabor);

                JOptionPane.showMessageDialog(this, "Sabor adicionado: " + novoSabor, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

            nomeField.setText("");
        });

        return saborPanel;
    }

    private void adicionarCliente() {
        String nome = nomeField.getText().trim();
        String sobrenome = sobrenomeField.getText().trim();
        String telefone = telefoneField.getText().trim();

        if (nome.isEmpty() || sobrenome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e sobrenome são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!telefone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "O telefone deve conter apenas números.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Adiciona um novo cliente à lista
        Cliente cliente = new Cliente(nome, sobrenome, telefone);
        clientes.add(cliente);

        // Adiciona o cliente na tabela
        tableModel.addRow(new Object[]{nome, sobrenome, telefone});

        limparCampos();
    }

    private void atualizarCliente() {
        int selectedRow = clienteTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Atualizar dados do cliente na tabela
            String nome = nomeField.getText().trim();
            String sobrenome = sobrenomeField.getText().trim();
            String telefone = telefoneField.getText().trim();

            if (nome.isEmpty() || sobrenome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e sobrenome são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!telefone.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "O telefone deve conter apenas números.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Atualiza os dados no ArrayList
            Cliente cliente = clientes.get(selectedRow);
            cliente.setNome(nome);
            cliente.setSobrenome(sobrenome);
            cliente.setTelefone(telefone);

            // Atualiza a tabela
            tableModel.setValueAt(nome, selectedRow, 0);
            tableModel.setValueAt(sobrenome, selectedRow, 1);
            tableModel.setValueAt(telefone, selectedRow, 2);
            limparCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para atualizar.");
        }
    }

    private void excluirCliente() {
        int selectedRow = clienteTable.getSelectedRow();

        if (selectedRow >= 0) {
            // Confirmar a exclusão
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza de que deseja excluir este cliente?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            // Se o usuário confirmar a exclusão
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove o cliente da lista
                clientes.remove(selectedRow);

                // Remove o cliente da tabela
                tableModel.removeRow(selectedRow);

                // Limpa os campos após a exclusão
                limparCampos();

                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um cliente para excluir.",                                      "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarClientes() {
        String sobrenomeFiltro = filtroSobrenomeField.getText().toLowerCase();
        String telefoneFiltro = filtroTelefoneField.getText();

        // Limpa a tabela
        tableModel.setRowCount(0);

        // Filtra a lista de clientes
        for (Cliente cliente : clientes) {
            if (cliente.getSobrenome().toLowerCase().contains(sobrenomeFiltro) &&
                cliente.getTelefone().contains(telefoneFiltro)) {
                tableModel.addRow(new Object[]{cliente.getNome(), cliente.getSobrenome(), cliente.getTelefone()});
            }
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        sobrenomeField.setText("");
        telefoneField.setText("");
    }

    // Método para exibir os pedidos anteriores de um cliente
    private void exibirPedidosAnteriores(Cliente cliente, JTextArea pedidosAnterioresArea) {
        pedidosAnterioresArea.setText(""); // Limpa a área de pedidos anteriores

        ArrayList<Pedido> listaPedidos = pedidosClientes.get(cliente);

        if (listaPedidos != null && !listaPedidos.isEmpty()) {
            pedidosAnterioresArea.append("Pedidos Anteriores:\n");
            for (Pedido pedidoAnterior : listaPedidos) {
                pedidosAnterioresArea.append("Pedido ID: " + pedidoAnterior.getIdPedido() + "\n");
                pedidosAnterioresArea.append("  Preço total: R$" + pedidoAnterior.getValorTotal() + "\n");
                for (Pizza pizza : pedidoAnterior.getPizzas()) {
                    pedidosAnterioresArea.append("  - " + pizza.toString() + "\n");
                }
                pedidosAnterioresArea.append("  Status: " + pedidoAnterior.getEstado() + "\n");
                pedidosAnterioresArea.append("\n");
            }
        } else {
            pedidosAnterioresArea.append("Nenhum pedido anterior encontrado para este cliente.");
        }
    }

    private void atualizarPedido(Cliente cliente) {
        ArrayList<Pedido> listaPedidos = pedidosClientes.get(cliente);

        if (listaPedidos == null || listaPedidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este cliente não possui pedidos anteriores para atualizar.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Exibe uma lista de pedidos para o cliente selecionar qual atualizar
        String[] pedidosOptions = new String[listaPedidos.size()];
        for (int i = 0; i < listaPedidos.size(); i++) {
            Pedido p = listaPedidos.get(i);
            pedidosOptions[i] = "Pedido ID: " + p.getIdPedido() + " - R$" + p.getValorTotal();
        }

        String selectedPedidoStr = (String) JOptionPane.showInputDialog(
                this,
                "Selecione um pedido para atualizar:",
                "Atualizar Pedido",
                JOptionPane.QUESTION_MESSAGE,
                null,
                pedidosOptions,
                pedidosOptions[0]);

        if (selectedPedidoStr != null) {
            int pedidoIndex = -1;
            for (int i = 0; i < pedidosOptions.length; i++) {
                if (pedidosOptions[i].equals(selectedPedidoStr)) {
                    pedidoIndex = i;
                    break;
                }
            }

            if (pedidoIndex != -1) {
                pedido = listaPedidos.get(pedidoIndex); // Carrega o pedido selecionado
                exibirItensPedidoAtual(); // Exibe os itens do pedido atual no JTextArea
                listaPedidos.remove(pedido);
            }
        }
        atualizaPreco();
    }

    private void excluirPizza() {
        if (pedido.getPizzas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há pizzas no pedido para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mostra as pizzas do pedido atual em uma lista
        String[] pizzasOptions = new String[pedido.getPizzas().size()];
        for (int i = 0; i < pedido.getPizzas().size(); i++) {
            Pizza pizza = pedido.getPizzas().get(i);
            pizzasOptions[i] = "Pizza " + (i + 1) + ": " + pizza.toString(); // Exibe o índice para o usuário
        }

        String selectedPizzaStr = (String) JOptionPane.showInputDialog(
                this,
                "Selecione uma pizza para excluir:",
                "Excluir Pizza",
                JOptionPane.QUESTION_MESSAGE,
                null,
                pizzasOptions,
                pizzasOptions[0]);

        if (selectedPizzaStr != null) {
            int pizzaIndex = Integer.parseInt(selectedPizzaStr.split(":")[0].split(" ")[1]) - 1; // Obtém o índice da pizza

            // Remove a pizza do pedido
            pedido.removerPizza(pizzaIndex); // Método que você deve implementar na classe Pedido
            exibirItensPedidoAtual(); // Atualiza a exibição dos itens
        }
        atualizaPreco();
    }

    private void exibirItensPedidoAtual() {
        pedidoArea.setText(""); // Limpa a área de pedido

        for (Pizza pizza : pedido.getPizzas()) {
            pedidoArea.append("Pizza: " + pizza + "\n");
        }
    }

    private Cliente buscarClientePorTelefone(String telefone) {
        ArrayList<Cliente> clientesCorrespondentes = new ArrayList<>();

        // Coleta todos os clientes que têm o mesmo telefone
        for (Cliente cliente : clientes) {
            if (cliente.getTelefone().equals(telefone)) {
                clientesCorrespondentes.add(cliente);
            }
        }

        // Verifica se temos clientes correspondentes
        if (clientesCorrespondentes.isEmpty()) {
            // Não encontrou clientes com esse telefone
            return null; // Ou exibir uma mensagem, se preferir
        } else if (clientesCorrespondentes.size() == 1) {
            // Apenas um cliente encontrado
            return clientesCorrespondentes.get(0);
        } else {
            // Múltiplos clientes encontrados, montando opções para o dropdown
            String[] clientesOptions = new String[clientesCorrespondentes.size()];
            for (int i = 0; i < clientesCorrespondentes.size(); i++) {
                Cliente cliente = clientesCorrespondentes.get(i);
                clientesOptions[i] = cliente.getTelefone() + " - " + cliente.getNome() + " " + cliente.getSobrenome();
            }

            // Exibe o dropdown para selecionar o cliente
            String selectedClienteStr = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecione um cliente:",
                    "Buscar Cliente",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    clientesOptions,
                    clientesOptions[0]);

            if (selectedClienteStr != null) {
                // Encontra o cliente correspondente ao selecionado
                for (Cliente cliente : clientesCorrespondentes) {
                    String clienteStr = cliente.getTelefone() + " - " + cliente.getNome() + " " + cliente.getSobrenome();
                    if (clienteStr.equals(selectedClienteStr)) {
                        return cliente;
                    }
                }
            }
        }

        return null; // Retorna null se nenhuma seleção foi feita
    }

    private void adicionarPizza(JComboBox<Sabor> sabor1ComboBox, JComboBox<Sabor> sabor2ComboBox) {
        if (clienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String formaSelecionada = (String) formaComboBox.getSelectedItem();
            String medidaSelecionada = (String) medidaComboBox.getSelectedItem();
            double tamanho = Double.parseDouble(tamanhoField.getText());

        // Converte a área em tamanho (lado/raio) se necessário
        if (medidaSelecionada.equals("Área (cm²)")) {
            if (tamanho < Forma.AREA_MIN || tamanho > Forma.AREA_MAX) {
                throw new IllegalArgumentException("A área deve estar entre " + Forma.AREA_MIN + " e " + Forma.AREA_MAX + " cm².");
            }
            switch (formaSelecionada) {
                case "Quadrado":
                    tamanho = Math.sqrt(tamanho); // transforma a área em lado
                    JOptionPane.showMessageDialog(null, "O lado do quadrado é: " + tamanho + " cm");
                    break;
                case "Triângulo":
                    tamanho = Math.sqrt((4 * tamanho) / Math.sqrt(3)); // transforma a área em lado
                    JOptionPane.showMessageDialog(null, "O lado do triângulo é: " + tamanho + " cm");
                    break;
                case "Círculo":
                    tamanho = Math.sqrt(tamanho / Math.PI); // transforma a área em raio
                    JOptionPane.showMessageDialog(null, "O raio do círculo é: " + tamanho + " cm");
                    break;
                default:
                    throw new IllegalArgumentException("Forma inválida.");
            }
        }

            Sabor sabor1 = (Sabor) sabor1ComboBox.getSelectedItem();
            Sabor sabor2 = (Sabor) sabor2ComboBox.getSelectedItem();

            Forma forma = null;
            switch (formaSelecionada) {
                case "Quadrado":
                    if (tamanho < Quadrado.getQuadradoMin() || tamanho > Quadrado.getQuadradoMax()) {
                        throw new IllegalArgumentException("O lado do quadrado deve ser entre " + Quadrado.getQuadradoMin() + " e " + Quadrado.getQuadradoMax() + " cm.");
                    }
                    forma = new Quadrado(tamanho);
                    break;
                case "Triângulo":
                    if (tamanho < Triangulo.getTrianguloMin() || tamanho > Triangulo.getTrianguloMax()) {
                        throw new IllegalArgumentException("O lado do triangulo deve ser entre " + Triangulo.getTrianguloMin() + " e " + Triangulo.getTrianguloMax() + " cm.");
                    }
                    forma = new Triangulo(tamanho);
                    break;
                case "Círculo":
                    if (tamanho < Circulo.getCirculoMin() || tamanho > Circulo.getCirculoMax()) {
                        throw new IllegalArgumentException("O raio do circulo deve ser entre " + Circulo.getCirculoMin() + " e " + Circulo.getCirculoMax() + " cm.");
                    }
                    forma = new Circulo(tamanho);
                    break;
                default:
                    throw new IllegalArgumentException("Forma inválida.");
            }

            Sabor[] sabores = (sabor2 == null) ? new Sabor[]{sabor1} : new Sabor[]{sabor1, sabor2};
            Pizza pizza = new Pizza(forma, sabores);
            pedido.adicionarPizza(pizza);

            pedidoArea.append("Pizza adicionada: " + pizza + "\n");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido para o tamanho.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        atualizaPreco();
    }

    private void finalizarPedido() {
        if (clienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um cliente antes de finalizar o pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Atribuir um ID único ao pedido
        int pedidoId = pedido.getIdPedido();
        double precoTotal = pedido.calcularPrecoTotal();

        // Detalhes do pedido finalizado
        String detalhesPedido = "Pedido ID: " + pedidoId + "\nCliente: " + clienteSelecionado.getNome() + " " + clienteSelecionado.getSobrenome() + "\nPreço total: R$" + precoTotal;
        JOptionPane.showMessageDialog(this, detalhesPedido, "Pedido Finalizado", JOptionPane.INFORMATION_MESSAGE);

        // Exibir detalhes do pedido na área de pedidos
        pedidoArea.append(detalhesPedido + "\n");

        // Adicionar o pedido à lista de pedidos do cliente
        ArrayList<Pedido> listaPedidos = pedidosClientes.getOrDefault(clienteSelecionado, new ArrayList<>());
        listaPedidos.add(pedido);
        pedidosClientes.put(clienteSelecionado, listaPedidos); // Atualiza o mapa

        // Limpar o pedido e o cliente selecionado para um novo pedido
        clienteSelecionado = null;
        atualizaPreco();
    }

    private void inicializarSaboresPadrao() {
        sabores.add(new Sabor("Margherita", "Simples"));
        sabores.add(new Sabor("Calabresa", "Simples"));
        sabores.add(new Sabor("Frango com Catupiry", "Especial"));
        sabores.add(new Sabor("Quatro Queijos", "Especial"));
        sabores.add(new Sabor("Portuguesa", "Premium"));
        sabores.add(new Sabor("Pepperoni", "Premium"));
    }

    private void atualizaPreco(){
        totalLabel.setText("Preço Total: R$ "+ pedido.calcularPrecoTotal());
    }

    public static void main(String[] args) {
        new PizzariaGUI();
    }
}
