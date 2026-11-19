package view;

import controller.ControlePlanejamento;
import exception.RegraNegocioException;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;

public class TelaPrincipal extends JFrame {
    private static final long serialVersionUID = 1L;
    private final ControlePlanejamento controller;
    private JTextField txtTitulo, txtCodPart, txtNomePart, txtOrcPart, txtDescDesp, txtValDesp, txtPrio;
    private JLabel lblTotalTopo;
    private JComboBox<String> cmbTipoDesp;
    private JCheckBox chkEssencial;
    private JPanel pnlChecksPart;
    private final Map<Integer, JCheckBox> mapChecks = new LinkedHashMap<>();
    private DefaultTableModel modPart, modDesp, modResumo;
    private JTable tblPart, tblDesp, tblResumo;
    private JTextArea txtDetalhe, txtSimulacao;

    public TelaPrincipal(ControlePlanejamento controller) {
        this.controller = controller;
        setTitle("Cabe no Bolso - Planejador de Despesas Compartilhadas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        add(criarTopo(), BorderLayout.NORTH);
        add(criarAbas(), BorderLayout.CENTER);
        atualizarTela();
    }

    private String fmtMoeda(long c) { return String.format(Locale.forLanguageTag("pt-BR"), "R$ %.2f", c / 100.0); }
    private String fmtNum(long c) { return String.format(Locale.forLanguageTag("pt-BR"), "%.2f", c / 100.0); }
    private void erro(String msg) { JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE); }

    private JPanel criarTopo() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        esq.add(new JLabel("Planejamento:"));
        txtTitulo = new JTextField(18);
        esq.add(txtTitulo);

        JButton bRen = new JButton("Renomear");
        bRen.addActionListener(e -> { try { controller.alterarTitulo(txtTitulo.getText()); atualizarTela(); } catch (RegraNegocioException ex) { erro(ex.getMessage()); } });
        JButton bNovo = new JButton("Novo Vazio");
        bNovo.addActionListener(e -> {
            String t = JOptionPane.showInputDialog(this, "Título:", "Novo Passeio");
            if (t != null) { try { controller.novoPlanejamento(t); limparPart(); limparDesp(); txtSimulacao.setText(""); atualizarTela(); } catch (RegraNegocioException ex) { erro(ex.getMessage()); } }
        });
        JButton bCarr = new JButton("Carregar...");
        bCarr.addActionListener(e -> acaoCarregar());
        JButton bSalv = new JButton("Salvar...");
        bSalv.addActionListener(e -> acaoSalvar());

        esq.add(bRen); esq.add(bNovo); esq.add(bCarr); esq.add(bSalv);
        lblTotalTopo = new JLabel("Custo Total: R$ 0,00");
        lblTotalTopo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTotalTopo.setForeground(new Color(0, 90, 160));
        p.add(esq, BorderLayout.WEST); p.add(lblTotalTopo, BorderLayout.EAST);
        return p;
    }

    private JTabbedPane criarAbas() {
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("1. Participantes", criarAbaPart());
        abas.addTab("2. Despesas", criarAbaDesp());
        abas.addTab("3. Resumo e Orçamentos", criarAbaResumo());
        abas.addTab("4. Simulador (Extra)", criarAbaSimulador());
        return abas;
    }
    private JPanel criarAbaPart() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        form.setBorder(BorderFactory.createTitledBorder("Participante"));
        txtCodPart = new JTextField(5); txtNomePart = new JTextField(16); txtOrcPart = new JTextField(8);
        form.add(new JLabel("Código:")); form.add(txtCodPart);
        form.add(new JLabel("Nome:")); form.add(txtNomePart);
        form.add(new JLabel("Orçamento (R$):")); form.add(txtOrcPart);

        JButton bAdd = new JButton("Adicionar");
        bAdd.addActionListener(e -> { try { controller.cadastrarParticipante(txtCodPart.getText(), txtNomePart.getText(), txtOrcPart.getText()); limparPart(); atualizarTela(); } catch (RegraNegocioException ex) { erro(ex.getMessage()); } });
        JButton bEd = new JButton("Salvar Edição");
        bEd.addActionListener(e -> {
            int r = tblPart.getSelectedRow(); if (r < 0) { erro("Selecione na tabela."); return; }
            try { controller.editarParticipante((int) modPart.getValueAt(r, 0), txtNomePart.getText(), txtOrcPart.getText()); limparPart(); atualizarTela(); } catch (RegraNegocioException ex) { erro(ex.getMessage()); }
        });
        JButton bEx = new JButton("Excluir");
        bEx.addActionListener(e -> {
            int r = tblPart.getSelectedRow(); if (r < 0) { erro("Selecione na tabela."); return; }
            try { controller.excluirParticipante((int) modPart.getValueAt(r, 0)); limparPart(); atualizarTela(); } catch (RegraNegocioException ex) { erro(ex.getMessage()); }
        });
        JButton bLimp = new JButton("Limpar");
        bLimp.addActionListener(e -> limparPart());
        form.add(bAdd); form.add(bEd); form.add(bEx); form.add(bLimp);

        modPart = new DefaultTableModel(new Object[]{"Código", "Nome", "Orçamento"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblPart = new JTable(modPart);
        tblPart.getSelectionModel().addListSelectionListener(e -> {
            int r = tblPart.getSelectedRow();
            if (r >= 0) {
                Participante part = controller.getPlanejamento().buscarParticipante((int) modPart.getValueAt(r, 0));
                if (part != null) { txtCodPart.setText("" + part.getCodigo()); txtCodPart.setEditable(false); txtNomePart.setText(part.getNome()); txtOrcPart.setText(fmtNum(part.getOrcamentoCentavos())); }
            }
        });
        p.add(form, BorderLayout.NORTH); p.add(new JScrollPane(tblPart), BorderLayout.CENTER);
        return p;
    }

    private void limparPart() { txtCodPart.setText(""); txtCodPart.setEditable(true); txtNomePart.setText(""); txtOrcPart.setText(""); tblPart.clearSelection(); }
    private JPanel criarAbaDesp() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        JPanel topo = new JPanel(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(3, 1, 4, 4));
        form.setBorder(BorderFactory.createTitledBorder("Despesa"));

        JPanel l1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        txtDescDesp = new JTextField(16); txtValDesp = new JTextField(8); cmbTipoDesp = new JComboBox<>(new String[]{"Fixa", "Por pessoa"});
        l1.add(new JLabel("Descrição:")); l1.add(txtDescDesp); l1.add(new JLabel("Valor (R$):")); l1.add(txtValDesp); l1.add(new JLabel("Tipo:")); l1.add(cmbTipoDesp);

        JPanel l2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        chkEssencial = new JCheckBox("Essencial", true); txtPrio = new JTextField("1", 4); txtPrio.setEnabled(false);
        chkEssencial.addActionListener(e -> txtPrio.setEnabled(!chkEssencial.isSelected()));
        l2.add(chkEssencial); l2.add(new JLabel("Prioridade (se opcional):")); l2.add(txtPrio);

        JPanel l3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JButton bInc = new JButton("Incluir");
        bInc.addActionListener(e -> { try { controller.cadastrarDespesa(txtDescDesp.getText(), txtValDesp.getText(), cmbTipoDesp.getSelectedIndex() == 0, marcados(), chkEssencial.isSelected(), txtPrio.getText()); limparDesp(); atualizarTela(); } catch (RegraNegocioException ex) { erro(ex.getMessage()); } });
        JButton bEd = new JButton("Salvar Edição");
        bEd.addActionListener(e -> {
            int r = tblDesp.getSelectedRow(); if (r < 0) { erro("Selecione na tabela."); return; }
            try { controller.editarDespesa((int) modDesp.getValueAt(r, 0), txtDescDesp.getText(), txtValDesp.getText(), cmbTipoDesp.getSelectedIndex() == 0, marcados(), chkEssencial.isSelected(), txtPrio.getText()); limparDesp(); atualizarTela(); } catch (RegraNegocioException ex) { erro(ex.getMessage()); }
        });
        JButton bEx = new JButton("Excluir");
        bEx.addActionListener(e -> {
            int r = tblDesp.getSelectedRow(); if (r < 0) { erro("Selecione na tabela."); return; }
            try { controller.excluirDespesa((int) modDesp.getValueAt(r, 0)); limparDesp(); atualizarTela(); } catch (RegraNegocioException ex) { erro(ex.getMessage()); }
        });
        JButton bLimp = new JButton("Limpar");
        bLimp.addActionListener(e -> limparDesp());
        l3.add(bInc); l3.add(bEd); l3.add(bEx); l3.add(bLimp);
        form.add(l1); form.add(l2); form.add(l3);

        pnlChecksPart = new JPanel(); pnlChecksPart.setLayout(new BoxLayout(pnlChecksPart, BoxLayout.Y_AXIS));
        JScrollPane scChecks = new JScrollPane(pnlChecksPart);
        scChecks.setBorder(BorderFactory.createTitledBorder("Envolvidos")); scChecks.setPreferredSize(new Dimension(220, 120));
        topo.add(form, BorderLayout.CENTER); topo.add(scChecks, BorderLayout.EAST);

        modDesp = new DefaultTableModel(new Object[]{"ID", "Descrição", "Tipo", "Valor Base", "Custo Total", "Classificação", "Participantes"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblDesp = new JTable(modDesp);
        tblDesp.getSelectionModel().addListSelectionListener(e -> {
            int r = tblDesp.getSelectedRow();
            if (r >= 0) {
                int id = (int) modDesp.getValueAt(r, 0);
                for (Despesa d : controller.getPlanejamento().listarDespesas()) {
                    if (d.getId() == id) {
                        txtDescDesp.setText(d.getDescricao()); txtValDesp.setText(fmtNum(d.getValorCentavos()));
                        cmbTipoDesp.setSelectedIndex(d instanceof DespesaFixa ? 0 : 1);
                        chkEssencial.setSelected(d.isEssencial()); txtPrio.setEnabled(!d.isEssencial());
                        txtPrio.setText(d.isEssencial() ? "1" : "" + d.getPrioridade());
                        for (JCheckBox cb : mapChecks.values()) cb.setSelected(false);
                        for (Participante part : d.getParticipantes()) { JCheckBox cb = mapChecks.get(part.getCodigo()); if (cb != null) cb.setSelected(true); }
                        break;
                    }
                }
            }
        });
        p.add(topo, BorderLayout.NORTH); p.add(new JScrollPane(tblDesp), BorderLayout.CENTER);
        return p;
    }
    private List<Participante> marcados() {
        List<Participante> sel = new ArrayList<>();
        for (Map.Entry<Integer, JCheckBox> e : mapChecks.entrySet()) if (e.getValue().isSelected()) sel.add(controller.getPlanejamento().buscarParticipante(e.getKey()));
        return sel;
    }

    private void limparDesp() {
        txtDescDesp.setText(""); txtValDesp.setText(""); cmbTipoDesp.setSelectedIndex(0); chkEssencial.setSelected(true); txtPrio.setText("1"); txtPrio.setEnabled(false);
        for (JCheckBox cb : mapChecks.values()) cb.setSelected(false);
        tblDesp.clearSelection();
    }

    private JPanel criarAbaResumo() {
        JPanel p = new JPanel(new GridLayout(2, 1, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        modResumo = new DefaultTableModel(new Object[]{"Código", "Participante", "Orçamento", "Total Gasto", "Saldo/Excesso", "Situação"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblResumo = new JTable(modResumo);
        txtDetalhe = new JTextArea(); txtDetalhe.setEditable(false); txtDetalhe.setFont(new Font("Monospaced", Font.PLAIN, 12));
        p.add(new JScrollPane(tblResumo)); p.add(new JScrollPane(txtDetalhe));
        return p;
    }

    private JPanel criarAbaSimulador() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        JButton bSim = new JButton("Executar Simulação Ótima (Preserva o Original)");
        bSim.addActionListener(e -> { try { mostrarSimulacao(controller.simular()); } catch (RegraNegocioException ex) { erro(ex.getMessage()); } });
        txtSimulacao = new JTextArea(); txtSimulacao.setEditable(false); txtSimulacao.setFont(new Font("Monospaced", Font.PLAIN, 12));
        p.add(bSim, BorderLayout.NORTH); p.add(new JScrollPane(txtSimulacao), BorderLayout.CENTER);
        return p;
    }

    private void mostrarSimulacao(ResultadoSimulacao res) {
        StringBuilder sb = new StringBuilder();
        sb.append("STATUS: ").append(res.getMensagem()).append("\n\nMANTIDAS:\n");
        for (Despesa d : res.getDespesasMantidas()) sb.append(" - ").append(d.getDescricao()).append(d.isEssencial() ? " [ESSENCIAL]\n" : " [OPCIONAL Prio " + d.getPrioridade() + "]\n");
        sb.append("\nRETIRADAS:\n");
        if (res.getDespesasRetiradas().isEmpty()) sb.append(" (Nenhuma)\n");
        for (Despesa d : res.getDespesasRetiradas()) sb.append(" - ").append(d.getDescricao()).append(" [OPCIONAL Prio ").append(d.getPrioridade()).append("]\n");
        sb.append("\nTOTAIS SIMULADOS:\n");
        for (Map.Entry<Participante, Long> e : res.getTotaisIndividuaisCentavos().entrySet()) sb.append(" ").append(e.getKey().getNome()).append(": ").append(fmtMoeda(e.getValue())).append("\n");
        sb.append("\nTOTAL GERAL SIMULADO: ").append(fmtMoeda(res.getCustoTotalGeralCentavos()));
        txtSimulacao.setText(sb.toString());
    }

    private void acaoSalvar() {
        JFileChooser fc = new JFileChooser(new File("dados"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile(); if (!f.getName().contains(".")) f = new File(f.getAbsolutePath() + ".ser");
            try { controller.salvarArquivo(f); JOptionPane.showMessageDialog(this, "Salvo com sucesso!"); } catch (RegraNegocioException ex) { erro(ex.getMessage()); }
        }
    }

    private void acaoCarregar() {
        JFileChooser fc = new JFileChooser(new File("dados"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try { controller.carregarArquivo(fc.getSelectedFile()); limparPart(); limparDesp(); txtSimulacao.setText(""); atualizarTela(); } catch (RegraNegocioException ex) { erro(ex.getMessage()); }
        }
    }

    private void atualizarTela() {
        Planejamento p = controller.getPlanejamento();
        txtTitulo.setText(p.getTitulo());
        lblTotalTopo.setText("Custo Total: " + fmtMoeda(p.calcularCustoTotal()));

        modPart.setRowCount(0); pnlChecksPart.removeAll(); mapChecks.clear();
        List<Participante> parts = p.listarParticipantes();
        for (Participante part : parts) {
            modPart.addRow(new Object[]{part.getCodigo(), part.getNome(), fmtMoeda(part.getOrcamentoCentavos())});
            JCheckBox cb = new JCheckBox(part.getCodigo() + " - " + part.getNome());
            mapChecks.put(part.getCodigo(), cb); pnlChecksPart.add(cb);
        }
        pnlChecksPart.revalidate(); pnlChecksPart.repaint();

        modDesp.setRowCount(0);
        for (Despesa d : p.listarDespesas()) {
            StringBuilder n = new StringBuilder();
            for (Participante part : d.getParticipantes()) { if (n.length() > 0) n.append(", "); n.append(part.getNome()); }
            modDesp.addRow(new Object[]{d.getId(), d.getDescricao(), d.getTipo(), fmtMoeda(d.getValorCentavos()), fmtMoeda(d.calcularTotal()), d.isEssencial() ? "Essencial" : "Opcional (P" + d.getPrioridade() + ")", n.toString()});
        }

        modResumo.setRowCount(0);
        for (Participante part : parts) {
            long g = p.calcularGastoParticipante(part), s = p.calcularSaldo(part);
            modResumo.addRow(new Object[]{part.getCodigo(), part.getNome(), fmtMoeda(part.getOrcamentoCentavos()), fmtMoeda(g), (s >= 0 ? "+" : "-") + fmtMoeda(Math.abs(s)), s >= 0 ? "DENTRO DO LIMITE" : "EXCEDEU EM " + fmtMoeda(Math.abs(s))});
        }

        StringBuilder det = new StringBuilder("DETALHAMENTO DO RATEIO:\n");
        for (Despesa d : p.listarDespesas()) {
            det.append("• ").append(d.getDescricao()).append(" (").append(d.getTipo()).append("): ");
            for (Participante part : parts) det.append(part.getNome()).append("=").append(fmtMoeda(d.getValorParticipante(part))).append("  ");
            det.append("\n");
        }
        txtDetalhe.setText(det.toString());
    }
}
