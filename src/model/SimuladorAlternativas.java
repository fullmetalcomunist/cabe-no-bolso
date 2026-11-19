package model;

import exception.RegraNegocioException;
import java.util.*;

public class SimuladorAlternativas {
    public static ResultadoSimulacao simular(Planejamento planejamento) throws RegraNegocioException {
        List<Participante> participantes = planejamento.listarParticipantes();
        if (participantes.isEmpty()) throw new RegraNegocioException("Cadastre participantes antes de simular.");

        List<Despesa> essenciais = new ArrayList<>();
        List<Despesa> opcionais = new ArrayList<>();
        for (Despesa d : planejamento.listarDespesas()) {
            if (d.isEssencial()) essenciais.add(d);
            else opcionais.add(d);
        }

        if (opcionais.size() > 10) throw new RegraNegocioException("Limite máximo de 10 despesas opcionais.");

        Map<Participante, Long> base = new LinkedHashMap<>();
        for (Participante p : participantes) base.put(p, 0L);

        long totalEssenciais = 0;
        for (Despesa d : essenciais) {
            totalEssenciais += d.calcularTotal();
            for (Participante p : participantes) base.put(p, base.get(p) + d.getValorParticipante(p));
        }

        for (Participante p : participantes) {
            if (base.get(p) > p.getOrcamentoCentavos()) {
                return new ResultadoSimulacao(false, "Sem solução: despesas essenciais excedem o orçamento de " + p.getNome(), essenciais, opcionais, 0, base, totalEssenciais);
            }
        }

        int n = opcionais.size();
        int melhorCombinacao = 0, melhorPrio = -1;
        long menorCusto = Long.MAX_VALUE;

        for (int combinacao = 0; combinacao < (1 << n); combinacao++) {
            int prio = 0;
            long custo = totalEssenciais;
            Map<Participante, Long> gastos = new LinkedHashMap<>(base);

            for (int i = 0; i < n; i++) {
                if ((combinacao & (1 << i)) != 0) {
                    Despesa opc = opcionais.get(i);
                    prio += opc.getPrioridade();
                    custo += opc.calcularTotal();
                    for (Participante p : participantes) gastos.put(p, gastos.get(p) + opc.getValorParticipante(p));
                }
            }

            boolean viavel = true;
            for (Participante p : participantes) {
                if (gastos.get(p) > p.getOrcamentoCentavos()) { viavel = false; break; }
            }

            if (viavel && (prio > melhorPrio || (prio == melhorPrio && custo < menorCusto))) {
                melhorPrio = prio;
                menorCusto = custo;
                melhorCombinacao = combinacao;
            }
        }

        List<Despesa> mantidas = new ArrayList<>(essenciais);
        List<Despesa> retiradas = new ArrayList<>();
        Map<Participante, Long> finais = new LinkedHashMap<>(base);
        long totalFinal = totalEssenciais;

        for (int i = 0; i < n; i++) {
            Despesa opc = opcionais.get(i);
            if ((melhorCombinacao & (1 << i)) != 0) {
                mantidas.add(opc);
                totalFinal += opc.calcularTotal();
                for (Participante p : participantes) finais.put(p, finais.get(p) + opc.getValorParticipante(p));
            } else {
                retiradas.add(opc);
            }
        }

        return new ResultadoSimulacao(true, "Solução encontrada (Prioridade total: " + melhorPrio + ")", mantidas, retiradas, melhorPrio, finais, totalFinal);
    }
}