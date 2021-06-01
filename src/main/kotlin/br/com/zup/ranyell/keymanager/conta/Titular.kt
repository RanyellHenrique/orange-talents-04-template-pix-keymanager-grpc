package br.com.zup.ranyell.keymanager.conta

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank

@Embeddable
class Titular(
    @Column(name="titular_id")
    @NotBlank
    val id: String,
    @Column(name="titular_nome")
    @NotBlank
    val nome: String,
    @NotBlank
    val cpf: String
) {

}
