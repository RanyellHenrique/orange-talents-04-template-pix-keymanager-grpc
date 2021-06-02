package br.com.zup.ranyell.keymanager.conta


import javax.persistence.Embeddable
import javax.persistence.Embedded
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
    @Embedded
    val instituicao: Instituicao,
    @NotNull
    @Embedded
    val titular: Titular
) {
}