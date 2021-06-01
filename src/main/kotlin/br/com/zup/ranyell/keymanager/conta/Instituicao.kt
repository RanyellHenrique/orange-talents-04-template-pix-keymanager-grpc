package br.com.zup.ranyell.keymanager.conta

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank

@Embeddable
class Instituicao(
    @NotBlank
    @Column(name = "instituicao_id")
    val nome: String,
    @NotBlank
    val ispb: String
) {
}