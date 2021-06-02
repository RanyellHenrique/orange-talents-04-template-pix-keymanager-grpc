package br.com.zup.ranyell.keymanager.pix.remove

import br.com.zup.ranyell.keymanager.compartilhado.validacao.UUIDValid
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RemoveChavePix(
    @field:UUIDValid
    @field:NotBlank
    val idCliente: String,
    @field:UUIDValid
    @field:NotBlank
    val idPix: String
) {
}