package com.ProjetoIntegrado.DependancyHeaven.repository;

import com.ProjetoIntegrado.DependancyHeaven.domain.Estado;
import com.ProjetoIntegrado.DependancyHeaven.domain.Membro;
import com.ProjetoIntegrado.DependancyHeaven.domain.Tarefa;
import com.ProjetoIntegrado.DependancyHeaven.domain.Template;
import com.ProjetoIntegrado.DependancyHeaven.domain.Usuario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EntidadeJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private MembroRepository membroRepository;

    @Autowired
    private TarefaRepository tarefaRepository;

    @Test
    void deveSalvarERecuperarUsuario() {
        Usuario usuario = new Usuario("Davi", "davi@email.com", "senha123");
        entityManager.persistAndFlush(usuario);

        Usuario encontrado = usuarioRepository.findById(usuario.getId()).orElse(null);

        assertNotNull(encontrado);
        assertNotNull(encontrado.getId());
        assertEquals("Davi", encontrado.getNome());
        assertEquals("davi@email.com", encontrado.getEmail());
    }

    @Test
    void deveSalvarTemplateComUsuario() {
        Usuario usuario = new Usuario("Paula", "paula@email.com", "senha456");
        entityManager.persistAndFlush(usuario);

        Template template = new Template("Projeto de Migração");
        template.setUsuario(usuario);
        entityManager.persistAndFlush(template);

        Template encontrado = templateRepository.findById(template.getId()).orElse(null);

        assertNotNull(encontrado);
        assertNotNull(encontrado.getId());
        assertEquals("Projeto de Migração", encontrado.getNome());
        assertEquals(usuario.getId(), encontrado.getUsuario().getId());
    }

    @Test
    void deveSalvarMembro() {
        Membro membro = new Membro("Carlos");
        entityManager.persistAndFlush(membro);

        Membro encontrado = membroRepository.findById(membro.getId()).orElse(null);

        assertNotNull(encontrado);
        assertNotNull(encontrado.getId());
        assertEquals("Carlos", encontrado.getNome());
    }

    @Test
    void deveSalvarTarefaComEstadoPendentePorPadrao() {
        Usuario usuario = new Usuario("Ana", "ana@email.com", "senha789");
        entityManager.persistAndFlush(usuario);

        Template template = new Template("Template Teste");
        template.setUsuario(usuario);
        entityManager.persistAndFlush(template);

        Tarefa tarefa = new Tarefa("Configurar Banco");
        tarefa.setDescricao("Instalar e configurar o PostgreSQL");
        tarefa.setTemplate(template);
        entityManager.persistAndFlush(tarefa);

        Tarefa encontrada = tarefaRepository.findById(tarefa.getId()).orElse(null);

        assertNotNull(encontrada);
        assertNotNull(encontrada.getId());
        assertEquals("Configurar Banco", encontrada.getTitulo());
        assertEquals("Instalar e configurar o PostgreSQL", encontrada.getDescricao());
        assertEquals(Estado.PENDENTE, encontrada.getEstado());
    }

    @Test
    void deveSalvarTarefaComMembros() {
        Usuario usuario = new Usuario("João", "joao@email.com", "senhaABC");
        entityManager.persistAndFlush(usuario);

        Template template = new Template("Template Membros");
        template.setUsuario(usuario);
        entityManager.persistAndFlush(template);

        Membro membro1 = new Membro("Davi");
        Membro membro2 = new Membro("Paula");
        entityManager.persistAndFlush(membro1);
        entityManager.persistAndFlush(membro2);

        Tarefa tarefa = new Tarefa("Fazer Deploy");
        tarefa.setTemplate(template);
        tarefa.getMembros().add(membro1);
        tarefa.getMembros().add(membro2);
        entityManager.persistAndFlush(tarefa);

        entityManager.clear();

        Tarefa encontrada = tarefaRepository.findById(tarefa.getId()).orElse(null);

        assertNotNull(encontrada);
        assertEquals(2, encontrada.getMembros().size());
    }

    @Test
    void deveSalvarTarefaComDependencias() {
        Usuario usuario = new Usuario("Maria", "maria@email.com", "senhaDEF");
        entityManager.persistAndFlush(usuario);

        Template template = new Template("Template Deps");
        template.setUsuario(usuario);
        entityManager.persistAndFlush(template);

        Tarefa tarefaA = new Tarefa("Configurar Docker");
        tarefaA.setTemplate(template);
        tarefaA.setEstado(Estado.FINALIZADO);
        entityManager.persistAndFlush(tarefaA);

        Tarefa tarefaB = new Tarefa("Configurar CI");
        tarefaB.setTemplate(template);
        tarefaB.setEstado(Estado.PENDENTE);
        entityManager.persistAndFlush(tarefaB);

        Tarefa tarefaC = new Tarefa("Fazer Deploy");
        tarefaC.setTemplate(template);
        tarefaC.getDependencias().add(tarefaA);
        tarefaC.getDependencias().add(tarefaB);
        entityManager.persistAndFlush(tarefaC);

        entityManager.clear();

        Tarefa encontrada = tarefaRepository.findById(tarefaC.getId()).orElse(null);

        assertNotNull(encontrada);
        assertEquals(2, encontrada.getDependencias().size());
    }
}
