package br.com.zup.ranyell.keymanager.pix.registra

import br.com.zup.ranyell.keymanager.conta.Conta
import br.com.zup.ranyell.keymanager.conta.Instituicao
import br.com.zup.ranyell.keymanager.conta.TipoDeConta
import br.com.zup.ranyell.keymanager.conta.Titular
import br.com.zup.ranyell.keymanager.pix.TipoDeChave
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NovaChavePixTest{

    lateinit var conta: Conta

    @BeforeEach
    fun setup() {
       conta = Conta(
            "0001",
            "00023",
            TipoDeConta.CONTA_CORRENTE,
            Instituicao("ITAU","00213"),
            Titular("ae93a61c-0652-43b3-bb8e-a17072295955", "asd", "23852310008")
        )
    }

    @Test
    internal fun `deve gerar uma nova chave quando o tipo for aleatorio`() {
        //cenário
        val novaChavePix = NovaChavePix(
            idCliente = "ae93a61c-0652-43b3-bb8e-a17072295955",
            tipoDeChave = TipoDeChave.CHAVE_ALEATORIA,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            chave = null
        )
        //ação
        val result = novaChavePix.toModel(conta)
        //validação
        assertNotNull(result.chave)
    }

    @Test
    internal fun `deve manter a chave indicada quando o tipo de chave nao for aleatorio`() {
        //cenário
        val novaChavePix = NovaChavePix(
            idCliente = "ae93a61c-0652-43b3-bb8e-a17072295955",
            tipoDeChave = TipoDeChave.EMAIL,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            chave = "email@email.com"
        )
        //ação
        val result = novaChavePix.toModel(conta)
        //validação
        assertEquals(novaChavePix.chave, result.chave)
    }
}