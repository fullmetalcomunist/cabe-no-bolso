package model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResultadoSimulacao {
    private final boolean viavel;
    private final String mensagem;
    private final List<Despesa> despesasMantidas;
    private final List<Despesa> despesasRetiradas;
    private final int somaPrioridades;
    private final Map<Participante, Long> totaisIndividuaisCentavos;
    private final long custoTotalGeralCentavos;

    public ResultadoSimulacao(
            boolean viavel,
            String mensagem,
            List<Despesa> despesasMantidas,
            List<Despesa> despesasRetiradas,
            int somaPrioridades,
            Map<Participante, Long> totaisIndividuaisCentavos,
            long custoTotalGeralCentavos) {
        this.viavel = viavel;
        this.mensagem = mensagem;
        this.despesasMantidas = Collections.unmodifiableList(despesasMantidas);
        this.despesasRetiradas = Collections.unmodifiableList(despesasRetiradas);
        this.somaPrioridades = somaPrioridades;
        this.totaisIndividuaisCentavos = Collections.unmodifiableMap(totaisIndividuaisCentavos);
        this.custoTotalGeralCentavos = custoTotalGeralCentavos;
    }

    public boolean isViavel() {
        return viavel;
    }

    public String getMensagem() {
        return mensagem;
    }

    public List<Despesa> getDespesasMantidas() {
        return despesasMantidas;
    }

    public List<Despesa> getDespesasRetiradas() {
        return despesasRetiradas;
    }

    public int getSomaPrioridades() {
        return somaPrioridades;
    }

    public Map<Participante, Long> getTotaisIndividuaisCentavos() {
        return totaisIndividuaisCentavos;
    }

    public long getCustoTotalGeralCentavos() {
        return custoTotalGeralCentavos;
    }
}
