package br.com.zup.ranyell.keymanager.sistemaitau

import br.com.zup.ranyell.keymanager.conta.Instituicao

data class IntituicaoResponse(
    val nome: String,
    val ispb: String
) {

    fun toModel(): Instituicao {
        return Instituicao(nome, ispb)
    }

}
