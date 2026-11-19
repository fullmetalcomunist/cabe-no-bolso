package model;

import exception.RegraNegocioException;
import java.util.*;

public class DespesaFixa extends Despesa {
    private static final long serialVersionUID = 1L;

    public DespesaFixa(int id, String descricao, long valorCentavos, List<Participante> participantes, boolean essencial, int prioridade) throws RegraNegocioException {
        super(id, descricao, valorCentavos, participantes, essencial, prioridade);
    }

    @Override
    public Map<Participante, Long> calcularRateio() {
        List<Participante> lista = getParticipantes();
        Collections.sort(lista);
        int qtd = lista.size();
        long base = getValorCentavos() / qtd;
        long resto = getValorCentavos() % qtd;

        Map<Participante, Long> rateio = new LinkedHashMap<>();
        for (int i = 0; i < qtd; i++) {
            rateio.put(lista.get(i), base + (i < resto ? 1L : 0L));
        }
        return rateio;
    }

    @Override
    public String getTipo() { return "Fixa"; }
}