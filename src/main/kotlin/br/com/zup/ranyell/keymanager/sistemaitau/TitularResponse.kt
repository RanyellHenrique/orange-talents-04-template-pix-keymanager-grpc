package br.com.zup.ranyell.keymanager.sistemaitau

import br.com.zup.ranyell.keymanager.conta.Titular

data class TitularResponse(
    val id: String,
    val nome: String,
    val cpf: String
) {

    fun toModel(): Titular {
        return Titular(id, nome, cpf)
    }

}
