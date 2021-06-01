package br.com.zup.ranyell.keymanager.sistemaitau

import br.com.zup.ranyell.keymanager.TipoDeConta as TipoDecontaGRPC
import br.com.zup.ranyell.keymanager.conta.Conta
import br.com.zup.ranyell.keymanager.conta.TipoDeConta

data class ContaResponse(
    val agencia: String,
    val numero: String,
    val titular: TitularResponse,
    val instituicao: IntituicaoResponse,
    val tipo: TipoDecontaGRPC
) {

    fun toModel(): Conta {
        return Conta(agencia, numero, TipoDeConta.valueOf(tipo.name), instituicao.toModel(), titular.toModel())
    }

}