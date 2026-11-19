package persistence;

import exception.RegraNegocioException;
import java.io.*;
import model.Planejamento;

public class PersistenciaPlanejamento {
    public void salvar(Planejamento planejamento, File arquivo) throws RegraNegocioException {
        try {
            if (arquivo.getParentFile() != null) arquivo.getParentFile().mkdirs();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(arquivo))) {
                out.writeObject(planejamento);
            }
        } catch (IOException e) {
            throw new RegraNegocioException("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }

    public Planejamento carregar(File arquivo) throws RegraNegocioException {
        if (arquivo == null || !arquivo.exists()) {
            throw new RegraNegocioException("Arquivo não encontrado.");
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(arquivo))) {
            return (Planejamento) in.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new RegraNegocioException("Arquivo inválido ou corrompido.");
        }
    }
}