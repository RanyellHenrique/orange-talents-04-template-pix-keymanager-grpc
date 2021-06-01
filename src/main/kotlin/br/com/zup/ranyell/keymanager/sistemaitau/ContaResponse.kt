package br.com.zup.ranyell.keymanager.sistemaitau

import br.com.zup.ranyell.keymanager.TipoDeConta
import br.com.zup.ranyell.keymanager.conta.Conta

data class ContaResponse(
    val agencia: String,
    val numero: String,
    val titular: TitularResponse,
    val instituicao: IntituicaoResponse,
    val tipo: TipoDeConta
) {

    fun toModel(): Conta {
        return Conta(agencia, numero, tipo, instituicao.toModel(), titular.toModel())
    }

}