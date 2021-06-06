package br.com.zup.ranyell.keymanager.pix.lista

import br.com.zup.ranyell.keymanager.ChavePixResponse
import br.com.zup.ranyell.keymanager.TipoDeChave
import br.com.zup.ranyell.keymanager.TipoDeConta
import br.com.zup.ranyell.keymanager.pix.ChavePix
import com.google.protobuf.Timestamp
import java.time.ZoneId

class ChavePixResponseConverter {
    fun toResponse(chave: ChavePix): ChavePixResponse {
        return ChavePixResponse.newBuilder()
            .setPixId(chave.id)
            .setClienteId(chave.conta.titular.id)
            .setTipoDeChave(TipoDeChave.valueOf(chave.tipoDeChave.name))
            .setTipoDeConta(TipoDeConta.valueOf(chave.conta.tipo.name))
            .setChave(chave.chave)
            .setRegistradaEm(chave.registradaEm.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()
    }
}
