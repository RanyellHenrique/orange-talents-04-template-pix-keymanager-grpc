package br.com.zup.ranyell.keymanager.pix

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoDeChaveTest {

    @Nested
    inner class ALEATORIA {
        @Test
        internal fun `deve retornar false quando a chave existir`() {
            //ação
            val result = TipoDeChave.CHAVE_ALEATORIA.valida("chave não nula")
            //validação
            assertFalse(result)
        }

        @Test
        internal fun `deve retornar true quando a chave for nulo`() {
            //ação
            val result = TipoDeChave.CHAVE_ALEATORIA.valida(null)
            //validação
            assertTrue(result)
        }
    }

    @Nested
    inner class CPF {
        @Test
        internal fun `deve retornar false quando a chave nao existir`() {
            //ação
            val result = TipoDeChave.CPF.valida(null)
            //validação
            assertFalse(result)
        }

        @Test
        internal fun `deve retornar false quando a chave nao for um CPF`() {
            //ação
            val result = TipoDeChave.CPF.valida("não é um CPF")
            //validação
            assertFalse(result)
        }

        @Test
        internal fun `deve retornar true quando a chave for um CPF`() {
            //ação
            val result = TipoDeChave.CPF.valida("23852310008")
            //validação
            assertTrue(result)
        }
    }

    @Nested
    inner class TELEFONE {
        @Test
        internal fun `deve retornar false quando a chave for nula`() {
            //ação
            val result = TipoDeChave.TELEFONE.valida(null)
            //validação
            assertFalse(result)
        }

        @Test
        internal fun `deve retornar false quando a chave nao for um telefone`() {
            //ação
            val result = TipoDeChave.TELEFONE.valida("não é um telefone")
            //validação
            assertFalse(result)
        }

        @Test
        internal fun `deve retornar true quando a chave for um telefone`() {
            //ação
            val result = TipoDeChave.TELEFONE.valida("+5585988714077")
            //validação
            assertTrue(result)
        }
    }

    @Nested
    inner class EMAIL {
        @Test
        internal fun `deve retornar false quando a chave for nula`() {
            //ação
            val result = TipoDeChave.EMAIL.valida(null)
            //validação
            assertFalse(result)
        }

        @Test
        internal fun `deve retornar false quando a chave nao for um email`() {
            //ação
            val result = TipoDeChave.EMAIL.valida("não é um email")
            //validação
            assertFalse(result)
        }

        @Test
        internal fun `deve retornar true quando a chave for um email`() {
            //ação
            val result = TipoDeChave.EMAIL.valida("bob@email.com")
            //validação
            assertTrue(result)
        }
    }
}