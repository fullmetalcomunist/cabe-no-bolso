package model;

import exception.RegraNegocioException;
import java.io.Serializable;
import java.util.*;

public abstract class Despesa implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final String descricao;
    private final long valorCentavos;
    private final Set<Participante> participantes;
    private final boolean essencial;
    private final int prioridade;

    public Despesa(int id, String descricao, long valorCentavos, List<Participante> participantes, boolean essencial, int prioridade) throws RegraNegocioException {
        if (descricao == null || descricao.trim().isEmpty()) throw new RegraNegocioException("Informe a descrição da despesa.");
        if (valorCentavos <= 0) throw new RegraNegocioException("O valor deve ser maior que zero.");
        if (participantes == null || participantes.isEmpty()) throw new RegraNegocioException("Selecione pelo menos um participante.");
        if (!essencial && prioridade <= 0) throw new RegraNegocioException("A prioridade deve ser maior que zero.");

        this.id = id;
        this.descricao = descricao.trim();
        this.valorCentavos = valorCentavos;
        this.participantes = new LinkedHashSet<>(participantes);
        this.essencial = essencial;
        this.prioridade = essencial ? 0 : prioridade;
    }

    public abstract Map<Participante, Long> calcularRateio();
    public abstract String getTipo();

    public long calcularTotal() {
        long soma = 0;
        for (long v : calcularRateio().values()) soma += v;
        return soma;
    }

    public long getValorParticipante(Participante p) {
        return calcularRateio().getOrDefault(p, 0L);
    }

    public int getId() { return id; }
    public String getDescricao() { return descricao; }
    public long getValorCentavos() { return valorCentavos; }
    public List<Participante> getParticipantes() { return new ArrayList<>(participantes); }
    public boolean isEssencial() { return essencial; }
    public int getPrioridade() { return prioridade; }
}