package br.com.zup.ranyell.keymanager.pix

import br.com.zup.ranyell.keymanager.NovaChavePixRequest
import br.com.zup.ranyell.keymanager.TipoDeConta
import br.com.zup.ranyell.keymanager.conta.Conta

import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.functions.isValid
import org.valiktor.validate
import java.util.*

class PixRequest(
    val idCliente: String,
    val tipoDeChave: TipoDeChave,
    var chave: String,
    val tipoDeConta: TipoDeConta
){
    constructor(novaChavePixRequest: NovaChavePixRequest): this(
        idCliente = novaChavePixRequest.idCliente,
        tipoDeChave = TipoDeChave.valueOf(novaChavePixRequest.tipoDeChave.name),
        chave = novaChavePixRequest.chave,
        tipoDeConta = novaChavePixRequest.tipoDeConta
    )

    init {
        validate(this) {
            validate(PixRequest::idCliente).isNotBlank()
            validate(PixRequest::tipoDeChave).isNotNull()
            validate(PixRequest::tipoDeConta).isNotNull()
            validate(PixRequest::chave).isValid {
                chave.matches(tipoDeChave.validacao.toRegex())
            }
        }
    }

    fun toModel(conta: Conta): Pix {
        if(tipoDeChave == TipoDeChave.CHAVE_ALEATORIA) {
            this.chave = UUID.randomUUID().toString()
        }
        return Pix(chave, tipoDeChave, conta)
    }
}