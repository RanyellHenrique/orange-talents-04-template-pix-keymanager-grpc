package br.com.zup.ranyell.keymanager.pix

import br.com.zup.ranyell.keymanager.NovaChavePixRequest
import br.com.zup.ranyell.keymanager.conta.TipoDeConta
import br.com.zup.ranyell.keymanager.pix.registra.NovaChavePix
import br.com.zup.ranyell.keymanager.TipoDeChave as TipoDeChaveGRPC
import br.com.zup.ranyell.keymanager.TipoDeConta as TipoDeContaGRPC

fun NovaChavePixRequest.toModel(): NovaChavePix {
    return NovaChavePix(
        idCliente = idCliente,
        tipoDeChave = when (tipoDeChave) {
            TipoDeChaveGRPC.UNKNOWN_TIPO_CHAVE -> null
            else -> TipoDeChave.valueOf(tipoDeChave.name)
        },
        chave = chave,
        tipoDeConta = when (tipoDeConta) {
            TipoDeContaGRPC.UNKNOWN_TIPO_CONTA -> null
            else -> TipoDeConta.valueOf(tipoDeConta.name)
        }
    )
}