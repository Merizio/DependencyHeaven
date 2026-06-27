# Desenvolvimento Orientado a Testes (TDD) no Back-end

O **TDD (Test-Driven Development)** inverte a lógica tradicional: nós escrevemos o teste *antes* de escrever o código que faz ele passar. Isso garante que:
1. Nós entendemos perfeitamente a regra de negócio antes de codificar.
2. O código fica protegido contra regressões (se alguém quebrar a lógica no futuro, o teste avisa na hora).

## O Ciclo do TDD (Red, Green, Refactor)

1. **🔴 Red:** Escreva um teste para uma regra de negócio que ainda não foi implementada. O teste vai falhar (ou nem compilar).
2. **🟢 Green:** Escreva o código mais simples possível na classe de negócio (`Service` ou domínio) apenas para fazer o teste passar.
3. **🔵 Refactor:** Melhore o código, tire duplicações e deixe limpo, garantindo que o teste continua verde.

---

## Exemplo Amigável: Regra do Estado `BLOQUEADO`

Nossa regra de negócio diz: *"Ao adicionar uma dependência não finalizada em uma tarefa, o estado dela deve ir para `BLOQUEADO`."*

Veja como o dev do Back-end deveria começar (escrevendo o teste na pasta `src/test/java/...`):

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TarefaTest {

    @Test
    void deveMudarEstadoParaBloqueadoQuandoAdicionarDependenciaNaoFinalizada() {
        // 1. Arrange (Preparação do cenário)
        Tarefa tarefaPrincipal = new Tarefa("Fazer Deploy");
        tarefaPrincipal.setEstado(Estado.PENDENTE);
        
        Tarefa tarefaDependencia = new Tarefa("Configurar Docker");
        tarefaDependencia.setEstado(Estado.EM_ANDAMENTO); // Não está finalizada!

        // 2. Act (Ação que estamos testando)
        tarefaPrincipal.adicionarDependencia(tarefaDependencia);

        // 3. Assert (Verificação do resultado)
        assertEquals(Estado.BLOQUEADO, tarefaPrincipal.getEstado(), 
            "A tarefa deveria ficar BLOQUEADA porque a dependência não está FINALIZADA");
    }
    
    @Test
    void deveManterPendenteQuandoAdicionarDependenciaJaFinalizada() {
        // 1. Arrange
        Tarefa tarefaPrincipal = new Tarefa("Fazer Deploy");
        tarefaPrincipal.setEstado(Estado.PENDENTE);
        
        Tarefa tarefaDependencia = new Tarefa("Configurar Docker");
        tarefaDependencia.setEstado(Estado.FINALIZADO); // Já está finalizada!

        // 2. Act
        tarefaPrincipal.adicionarDependencia(tarefaDependencia);

        // 3. Assert
        assertEquals(Estado.PENDENTE, tarefaPrincipal.getEstado(), 
            "A tarefa deveria continuar PENDENTE, pois a dependência já terminou");
    }
}
```

### Como proceder a partir daqui?
1. O desenvolvedor tentará rodar isso. Vai dar erro porque o método `adicionarDependencia` provavelmente ainda nem existe, ou não tem lógica nenhuma.
2. O desenvolvedor vai lá na classe `Tarefa` e implementa:
   ```java
   public void adicionarDependencia(Tarefa dependencia) {
       this.dependencias.add(dependencia);
       if (dependencia.getEstado() != Estado.FINALIZADO) {
           this.estado = Estado.BLOQUEADO;
       }
   }
   ```
3. Roda o teste novamente: **Sucesso! 🟢**

Isso ajuda demais o Back-end a construir um sistema super seguro e livre de bugs no grafo de dependências!

---

## Como Executar os Testes

Para garantir que o código recém-escrito ou refatorado não quebrou nada, execute os testes utilizando o Maven Wrapper que já está no projeto.

Abra o terminal na raiz do projeto e rode:

```bash
# Executa todos os testes do projeto
./mvnw test

# (No Windows)
mvnw.cmd test
```

O Maven baixará as dependências necessárias, executará a suíte completa de testes usando JUnit e mostrará um relatório no terminal indicando se os testes passaram (**BUILD SUCCESS**) ou falharam (**BUILD FAILURE**).
