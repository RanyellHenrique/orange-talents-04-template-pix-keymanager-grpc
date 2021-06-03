package br.com.zup.ranyell.keymanager.pix.registra

import br.com.zup.ranyell.keymanager.compartilhado.validacao.UUIDValid
import br.com.zup.ranyell.keymanager.conta.Conta
import br.com.zup.ranyell.keymanager.compartilhado.validacao.ValidPixKey
import br.com.zup.ranyell.keymanager.conta.TipoDeConta
import br.com.zup.ranyell.keymanager.pix.ChavePix
import br.com.zup.ranyell.keymanager.pix.TipoDeChave
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
data class RegistraChavePix(
    @field:UUIDValid
    @field:NotBlank
    val idCliente: String,
    @field:NotNull
    val tipoDeChave: TipoDeChave?,
    @field:Size(max = 77)
    var chave: String?,
    @field:NotNull
    val tipoDeConta: TipoDeConta?
){

    fun toModel(conta: Conta): ChavePix {
        return ChavePix(chave!!, tipoDeChave!!, conta)
    }
}