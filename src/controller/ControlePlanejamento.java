package controller;

import exception.RegraNegocioException;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import model.*;
import persistence.PersistenciaPlanejamento;

public class ControlePlanejamento {
    private Planejamento planejamento = new Planejamento("Domingo no Beach Park");
    private final PersistenciaPlanejamento persistencia = new PersistenciaPlanejamento();

    public Planejamento getPlanejamento() { return planejamento; }

    public void novoPlanejamento(String titulo) throws RegraNegocioException {
        if (titulo == null || titulo.trim().isEmpty()) throw new RegraNegocioException("Informe um título.");
        this.planejamento = new Planejamento(titulo);
    }

    public void alterarTitulo(String titulo) throws RegraNegocioException { planejamento.setTitulo(titulo); }

    public long paraCentavos(String texto, String campo) throws RegraNegocioException {
        if (texto == null || texto.trim().isEmpty()) throw new RegraNegocioException("Preencha o campo " + campo + ".");
        try {
            BigDecimal bd = new BigDecimal(texto.trim().replace("R$", "").replace(" ", "").replace(",", "."));
            return bd.multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).longValueExact();
        } catch (Exception e) {
            throw new RegraNegocioException("Valor inválido em " + campo + ".");
        }
    }

    public int paraInteiro(String texto, String campo) throws RegraNegocioException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (Exception e) {
            throw new RegraNegocioException("Número inválido em " + campo + ".");
        }
    }

    public void cadastrarParticipante(String cod, String nome, String orc) throws RegraNegocioException {
        planejamento.adicionarParticipante(paraInteiro(cod, "Código"), nome, paraCentavos(orc, "Orçamento"));
    }

    public void editarParticipante(int cod, String nome, String orc) throws RegraNegocioException {
        planejamento.editarParticipante(cod, nome, paraCentavos(orc, "Orçamento"));
    }

    public void excluirParticipante(int cod) throws RegraNegocioException { planejamento.removerParticipante(cod); }

    public void cadastrarDespesa(String desc, String val, boolean fixa, List<Participante> env, boolean ess, String prio) throws RegraNegocioException {
        planejamento.adicionarDespesa(desc, paraCentavos(val, "Valor"), fixa, env, ess, ess ? 0 : paraInteiro(prio, "Prioridade"));
    }

    public void editarDespesa(int id, String desc, String val, boolean fixa, List<Participante> env, boolean ess, String prio) throws RegraNegocioException {
        planejamento.editarDespesa(id, desc, paraCentavos(val, "Valor"), fixa, env, ess, ess ? 0 : paraInteiro(prio, "Prioridade"));
    }

    public void excluirDespesa(int id) throws RegraNegocioException { planejamento.removerDespesa(id); }
    public void salvarArquivo(File arq) throws RegraNegocioException { persistencia.salvar(planejamento, arq); }
    public void carregarArquivo(File arq) throws RegraNegocioException { this.planejamento = persistencia.carregar(arq); }
    public ResultadoSimulacao simular() throws RegraNegocioException { return SimuladorAlternativas.simular(planejamento); }
}