import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AlmoxarifadoGUI extends JFrame implements ActionListener {

    // Gerenciador de Negócios
    private final Almoxarifado almoxarifado = new Almoxarifado();

    // Componentes de Entrada
    private final JTextField txtNome = new JTextField(15);
    private final JTextField txtCodigo = new JTextField(10);
    private final JTextField txtQuantidade = new JTextField(5);
    private final JTextField txtLocalizacao = new JTextField(10);

    // Componentes de Tabela
    private final JTable tabelaPecas;
    private final DefaultTableModel modeloTabela;
    private final String[] colunas = {"Código", "Nome", "Quantidade", "Localização"};

    /**
     * Construtor da GUI
     */
    public AlmoxarifadoGUI() {
        super("Sistema Gráfico de Almoxarifado");

        // Configuração da Janela Principal
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 500);
        this.setLayout(new BorderLayout(10, 10)); // Layout principal com margem

        // 1. Painel de Entrada de Dados (Norte)
        JPanel painelEntrada = criarPainelEntrada();
        this.add(painelEntrada, BorderLayout.NORTH);

        // 2. Tabela de Visualização de Peças (Centro)
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaPecas = new JTable(modeloTabela);
        JScrollPane painelScroll = new JScrollPane(tabelaPecas);
        this.add(painelScroll, BorderLayout.CENTER);

        // 3. Painel de Ações (Sul)
        JPanel painelAcoes = criarPainelAcoes();
        this.add(painelAcoes, BorderLayout.SOUTH);

        // Inicializa com alguns dados de exemplo (opcional)
        inicializarDadosExemplo();

        this.setVisible(true); // Tornar a janela visível
    }

    // --- MÉTODOS DE CONSTRUÇÃO DE PAINÉIS ---

    private JPanel criarPainelEntrada() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // Layout horizontal
        panel.setBorder(BorderFactory.createTitledBorder("Nova Peça"));

        panel.add(new JLabel("Nome:"));
        panel.add(txtNome);

        panel.add(new JLabel("Código:"));
        panel.add(txtCodigo);

        panel.add(new JLabel("Qtd Inicial:"));
        panel.add(txtQuantidade);

        panel.add(new JLabel("Local:"));
        panel.add(txtLocalizacao);

        JButton btnAdicionar = new JButton("Adicionar Peça");
        btnAdicionar.setActionCommand("ADICIONAR");
        btnAdicionar.addActionListener(this);
        panel.add(btnAdicionar);

        return panel;
    }

    private JPanel criarPainelAcoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnRemover = new JButton("Remover Selecionada");
        btnRemover.setActionCommand("REMOVER");
        btnRemover.addActionListener(this);
        panel.add(btnRemover);

        JButton btnEntrada = new JButton("+ Entrada (Estoque)");
        btnEntrada.setActionCommand("ENTRADA");
        btnEntrada.addActionListener(this);
        panel.add(btnEntrada);

        JButton btnSaida = new JButton("- Saída (Estoque)");
        btnSaida.setActionCommand("SAIDA");
        btnSaida.addActionListener(this);
        panel.add(btnSaida);

        return panel;
    }

    // --- MÉTODOS DE LÓGICA DA GUI ---

    private void inicializarDadosExemplo() {
        almoxarifado.adicionarPeca(new Peca("Parafuso M8", 1001, 500, "A1-01"));
        almoxarifado.adicionarPeca(new Peca("Filtro de Óleo", 2005, 120, "B3-10"));
        almoxarifado.adicionarPeca(new Peca("Placa de Circuito", 3100, 5, "C5-02"));
        atualizarTabela();
    }

    private void atualizarTabela() {
        // Limpa todas as linhas atuais da tabela
        modeloTabela.setRowCount(0);

        // Adiciona as peças do Almoxarifado à tabela
        for (Peca peca : almoxarifado.listarPecas()) {
            modeloTabela.addRow(new Object[]{
                    peca.getCodigo(),
                    peca.getNome(),
                    peca.getQuantidade(),
                    peca.getLocalizacao()
            });
        }
    }

    private void adicionarPeca() {
        try {
            String nome = txtNome.getText().trim();
            int codigo = Integer.parseInt(txtCodigo.getText().trim());
            int quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            String localizacao = txtLocalizacao.getText().trim();

            // Validação simples
            if (nome.isEmpty() || localizacao.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos de texto!", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Peca novaPeca = new Peca(nome, codigo, quantidade, localizacao);
            almoxarifado.adicionarPeca(novaPeca); // A lógica de Almoxarifado já verifica códigos duplicados

            // Limpa os campos após o sucesso
            txtNome.setText("");
            txtCodigo.setText("");
            txtQuantidade.setText("");
            txtLocalizacao.setText("");

            atualizarTabela();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Código e Quantidade devem ser números inteiros válidos.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerPecaSelecionada() {
        int linhaSelecionada = tabelaPecas.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma peça na tabela para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtém o código da primeira coluna (índice 0) da linha selecionada
        int codigo = (int) modeloTabela.getValueAt(linhaSelecionada, 0);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover a peça de código " + codigo + "?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            if (almoxarifado.removerPeca(codigo)) {
                JOptionPane.showMessageDialog(this, "Peça removida com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                atualizarTabela();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao remover. Peça não encontrada na lógica.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void alterarQuantidade(boolean isEntrada) {
        int linhaSelecionada = tabelaPecas.getSelectedRow();

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma peça na tabela para alterar o estoque.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        Peca peca = almoxarifado.buscarPecaPorCodigo(codigo);

        if (peca == null) return; // Não deveria ocorrer se o código vier da tabela

        String acao = isEntrada ? "Entrada" : "Saída";
        String input = JOptionPane.showInputDialog(this,
                "Digite a quantidade para " + acao + " (Peça: " + peca.getNome() + "):",
                acao + " de Estoque", JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) return;

        try {
            int alteracao = Integer.parseInt(input.trim());

            // Se for saída, a alteração é negativa
            if (!isEntrada) {
                alteracao = -alteracao;
            }

            int novaQuantidade = peca.getQuantidade() + alteracao;

            if (novaQuantidade < 0) {
                JOptionPane.showMessageDialog(this, "Estoque insuficiente para esta saída. Quantidade atual: " + peca.getQuantidade(), "Erro de Estoque", JOptionPane.WARNING_MESSAGE);
                return;
            }

            peca.setQuantidade(novaQuantidade);
            JOptionPane.showMessageDialog(this, acao + " efetuada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            atualizarTabela();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um número inteiro válido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tratamento dos Eventos (Cliques nos Botões)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "ADICIONAR":
                adicionarPeca();
                break;
            case "REMOVER":
                removerPecaSelecionada();
                break;
            case "ENTRADA":
                alterarQuantidade(true); // true = Entrada
                break;
            case "SAIDA":
                alterarQuantidade(false); // false = Saída
                break;
        }
    }

    // --- MÉTODO PRINCIPAL ---
    public static void main(String[] args) {
        // Garante que a interface seja iniciada na Thread de Eventos
        SwingUtilities.invokeLater(AlmoxarifadoGUI::new);
    }
}