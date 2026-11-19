package model;

import exception.RegraNegocioException;
import java.util.*;

public class DespesaPorPessoa extends Despesa {
    private static final long serialVersionUID = 1L;

    public DespesaPorPessoa(int id, String descricao, long valorCentavos, List<Participante> participantes, boolean essencial, int prioridade) throws RegraNegocioException {
        super(id, descricao, valorCentavos, participantes, essencial, prioridade);
    }

    @Override
    public Map<Participante, Long> calcularRateio() {
        List<Participante> lista = getParticipantes();
        Collections.sort(lista);
        Map<Participante, Long> rateio = new LinkedHashMap<>();
        for (Participante p : lista) rateio.put(p, getValorCentavos());
        return rateio;
    }

    @Override
    public String getTipo() { return "Por pessoa"; }
}