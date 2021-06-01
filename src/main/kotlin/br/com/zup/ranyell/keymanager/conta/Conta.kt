package br.com.zup.ranyell.keymanager.conta

import br.com.zup.ranyell.keymanager.TipoDeConta
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Embeddable
class Conta(
    @NotBlank
    val agencia: String,
    @NotBlank
    val numero: String,
    @NotNull
    @Enumerated(EnumType.STRING)
    val tipo: TipoDeConta,
    @NotNull
    val instituicao: Instituicao,
    @NotNull
    val titular: Titular
) {
}