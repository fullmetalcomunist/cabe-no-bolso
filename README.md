# Cabe no Bolso — Planejador de Despesas Compartilhadas

Aplicação desktop desenvolvida em Java (JDK 17+) com interface gráfica em **Java Swing** e arquitetura **MVC**, destinada ao planejamento de despesas compartilhadas em grupo, controle de limites individuais de orçamento e simulação de alternativas viáveis.

## Autor
* **José Airton Jorge Ferreira da Silva** — Matrícula: 2640874

## Requisitos do Ambiente
* **JDK:** Java 17 ou superior.
* **Dependências:** Apenas bibliotecas padrão da plataforma Java SE (javax.swing, java.awt, java.io, java.util, java.math).

## Estrutura de Pastas e Pacotes
* dados/caso_referencia.ser — Arquivo serializado do caso oficial de validação
* dados/domingo_beach_park.ser — Arquivo serializado do cenário de exemplo (Domingo no Beach Park)
* docs/diagrama_classes.puml — Código-fonte da modelagem UML em PlantUML
* src/app/ — Main.java
* src/controller/ — ControlePlanejamento.java
* src/exception/ — RegraNegocioException.java
* src/model/ — Planejamento.java, Participante.java, Despesa.java, DespesaFixa.java, DespesaPorPessoa.java, SimuladorAlternativas.java, ResultadoSimulacao.java
* src/persistence/ — PersistenciaPlanejamento.java
* src/view/ — TelaPrincipal.java

## Compilação e Execução via Terminal (PowerShell)

1. **Compilar o projeto:**
``` powershell
mkdir -Force bin
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse -Filter *.java src).FullName
```

2. **Executar a aplicação gráfica (Swing):**
``` powershell
java -cp bin app.Main
```
*(Ao iniciar, o sistema carrega dados/caso_referencia.ser. Para abrir o cenário do Beach Park, clique em **Carregar...** e selecione dados/domingo_beach_park.ser.)*