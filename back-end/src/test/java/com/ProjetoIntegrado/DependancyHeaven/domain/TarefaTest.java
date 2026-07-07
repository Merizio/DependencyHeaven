package com.ProjetoIntegrado.DependancyHeaven.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TarefaTest {

    private Tarefa tarefa;

    @BeforeEach
    void setUp() {
        tarefa = new Tarefa("Tarefa Principal");
    }

    // =========================================================================
    // Transição para EM_ANDAMENTO (iniciar)
    // =========================================================================
    @Nested
    class IniciarTarefa {

        @Test
        void deveIniciarTarefaQuandoTiverMembrosEStatusPendente() {
            tarefa.getMembros().add(new Membro("Davi"));

            tarefa.iniciar();

            assertEquals(Estado.EM_ANDAMENTO, tarefa.getEstado());
        }

        @Test
        void naoDeveIniciarTarefaSemMembros() {
            // tarefa sem membros
            assertThrows(IllegalStateException.class, () -> tarefa.iniciar(),
                "Deveria lançar exceção ao iniciar tarefa sem membros");
        }

        @Test
        void naoDeveIniciarTarefaBloqueada() {
            tarefa.getMembros().add(new Membro("Davi"));
            tarefa.setEstado(Estado.BLOQUEADO);

            assertThrows(IllegalStateException.class, () -> tarefa.iniciar(),
                "Deveria lançar exceção ao iniciar tarefa bloqueada");
        }
    }

    // =========================================================================
    // Adicionar Dependência
    // =========================================================================
    @Nested
    class AdicionarDependencia {

        @Test
        void deveFicarBloqueadaAoAdicionarDependenciaNaoFinalizada() {
            Tarefa dependencia = new Tarefa("Configurar Docker");
            dependencia.setEstado(Estado.EM_ANDAMENTO);

            tarefa.adicionarDependencia(dependencia);

            assertEquals(Estado.BLOQUEADO, tarefa.getEstado(),
                "A tarefa deveria ficar BLOQUEADA porque a dependência não está FINALIZADA");
            assertTrue(tarefa.getDependencias().contains(dependencia));
        }

        @Test
        void deveFicarPendenteAoAdicionarDependenciaJaFinalizada() {
            Tarefa dependencia = new Tarefa("Configurar Docker");
            dependencia.setEstado(Estado.FINALIZADO);

            tarefa.adicionarDependencia(dependencia);

            assertEquals(Estado.PENDENTE, tarefa.getEstado(),
                "A tarefa deveria continuar PENDENTE, pois a dependência já terminou");
            assertTrue(tarefa.getDependencias().contains(dependencia));
        }

        @Test
        void deveBloquearTarefaEmAndamentoAoAdicionarDependenciaNaoFinalizada() {
            tarefa.getMembros().add(new Membro("Davi"));
            tarefa.iniciar();
            assertEquals(Estado.EM_ANDAMENTO, tarefa.getEstado());

            Tarefa dependencia = new Tarefa("Configurar Docker");
            dependencia.setEstado(Estado.PENDENTE);

            tarefa.adicionarDependencia(dependencia);

            assertEquals(Estado.BLOQUEADO, tarefa.getEstado(),
                "Tarefa EM_ANDAMENTO deveria ir para BLOQUEADO ao receber dependência não finalizada");
        }

        @Test
        void naoDeveAdicionarDependenciaNula() {
            assertThrows(IllegalArgumentException.class, () -> tarefa.adicionarDependencia(null),
                "Deveria lançar exceção ao adicionar dependência nula");
        }

        @Test
        void naoDeveDuplicarDependencia() {
            Tarefa dependencia = new Tarefa("Dependência");
            
            tarefa.adicionarDependencia(dependencia);
            tarefa.adicionarDependencia(dependencia); // Tentativa de adicionar de novo

            assertEquals(1, tarefa.getDependencias().size(), "Não deveria duplicar a dependência na lista");
            assertEquals(1, dependencia.getDependentes().size(), "Não deveria duplicar a tarefa na lista de dependentes");
        }
    }

    // =========================================================================
    // Validação de Ciclo (DAG)
    // =========================================================================
    @Nested
    class ValidacaoDag {

        @Test
        void naoDevePermitirDependenciaCiclicaDireta() {
            Tarefa tarefaA = new Tarefa("Tarefa A");
            Tarefa tarefaB = new Tarefa("Tarefa B");

            tarefaA.adicionarDependencia(tarefaB);

            assertThrows(IllegalStateException.class, () -> tarefaB.adicionarDependencia(tarefaA),
                "Deveria lançar exceção para dependência cíclica direta: A->B, B->A");
        }

        @Test
        void naoDevePermitirDependenciaCiclicaIndireta() {
            Tarefa tarefaA = new Tarefa("Tarefa A");
            Tarefa tarefaB = new Tarefa("Tarefa B");
            Tarefa tarefaC = new Tarefa("Tarefa C");

            tarefaA.adicionarDependencia(tarefaB);
            tarefaB.adicionarDependencia(tarefaC);

            assertThrows(IllegalStateException.class, () -> tarefaC.adicionarDependencia(tarefaA),
                "Deveria lançar exceção para dependência cíclica indireta: A->B->C, C->A");
        }

        @Test
        void devePermitirDependenciaSemCiclo() {
            Tarefa tarefaA = new Tarefa("Tarefa A");
            Tarefa tarefaB = new Tarefa("Tarefa B");
            Tarefa tarefaC = new Tarefa("Tarefa C");

            // A depende de B, A depende de C (sem ciclo)
            assertDoesNotThrow(() -> {
                tarefaA.adicionarDependencia(tarefaB);
                tarefaA.adicionarDependencia(tarefaC);
            });
        }
    }

    // =========================================================================
    // Finalizar Tarefa (propagar desbloqueio)
    // =========================================================================
    @Nested
    class FinalizarTarefa {

        @Test
        void deveDesbloquearDependentesQuandoTodasDependenciasFinalizadas() {
            Tarefa dependencia1 = new Tarefa("Dep 1");
            dependencia1.setEstado(Estado.FINALIZADO);

            Tarefa dependencia2 = new Tarefa("Dep 2");
            dependencia2.setEstado(Estado.EM_ANDAMENTO);

            Tarefa dependente = new Tarefa("Dependente");
            dependente.adicionarDependencia(dependencia1);
            dependente.adicionarDependencia(dependencia2);
            assertEquals(Estado.BLOQUEADO, dependente.getEstado());

            // Agora finalizamos a dep2
            dependencia2.getMembros().add(new Membro("Davi"));
            dependencia2.finalizar();

            assertEquals(Estado.PENDENTE, dependente.getEstado(),
                "Dependente deveria ir para PENDENTE após todas as dependências serem finalizadas");
        }

        @Test
        void naoDeveDesbloquearSeAindaHaDependenciaPendente() {
            Tarefa dependencia1 = new Tarefa("Dep 1");
            dependencia1.setEstado(Estado.EM_ANDAMENTO);

            Tarefa dependencia2 = new Tarefa("Dep 2");
            dependencia2.setEstado(Estado.EM_ANDAMENTO);

            Tarefa dependente = new Tarefa("Dependente");
            dependente.adicionarDependencia(dependencia1);
            dependente.adicionarDependencia(dependencia2);
            assertEquals(Estado.BLOQUEADO, dependente.getEstado());

            // Finalizamos apenas dep1
            dependencia1.getMembros().add(new Membro("Davi"));
            dependencia1.finalizar();

            assertEquals(Estado.BLOQUEADO, dependente.getEstado(),
                "Dependente deveria continuar BLOQUEADO pois dep2 ainda não foi finalizada");
        }
    }

    // =========================================================================
    // Reabrir Tarefa (propagar bloqueio)
    // =========================================================================
    @Nested
    class ReabrirTarefa {

        @Test
        void deveBloquearDependentesQuandoReaberta() {
            Tarefa dependencia = new Tarefa("Dep 1");
            dependencia.setEstado(Estado.FINALIZADO);

            Tarefa dependente = new Tarefa("Dependente");
            dependente.adicionarDependencia(dependencia);
            assertEquals(Estado.PENDENTE, dependente.getEstado(),
                "Deveria estar PENDENTE pois dependência já estava FINALIZADA");

            // Reabre a dependência
            dependencia.reabrir();

            assertEquals(Estado.BLOQUEADO, dependente.getEstado(),
                "Dependente deveria voltar a BLOQUEADO após a dependência ser reaberta");
        }

        @Test
        void deveBloquearMultiplosDependentesQuandoReaberta() {
            Tarefa dependencia = new Tarefa("Dep Compartilhada");
            dependencia.setEstado(Estado.FINALIZADO);

            Tarefa dependente1 = new Tarefa("Dependente 1");
            dependente1.adicionarDependencia(dependencia);

            Tarefa dependente2 = new Tarefa("Dependente 2");
            dependente2.adicionarDependencia(dependencia);

            assertEquals(Estado.PENDENTE, dependente1.getEstado());
            assertEquals(Estado.PENDENTE, dependente2.getEstado());

            // Reabre
            dependencia.reabrir();

            assertEquals(Estado.BLOQUEADO, dependente1.getEstado());
            assertEquals(Estado.BLOQUEADO, dependente2.getEstado());
        }
    }
}
