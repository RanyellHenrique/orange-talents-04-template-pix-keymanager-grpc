package br.com.zup.ranyell.keymanager.pix.lista

import br.com.zup.ranyell.keymanager.ChavePixResponse
import br.com.zup.ranyell.keymanager.conta.TipoDeConta
import br.com.zup.ranyell.keymanager.pix.ChavePix
import br.com.zup.ranyell.keymanager.pix.TipoDeChave
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

data class ListaChavePix(
    val pixId: String,
    val clienteId: String,
    val tipoDeChave: TipoDeChave,
    val tipoDeConta: TipoDeConta,
    val chave: String,
    val registradaEm: LocalDateTime
) {

    constructor(chavePix: ChavePix) : this(
        pixId = chavePix.id!!,
        clienteId = chavePix.conta.titular.id,
        tipoDeChave = chavePix.tipoDeChave,
        tipoDeConta = chavePix.conta.tipo,
        chave = chavePix.chave,
        registradaEm = chavePix.registradaEm
    )

    fun toResponse(): ChavePixResponse {
        return ChavePixResponse.newBuilder()
            .setPixId(pixId)
            .setClienteId(clienteId)
            .setTipoDeChave(br.com.zup.ranyell.keymanager.TipoDeChave.valueOf(tipoDeChave.name))
            .setTipoDeConta(br.com.zup.ranyell.keymanager.TipoDeConta.valueOf(tipoDeConta.name))
            .setChave(chave)
            .setRegistradaEm(registradaEm.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()
    }

}
