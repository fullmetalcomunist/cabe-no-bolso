package model;

import exception.RegraNegocioException;
import java.io.Serializable;
import java.util.*;

public class Planejamento implements Serializable {
    private static final long serialVersionUID = 1L;
    private String titulo;
    private final Map<Integer, Participante> participantes = new LinkedHashMap<>();
    private final Map<Integer, Despesa> despesas = new LinkedHashMap<>();
    private int proximoId = 1;

    public Planejamento(String titulo) {
        this.titulo = (titulo == null || titulo.trim().isEmpty()) ? "Domingo no Beach Park" : titulo.trim();
    }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) throws RegraNegocioException {
        if (titulo == null || titulo.trim().isEmpty()) throw new RegraNegocioException("Informe um título.");
        this.titulo = titulo.trim();
    }

    public void adicionarParticipante(int codigo, String nome, long orcamentoCentavos) throws RegraNegocioException {
        if (participantes.containsKey(codigo)) throw new RegraNegocioException("Código já cadastrado.");
        participantes.put(codigo, new Participante(codigo, nome, orcamentoCentavos));
    }

    public void editarParticipante(int codigo, String nome, long orcamentoCentavos) throws RegraNegocioException {
        Participante p = participantes.get(codigo);
        if (p == null) throw new RegraNegocioException("Participante não encontrado.");
        p.setNome(nome);
        p.setOrcamentoCentavos(orcamentoCentavos);
    }

    public void removerParticipante(int codigo) throws RegraNegocioException {
        Participante p = participantes.get(codigo);
        if (p == null) throw new RegraNegocioException("Participante não encontrado.");
        for (Despesa d : despesas.values()) {
            if (d.getParticipantes().contains(p)) {
                throw new RegraNegocioException("Participante vinculado à despesa: " + d.getDescricao());
            }
        }
        participantes.remove(codigo);
    }

    public Participante buscarParticipante(int codigo) { return participantes.get(codigo); }

    public List<Participante> listarParticipantes() {
        List<Participante> lista = new ArrayList<>(participantes.values());
        Collections.sort(lista);
        return lista;
    }

    public void adicionarDespesa(String desc, long valor, boolean fixa, List<Participante> env, boolean ess, int prio) throws RegraNegocioException {
        Despesa d = fixa
            ? new DespesaFixa(proximoId, desc, valor, env, ess, prio)
            : new DespesaPorPessoa(proximoId, desc, valor, env, ess, prio);
        despesas.put(proximoId++, d);
    }

    public void editarDespesa(int id, String desc, long valor, boolean fixa, List<Participante> env, boolean ess, int prio) throws RegraNegocioException {
        if (!despesas.containsKey(id)) throw new RegraNegocioException("Despesa não encontrada.");
        Despesa d = fixa
            ? new DespesaFixa(id, desc, valor, env, ess, prio)
            : new DespesaPorPessoa(id, desc, valor, env, ess, prio);
        despesas.put(id, d);
    }

    public void removerDespesa(int id) throws RegraNegocioException {
        if (despesas.remove(id) == null) throw new RegraNegocioException("Despesa não encontrada.");
    }

    public List<Despesa> listarDespesas() { return new ArrayList<>(despesas.values()); }

    public long calcularGastoParticipante(Participante p) {
        long total = 0;
        for (Despesa d : despesas.values()) total += d.getValorParticipante(p);
        return total;
    }

    public long calcularCustoTotal() {
        long total = 0;
        for (Despesa d : despesas.values()) total += d.calcularTotal();
        return total;
    }

    public long calcularSaldo(Participante p) {
        return p.getOrcamentoCentavos() - calcularGastoParticipante(p);
    }
}