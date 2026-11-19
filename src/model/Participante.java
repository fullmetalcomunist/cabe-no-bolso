package model;

import exception.RegraNegocioException;
import java.io.Serializable;
import java.util.Objects;

public class Participante implements Serializable, Comparable<Participante> {
    private static final long serialVersionUID = 1L;

    private final int codigo;
    private String nome;
    private long orcamentoCentavos;

    public Participante(int codigo, String nome, long orcamentoCentavos) throws RegraNegocioException {
        if (codigo <= 0) {
            throw new RegraNegocioException("O código do participante deve ser maior que zero.");
        }
        this.codigo = codigo;
        setNome(nome);
        setOrcamentoCentavos(orcamentoCentavos);
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) throws RegraNegocioException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome do participante não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public long getOrcamentoCentavos() {
        return orcamentoCentavos;
    }

    public void setOrcamentoCentavos(long orcamentoCentavos) throws RegraNegocioException {
        if (orcamentoCentavos < 0) {
            throw new RegraNegocioException("O orçamento não pode ser negativo.");
        }
        this.orcamentoCentavos = orcamentoCentavos;
    }

    @Override
    public int compareTo(Participante outro) {
        return Integer.compare(this.codigo, outro.codigo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participante)) return false;
        Participante that = (Participante) o;
        return codigo == that.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + nome;
    }
}
