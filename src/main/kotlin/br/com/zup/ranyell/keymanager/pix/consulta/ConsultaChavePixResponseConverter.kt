package br.com.zup.ranyell.keymanager.pix.consulta

import br.com.zup.ranyell.keymanager.ConsultaChavePixResponse
import br.com.zup.ranyell.keymanager.Conta
import com.google.protobuf.Timestamp
import java.time.ZoneId

class ConsultaChavePixResponseConverter {

    fun convert(chaveInfo: ChavePixInfo): ConsultaChavePixResponse {
        return ConsultaChavePixResponse.newBuilder()
            .setChave(chaveInfo.chave)
            .setTipoDeChave(chaveInfo.tipo)
            .setClientId(chaveInfo.clienteId ?: "")
            .setPixId(chaveInfo.pixId ?: "")
            .setTitularCpf(chaveInfo.titularCpf)
            .setTitularNome(chaveInfo.titularNome)
            .setConta(
                Conta.newBuilder()
                    .setTipoDeconta(chaveInfo.conta.tipoDeConta)
                    .setNome(chaveInfo.conta.nome)
                    .setAgencia(chaveInfo.conta.agencia)
                    .setNumero(chaveInfo.conta.numero)
                    .build()
            )
            .setRegistradaEm(chaveInfo.registradaEm.let {
                val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                Timestamp.newBuilder()
                    .setSeconds(createdAt.epochSecond)
                    .setNanos(createdAt.nano)
                    .build()
            })
            .build()
    }
}